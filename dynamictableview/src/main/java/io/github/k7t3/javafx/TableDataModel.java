package io.github.k7t3.javafx;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

class TableDataModel<T> {

    ObjectProperty<SortedList<T>> sortedProperty = new SimpleObjectProperty<>();

    SortedList<T> getSorted() {
        return sortedProperty.get();
    }

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
        int rowCount = (int)Math.ceil(getSorted().size() / (double)columnCount);

        normalizeRowCount(rowCount);
    }

    T get(int rowIndex, int columnIndex) {
        int count = getSorted().size();
        int columnCount = columnCountProperty.get();

        int skip = rowIndex * columnCount;
        int index = skip + columnIndex;

        if (count - 1 < index) {
            return null;
        }

        return getSorted().get(index);
    }

}