package io.github.k7t3.javafx;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

class DynamicTableCellValueFactory<T> implements Callback<TableColumn.CellDataFeatures<TableDataRowModel<T> , T>, ObservableValue<T>> {

    private static final System.Logger LOGGER = System.getLogger(DynamicTableCellValueFactory.class.getName());

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<TableDataRowModel<T>, T> param) {
        TableDataRowModel<T> rowData = param.getValue();
        TableColumn<TableDataRowModel<T>, T> column = param.getTableColumn();

        if (column instanceof DynamicTableColumn) {
            int columnIndex = ((DynamicTableColumn<T>)column).columnIndex;
            return rowData.getProperty(columnIndex);
        }

        LOGGER.log(System.Logger.Level.WARNING, "unknown type column");

        return new ReadOnlyObjectWrapper<>();
    }

}