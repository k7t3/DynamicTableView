package com.k7t3.javafx.control;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.logging.Logger;

class DynamicTableCellValueFactory<T> implements Callback<TableColumn.CellDataFeatures<TableDataRowModel<T> , T>, ObservableValue<T>> {

    private static final Logger LOGGER = Logger.getLogger(DynamicTableCellValueFactory.class.getName());

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<TableDataRowModel<T>, T> param) {
        TableDataRowModel<T> rowData = param.getValue();
        TableColumn<TableDataRowModel<T>, T> column = param.getTableColumn();

        if (column instanceof DynamicTableColumn) {
            int columnIndex = ((DynamicTableColumn<T>)column).columnIndex;
            return rowData.getProperty(columnIndex);
        }

        LOGGER.warning("unknown type column");

        return new ReadOnlyObjectWrapper<>();
    }

}