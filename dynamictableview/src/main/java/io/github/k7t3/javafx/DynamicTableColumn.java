package io.github.k7t3.javafx;

import javafx.beans.property.*;
import javafx.scene.control.TableColumn;

import java.util.function.BiFunction;

class DynamicTableColumn<T> extends TableColumn<TableDataRowModel<T>, T> {

    protected final DynamicTableView<T> control;

    int columnIndex;

    final DoubleProperty columnSizeProperty;

    DynamicTableColumn(DynamicTableView<T> control, int index) {
        super();
        this.control = control;
        this.columnIndex = index;
        this.columnSizeProperty = new SimpleDoubleProperty();

        if (control.getCellFactory() != null)
            init(control.getCellFactory());
        else
            init(DefaultDynamicTableCell::new);
    }

    private void init(BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>> factory) {
        this.setCellValueFactory(new DynamicTableCellValueFactory<>());
        this.setCellFactory(column -> factory.apply(columnIndex, columnSizeProperty));
    }

}