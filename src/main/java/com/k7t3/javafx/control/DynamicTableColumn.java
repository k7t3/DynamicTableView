package com.k7t3.javafx.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableColumn;

class DynamicTableColumn<T> extends TableColumn<TableDataRowModel<T>, T> {

    private final DynamicTableView<T> control;

    int columnIndex;

    final DoubleProperty columnSizeProperty;

    DynamicTableColumn(DynamicTableView<T> control, int index) {
        super();
        this.control = control;
        this.columnIndex = index;
        this.columnSizeProperty = new SimpleDoubleProperty();
        init();
    }

    private void init() {
        this.setCellValueFactory(new DynamicTableCellValueFactory<>());
        this.setCellFactory(column -> control.getCellFactory().apply(columnIndex, columnSizeProperty));
    }

}