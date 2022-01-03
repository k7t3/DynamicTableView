package com.k7t3.javafx.control;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import java.util.List;
import java.util.logging.Logger;

class DynamicTableViewController<T> {

    private static final Logger logger = Logger.getLogger(DynamicTableViewController.class.getName());

    private static final double DEFAULT_CELL_SIZE = 200d;

    private static final double DEFAULT_CELL_SIZE_MULTIPLY = 1d;

    final DoubleProperty requestCellSizeProperty = new SimpleDoubleProperty(DEFAULT_CELL_SIZE);

    final DoubleProperty cellSizeMultiply = new SimpleDoubleProperty(DEFAULT_CELL_SIZE_MULTIPLY);

    final DoubleBinding cellSizeProperty = requestCellSizeProperty.multiply(cellSizeMultiply);

    final IntegerProperty columnCountProperty = new SimpleIntegerProperty();

    final ObjectProperty<T> selectedProperty = new SimpleObjectProperty<>();

    final ObservableList<T> selectedItemsProperty = FXCollections.observableArrayList();

    final ReadOnlyIntegerWrapper pictureCountProperty = new ReadOnlyIntegerWrapper();

    final ReadOnlyIntegerWrapper selectedCountProperty = new ReadOnlyIntegerWrapper();

    final TableDataModel<T> dataModel = new TableDataModel<T>();

    private final DynamicTableView<T> control;

    DynamicTableViewController(DynamicTableView<T> control) {
        this.control = control;
        init();
    }

    private class DelayedUpdateColumnCountService extends Service<Void> {
        private double width;

        public void restart(double width) {
            this.width = width;
            restart();
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    long milli = 200;
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(milli / 10);
                        } catch (InterruptedException e) {
                            return null;
                        }
                        if (isCancelled()) {
                            return null;
                        }
                    }
                    return null;
                }
            };
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            updateColumnCount(width);
            control.refresh();
        }
    }

    private void init() {
        logger.fine("init instance.");

        control.setPlaceholder(new Label("画像がありません"));
        control.getSelectionModel().setCellSelectionEnabled(true);
        control.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        dataModel.columnCountProperty.bind(columnCountProperty);
        control.setItems(dataModel.rows);

        getSortedList().addListener((ListChangeListener<T>) c -> {
            while (c.next()) {
                if (c.wasUpdated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        // updated
                    }
                } else if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        // permutated
                    }
                } else {
                    if (c.wasAdded()) {
                        pictureCountProperty.set(c.getList().size());
                        if (!columnCountChanged) {
                            dataModel.normalizeRows();
                        }
                    } else if (c.wasRemoved()) {
                        pictureCountProperty.set(c.getList().size());
                        if (!columnCountChanged) {
                            dataModel.normalizeRows();
                        }
                        selectedItemsProperty.removeAll(c.getRemoved());
                    }
                }
            }
        });


        control.widthProperty().addListener((p, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(n.doubleValue());
            }
        });
//        DelayedLayoutUpdateService service = new DelayedLayoutUpdateService();
//        control.widthProperty().addListener((p, o, n) -> {
//            if (n != null && !o.equals(n)) {
//                service.restart(n.doubleValue());
//            }
//        });

        cellSizeProperty.addListener((ob, o, n) -> {
            if (n != null && !o.equals(n)) {
                updateColumnCount(control.getWidth());
                control.refresh();
            }
        });

        columnCountProperty.addListener((p, o, n) -> {
            normalizeColumnCount();
        });

        //noinspection rawtypes
        control.getSelectionModel().getSelectedCells().addListener((ListChangeListener<? super TablePosition>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {

                    List<? extends TablePosition> changed = c.getRemoved();

                    for (int i = changed.size() - 1; -1 < i; i--) {
                        TablePosition position = changed.get(i);
                        int row = position.getRow();
                        int column = position.getColumn();
                        T picture = dataModel.get(row, column);
                        if (picture != null) {
                            selectedItemsProperty.remove(picture);
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
                            selectedItemsProperty.add(picture);
                        }
                    }

                }
            }
        });

        selectedItemsProperty.addListener((ListChangeListener<? super T>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    selectedCountProperty.set(c.getList().size());

                    List<? extends T> list = c.getList();
                    if (list.isEmpty()) {
                        selectedProperty.set(null);
                    } else {
                        T picture = list.get(list.size() - 1);
                        selectedProperty.set(picture);
                    }

                }
                if (c.wasAdded()) {
                    selectedCountProperty.set(c.getList().size());

                    List<? extends T> list = c.getAddedSubList();
                    T picture = list.get(list.size() - 1);
                    selectedProperty.set(picture);

                }
            }
        });
    }

    private boolean columnCountChanged = false;

    private void updateColumnCount(double viewWidth) {
        int columnCount = Math.max(1, (int)(viewWidth / cellSizeProperty.doubleValue()));
        columnCountProperty.set(columnCount);
    }

    private void normalizeColumnCount() {
        int currentCount = control.getColumns().size();
        int count = columnCountProperty.get();

        if (currentCount == count) return;

        columnCountChanged = true;
        control.getSelectionModel().clearSelection();

        if (count < currentCount) {
            for (int i = 0; i < currentCount - count; i++) {
                control.getColumns().remove(currentCount - i - 1);
            }
        } else {

            for (int i = currentCount; i < count; i++) {

                DynamicTableColumn<T> column = new DynamicTableColumn<>(control, i);
                column.columnSizeProperty.bind(cellSizeProperty);
                control.getColumns().add(column);

            }

        }

        dataModel.normalizeRows();

        columnCountChanged = false;
    }

    public ObservableList<T> getContentItems() {
        return dataModel.values;
    }

    public FilteredList<T> getFilteredList() {
        return dataModel.filtered;
    }

    public SortedList<T> getSortedList() {
        return dataModel.sorted;
    }
}
