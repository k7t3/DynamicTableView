package com.k7t3.javafx.control;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TableCell;

/**
 * <p>{@link DynamicTableView}に表示するセルクラスです。</p>
 * <p>内部変数に列インデックスを表す{@link DynamicTableCell#columnIndex},
 * 列の横幅を表す{@link DynamicTableCell#columnWidthProperty}があります。</p>
 * @param <T> 表示するデータオブジェクト
 */
public abstract class DynamicTableCell<T> extends TableCell<TableDataRowModel<T>, T> {

    protected final int columnIndex;
    protected final ReadOnlyDoubleProperty columnWidthProperty;

    public DynamicTableCell(int index, ReadOnlyDoubleProperty columnWidthProperty) {
        super();
        this.columnIndex = index;
        this.columnWidthProperty = columnWidthProperty;
        prefWidthProperty().bind(columnWidthProperty);
        prefHeightProperty().bind(columnWidthProperty);
    }

    @Override
    public abstract void updateItem(T item, boolean empty);
}