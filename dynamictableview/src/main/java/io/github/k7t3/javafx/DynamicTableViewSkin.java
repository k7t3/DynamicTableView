package io.github.k7t3.javafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class DynamicTableViewSkin<T> extends SkinBase<DynamicTableView<T>> {

    private static final System.Logger LOGGER = System.getLogger(DynamicTableViewSkin.class.getName());

    private final IntegerProperty columnCountProperty = new SimpleIntegerProperty();

    private final TableDataModel<T> dataModel;

    private final TableView<TableDataRowModel<T>> tableView;

    private final DynamicTableView<T> control;

    public DynamicTableViewSkin(DynamicTableView<T> control) {
        super(control);
        this.control = getSkinnable();
        this.tableView = new TableView<>();
        dataModel = new TableDataModel<>(columnCountProperty, control.sortedItemsProperty);
        init();
    }

    /**
     * コントロールで管理される項目が変更されたときのリスナ
     * @param c 変更
     */
    private void itemsChangeListener(ListChangeListener.Change<? extends T> c) {
        while (c.next()) {
            if (c.wasUpdated()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    // TODO updated
                }
            } else if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    // TODO permutated
                }
            } else {
                if (c.wasAdded()) {

                    if (!changingColumnCount) {

                        dataModel.normalizeRows();

                        // 追加された要素を含む行を更新する
                        var indices = IntStream.range(c.getFrom(), c.getTo()).boxed().collect(Collectors.toList());
                        dataModel.updateRowContainsItem(indices);

                    }

                } else if (c.wasRemoved()) {

                    if (!changingColumnCount) {

                        dataModel.normalizeRows();

                        dataModel.updateRowIndexAfter(c.getFrom());

                    }

                    // 選択リストに含まれていた場合は削除する
                    control.getSelectionModel().selectedItems.removeAll(c.getRemoved());
                }
            }
        }
    }

    private void init() {
        LOGGER.log(System.Logger.Level.DEBUG, "init instance");

        control.getSelectionModel().inject(tableView, dataModel);

        tableView.setItems(dataModel.getRows());
        getChildren().add(tableView);

        // TableViewのPlaceHolderプロパティ
        tableView.placeholderProperty().bind(control.placeHolderProperty());

        // 複数選択したセルをクリックして解除する時に発生するExceptionはOpenJFX18で修正されるらしい
        // https://bugs.openjdk.java.net/browse/JDK-8273324
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // アイテムリストが更新されたらFilterとSortedも更新するリスナを追加
        control.itemsProperty().addListener((ob, o, n) -> {
            if (n == null) {
                control.filteredItemsProperty.set(null);
                control.sortedItemsProperty.set(null);
            } else {
                control.filteredItemsProperty.set(new FilteredList<>(n));
                control.sortedItemsProperty.set(new SortedList<>(control.getFilteredItems()));
            }
        });

        // Sortedリストにリスナを追加
        ChangeListener<? super Comparator<? super T>> comparatorListener = (ob, o, n) -> {
            control.getSelectionModel().clearSelection();
            tableView.refresh();
        };
        control.sortedItemsProperty().addListener((ob, o, n) -> {
            if (o != null) {
                o.removeListener(this::itemsChangeListener);
            }

            if (n != null) {
                n.addListener(this::itemsChangeListener);
                n.comparatorProperty().addListener(comparatorListener);
            }
        });
        control.getSortedItems().addListener(this::itemsChangeListener);
        control.getSortedItems().comparatorProperty().addListener(comparatorListener);

        // Filterリストにリスナを追加
        ChangeListener<? super Predicate<? super T>> predicateListener = (ob, o, n) -> {
            control.getSelectionModel().clearSelection();
            tableView.refresh();
        };
        control.filteredItemsProperty().addListener((ob, o, n) -> {
            if (o != null) {
                o.predicateProperty().removeListener(predicateListener);
            }

            if (n != null) {
                n.predicateProperty().addListener(predicateListener);
            }
        });
        control.getFilteredItems().predicateProperty().addListener(predicateListener);


        control.widthProperty().addListener((p, o, n) -> {
            if (n != null && !o.equals(n)) {
                calculateColumnCount(n.doubleValue());
            }
        });

        control.cellWidthProperty().addListener((ob, o, n) -> {
            if (n != null && !o.equals(n)) {
                calculateColumnCount(control.getWidth());
            }
        });

        // 列数プロパティが変更されたら画面の列数を最適化
        columnCountProperty.addListener((p, o, n) -> normalizeColumnCount());
    }

    /**
     * 現在の画面サイズとセルのサイズを考慮して列数を更新する。
     * @param viewWidth 画面サイズ
     */
    private void calculateColumnCount(double viewWidth) {
        int columnCount = Math.max(1, (int)(viewWidth / control.getCellWidth()));
        columnCountProperty.set(columnCount);
    }

    private boolean changingColumnCount = false;

    private void normalizeColumnCount() {
        int currentCount = tableView.getColumns().size();
        int count = columnCountProperty.get();

        if (currentCount == count) return;

        changingColumnCount = true;
        control.getSelectionModel().clearSelection();

        if (count < currentCount) {
            for (int i = 0; i < currentCount - count; i++) {
                tableView.getColumns().remove(currentCount - i - 1);
            }
        } else {

            for (int i = currentCount; i < count; i++) {

                DynamicTableColumn<T> column = new DynamicTableColumn<>(control, i);
                tableView.getColumns().add(column);

            }

        }

        // 列数の変更に伴って行数も更新する
        dataModel.normalizeRows();

        // すべての行を更新する
        dataModel.getRows().forEach(TableDataRowModel::update);

        changingColumnCount = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        dataModel.getRows().forEach(TableDataRowModel::clear);
        dataModel.getRows().clear();
    }
}
