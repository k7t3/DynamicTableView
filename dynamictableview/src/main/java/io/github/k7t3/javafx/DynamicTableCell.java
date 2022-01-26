package io.github.k7t3.javafx;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.TableCell;

/**
 * <p>{@link DynamicTableView}に表示するセルクラスです。</p>
 * <p>内部変数に列インデックスを表す{@link DynamicTableCell#columnIndex},
 * 列の横幅を表す{@link DynamicTableCell#cellSizeProperty}があります。</p>
 * @param <T> 表示するデータオブジェクト
 */
public abstract class DynamicTableCell<T> extends TableCell<TableDataRowModel<T>, T> {

    /**
     * このセルが何列目に該当するかを示す列番号です。
     */
    protected final int columnIndex;

    /**
     * このセルの一辺のサイズを持つプロパティです。
     */
    protected final ReadOnlyDoubleProperty cellSizeProperty;

    public DynamicTableCell(int index, ReadOnlyDoubleProperty cellSizeProperty) {
        super();
        this.columnIndex = index;
        this.cellSizeProperty = cellSizeProperty;
        prefWidthProperty().bind(cellSizeProperty);
        prefHeightProperty().bind(cellSizeProperty);
    }

    private Node view;

    /**
     * このセルに表示するビューを作成します。
     * @return このセルに表示するビュー
     */
    protected abstract Node createView();

    /**
     * セルに表示されるアイテムが更新されるときに呼び出されます。
     * @param item 表示するアイテム
     */
    protected abstract void updateItem(T item);

    /**
     * セルに割り当てられるアイテムがnullのときに呼び出されます。
     * 規定では何も処理しません。
     */
    protected void onEmpty() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
            onEmpty();
            return;
        }

        if (view == null) {
            view = createView();
        }

        updateItem(item);

        setGraphic(view);
    }
}