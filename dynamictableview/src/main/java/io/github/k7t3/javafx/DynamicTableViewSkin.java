package io.github.k7t3.javafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

        // ????????????????????????????????????????????????
        control.clearSelectionAction = () -> {
            tableView.getSelectionModel().clearSelection();
            tableView.getFocusModel().focus(0, tableView.getVisibleLeafColumn(0));
        };

        tableView.placeholderProperty().bind(control.placeHolderProperty());

        // ???????????????????????????????????????????????????????????????????????????Exception???OpenJFX18???????????????????????????
        // https://bugs.openjdk.java.net/browse/JDK-8273324
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // ??????????????????????????????????????????Filter???Sorted?????????????????????????????????
        control.itemsProperty().addListener((ob, o, n) -> {
            if (n == null) {
                control.filteredItemsProperty.set(null);
                control.sortedItemsProperty.set(null);
            } else {
                control.filteredItemsProperty.set(new FilteredList<>(n));
                control.sortedItemsProperty.set(new SortedList<>(control.filteredItemsProperty.get()));
            }
        });
        // Sorted?????????????????????????????????????????????????????????????????????????????????????????????
        control.sortedItemsProperty().addListener((ob, o, n) -> {
            if (o != null) {
                // ????????????????????????
                o.removeListener(this::itemsChangeListener);
            }

            if (n != null) {
                // ????????????????????????
                n.addListener(this::itemsChangeListener);
                // Comparator???????????????????????????TableView?????????????????????????????????????????????
                n.comparatorProperty().addListener((observable, oldValue, newValue) -> tableView.refresh());
            }
        });
        // Sorted?????????????????????????????????????????????????????????????????????????????????????????????
        control.filteredItemsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Filter???Predicate???????????????????????????TableView???????????????????????????
                newValue.predicateProperty().addListener((observable1, oldValue1, newValue1) -> {
                    tableView.refresh();
                });
            }
        });
        // ??????Sorted?????????????????????????????????????????????
        control.getSortedItems().addListener(this::itemsChangeListener);
        // ??????Sorted????????????Comparator???????????????????????????TableView?????????????????????????????????????????????
        control.getSortedItems().comparatorProperty().addListener((ob, o, n) -> tableView.refresh());

        dataModel.sortedProperty.bind(control.sortedItemsProperty());
        dataModel.columnCountProperty.bind(columnCountProperty);
        tableView.setItems(dataModel.rows);


        control.widthProperty().addListener((p, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(n.doubleValue());
            }
        });

        control.cellSizeProperty().addListener((ob, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(control.getWidth());
                tableView.refresh();
            }
        });

        columnCountProperty.addListener((p, o, n) -> {
            normalizeColumnCount();
        });

        // TableView????????????????????????????????????selectedItems?????????
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

        // selectedItems??????????????????????????????????????????selected??????????????????
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
        int columnCount = Math.max(1, (int)(viewWidth / control.getCellSize()));
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
                column.columnSizeProperty.bind(control.cellSizeProperty());
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
