package com.k7t3.javafx.control;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

class TableDataModel<T> {

    ObservableList<T> values = FXCollections.observableArrayList();

    FilteredList<T> filtered = new FilteredList<>(values);

    SortedList<T> sorted = new SortedList<>(filtered);

    ObservableList<TableDataRowModel<T>> rows = FXCollections.observableArrayList();

    IntegerProperty columnCountProperty = new SimpleIntegerProperty();

    private void normalizeRowCount(int rowCount) {
        int currentRowCount = rows.size();

        if (rowCount < currentRowCount) {
            for (int i = 0; i < currentRowCount - rowCount; i++) {
                rows.remove(currentRowCount - i - 1);
            }
        } else if (currentRowCount < rowCount) {
            for (int i = currentRowCount; i < rowCount; i++) {
                rows.add(new TableDataRowModel<>(i, this));
            }
        } else {
            // TODO 該当する行だけを更新するようにする。
            rows.forEach(TableDataRowModel::update);
        }
    }

    void normalizeRows() {
        int columnCount = columnCountProperty.get();
        int rowCount = (int)Math.ceil(sorted.size() / (double)columnCount);

        normalizeRowCount(rowCount);
    }

    T get(int rowIndex, int columnIndex) {
        int count = sorted.size();
        int columnCount = columnCountProperty.get();

        int skip = rowIndex * columnCount;
        int index = skip + columnIndex;

        if (count - 1 < index) {
            return null;
        }

        return sorted.get(index);
    }

}