package io.github.k7t3.javafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Collection;
import java.util.HashSet;

class TableDataModel<T> {

    private final ObservableList<TableDataRowModel<T>> rows = FXCollections.observableArrayList();

    public TableDataModel() {
    }

    private static final int DEFAULT_COLUMN_COUNT = 0;

    private IntegerProperty columnCount;

    public IntegerProperty columnCountProperty() {
        if (columnCount == null) {
            columnCount = new SimpleIntegerProperty(DEFAULT_COLUMN_COUNT);
        }
        return columnCount;
    }

    public int getColumnCount() {
        if (columnCount == null) {
            return DEFAULT_COLUMN_COUNT;
        }
        return columnCount.get();
    }

    public int getRowCount() {
        return rows.size();
    }

    public ObservableList<TableDataRowModel<T>> getRows() {
        return rows;
    }

    private ObjectProperty<SortedList<T>> sortedItems;

    public ObjectProperty<SortedList<T>> sortedItemsProperty() {
        if (sortedItems == null) {
            sortedItems = new SimpleObjectProperty<>();
        }
        return sortedItems;
    }

    public SortedList<T> getItems() {
        return sortedItemsProperty().get();
    }

    /**
     * 指定の要素インデックスを含む行を含んだすべての行を更新する
     * @param itemIndex 更新を開始する要素インデックス
     */
    void updateRowIndexAfter(int itemIndex) {
        var startRow = calculateRowIndex(itemIndex);

        for (int i = startRow; i < rows.size(); i++) {
            var row = rows.get(i);
            row.update();
        }
    }

    /**
     * 引数の要素インデックスを含んだ行すべてを更新する。
     * @param itemIndices 更新する要素インデックス
     */
    void updateRowContainsItem(Collection<Integer> itemIndices) {
        var updatedRows = new HashSet<Integer>(rows.size());

        for (var itemIndex : itemIndices) {

            var rowIndex = calculateRowIndex(itemIndex);

            var row = rows.get(rowIndex);

            if (!updatedRows.contains(rowIndex)) {
                row.update();
                updatedRows.add(rowIndex);
            }
        }
    }

    private int calculateRowIndex(int itemIndex) {
        var columnCount = this.columnCount.get();
        if (columnCount < 1) {
            throw new IllegalStateException("no column");
        }

        return itemIndex / columnCount;
    }

    void normalizeRows() {
        int columnCount = this.columnCount.get();
        int rowCount = (int)Math.ceil(getItems().size() / (double)columnCount);

        normalizeRowCount(rowCount);
    }

    private void normalizeRowCount(int rowCount) {
        int currentRowCount = rows.size();

        if (rowCount < currentRowCount) {
            // 行数が超過している場合、超過分を削除
            for (int i = 0; i < currentRowCount - rowCount; i++) {
                var row = rows.get(currentRowCount - i - 1);
                row.clear();
                rows.remove(row);
            }

        } else if (currentRowCount < rowCount) {
            // 行数が不足している場合、不足分を追加
            for (int i = currentRowCount; i < rowCount; i++) {
                rows.add(new TableDataRowModel<>(i, this));
            }

        }
    }

    T get(int rowIndex, int columnIndex) {
        int count = getItems().size();
        int columnCount = this.columnCount.get();

        int skip = rowIndex * columnCount;
        int index = skip + columnIndex;

        if (count - 1 < index) {
            return null;
        }

        return getItems().get(index);
    }

}