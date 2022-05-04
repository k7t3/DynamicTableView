package io.github.k7t3.javafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventType;
import javafx.scene.control.*;

import java.util.List;

class DynamicTableViewSkin<T> extends SkinBase<DynamicTableView<T>> {

    private static final System.Logger LOGGER = System.getLogger(DynamicTableViewSkin.class.getName());

    private final IntegerProperty columnCountProperty = new SimpleIntegerProperty();

//    private final ReadOnlyIntegerWrapper selectedCountProperty = new ReadOnlyIntegerWrapper();

    private final TableDataModel<T> dataModel = new TableDataModel<>();

    private final TableView<TableDataRowModel<T>> tableView;

    private final DynamicTableView<T> control;

    public DynamicTableViewSkin(DynamicTableView<T> control) {
        super(control);
        this.control = getSkinnable();
        this.tableView = new TableView<>();
        init();
    }

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
                    if (!columnCountChanged) {
                        dataModel.normalizeRows();
                    }
                } else if (c.wasRemoved()) {
                    if (!columnCountChanged) {
                        dataModel.normalizeRows();
                    }
                    control.selectedItems.removeAll(c.getRemoved());
                }
            }
        }
    }

    private void init() {
        LOGGER.log(System.Logger.Level.DEBUG, "init instance");

        getChildren().add(tableView);

        // 選択項目のクリアアクションを定義
        control.clearSelectionAction = () -> {
            tableView.getSelectionModel().clearSelection();
            tableView.getFocusModel().focus(0, tableView.getVisibleLeafColumn(0));
        };

        tableView.placeholderProperty().bind(control.placeHolderProperty());
        tableView.addEventHandler(EventType.ROOT, event -> {
            // TableView内で完結したイベントをDynamicTableViewコントロールへ移送する
            if (event.isConsumed() && event.getSource() == tableView) {
                control.fireEvent(event.copyFor(tableView, control));
            }
        });

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
                control.sortedItemsProperty.set(new SortedList<>(control.filteredItemsProperty.get()));
            }
        });
        // Sortedリストが更新されたらときに各種実施する処理を書いたリスナを追加
        control.sortedItemsProperty().addListener((ob, o, n) -> {
            if (o != null) {
                // 監視リスナを削除
                o.removeListener(this::itemsChangeListener);
            }

            if (n != null) {
                // 監視リスナを追加
                n.addListener(this::itemsChangeListener);
                // Comparatorが更新されたときにTableViewをリフレッシュするリスナを追加
                n.comparatorProperty().addListener((observable, oldValue, newValue) -> tableView.refresh());
            }
        });
        // Sortedリストが更新されたらときに各種実施する処理を書いたリスナを追加
        control.filteredItemsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Filter用Predicateが更新されたときにTableViewをリフレッシュする
                newValue.predicateProperty().addListener((observable1, oldValue1, newValue1) -> {
                    tableView.refresh();
                });
            }
        });
        // 初期Sortedリストに一覧更新用リスナを追加
        control.getSortedItems().addListener(this::itemsChangeListener);
        // 初期SortedリストにComparatorが更新されたときにTableViewをリフレッシュするリスナを追加
        control.getSortedItems().comparatorProperty().addListener((ob, o, n) -> tableView.refresh());

        dataModel.sortedProperty.bind(control.sortedItemsProperty());
        dataModel.columnCountProperty.bind(columnCountProperty);
        tableView.setItems(dataModel.rows);


        control.widthProperty().addListener((p, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(n.doubleValue());
            }
        });

        control.columnWidthProperty().addListener((ob, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(control.getWidth());
                tableView.refresh();
            }
        });

        columnCountProperty.addListener((p, o, n) -> {
            normalizeColumnCount();
        });

        // TableViewの選択しているアイテムをselectedItemsに流す
        //noinspection rawtypes
        tableView.getSelectionModel().getSelectedCells().addListener((ListChangeListener<? super TablePosition>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {

                    List<? extends TablePosition> changed = c.getRemoved();

                    for (int i = changed.size() - 1; -1 < i; i--) {
                        TablePosition position = changed.get(i);
                        int row = position.getRow();
                        int column = position.getColumn();
                        T picture = dataModel.get(row, column);
                        if (picture != null) {
                            control.selectedItems.remove(picture);
                        }
                    }
                }
                if (c.wasAdded()) {

                    List<? extends TablePosition> list = c.getAddedSubList();

                    for (int i = list.size() - 1; -1 < i; i--) {
                        TablePosition position = list.get(i);
                        int row = position.getRow();
                        int column = position.getColumn();
                        T picture = dataModel.get(row, column);
                        if (picture != null) {
                            control.selectedItems.add(picture);
                        }
                    }

                }
            }
        });

        // selectedItemsのうち、最新の選択アイテムをselectedにセットする
        control.selectedItems.addListener((ListChangeListener<? super T>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    List<? extends T> list = c.getList();
                    if (list.isEmpty()) {
                        control.selectedItemProperty.set(null);
                    } else {
                        T picture = list.get(list.size() - 1);
                        control.selectedItemProperty.set(picture);
                    }

                }
                if (c.wasAdded()) {
                    List<? extends T> list = c.getAddedSubList();
                    T picture = list.get(list.size() - 1);
                    control.selectedItemProperty.set(picture);

                }
            }
        });
    }

    private boolean columnCountChanged = false;

    private void updateColumnCount(double viewWidth) {
        int columnCount = Math.max(1, (int)(viewWidth / control.getColumnWidth()));
        columnCountProperty.set(columnCount);
    }

    private void normalizeColumnCount() {
        int currentCount = tableView.getColumns().size();
        int count = columnCountProperty.get();

        if (currentCount == count) return;

        columnCountChanged = true;
        tableView.getSelectionModel().clearSelection();

        if (count < currentCount) {
            for (int i = 0; i < currentCount - count; i++) {
                tableView.getColumns().remove(currentCount - i - 1);
            }
        } else {

            for (int i = currentCount; i < count; i++) {

                DynamicTableColumn<T> column = new DynamicTableColumn<>(control, i);
                column.columnSizeProperty.bind(control.columnWidthProperty());
                tableView.getColumns().add(column);

            }

        }

        dataModel.normalizeRows();

        columnCountChanged = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        dataModel.rows.clear();
    }
}
