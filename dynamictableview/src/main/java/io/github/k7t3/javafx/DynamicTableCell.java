package io.github.k7t3.javafx;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.Node;
import javafx.scene.control.TableCell;

/**
 * <p>{@link DynamicTableView}に表示するセルクラスです。</p>
 * <p>列の横幅を表す{@link DynamicTableCell#cellSizeProperty()}があります。</p>
 * @param <T> 表示するデータオブジェクト
 */
public abstract class DynamicTableCell<T> extends TableCell<TableDataRowModel<T>, T> {

    private static final String DEFAULT_STYLE_CLASS = "dynamic-table-cell";

    public DynamicTableCell() {
        super();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    /**
     * このセルの一辺のサイズを持つプロパティです。
     */
    private ReadOnlyDoubleWrapper cellSize;

    public double getCellSize() {
        if (cellSize == null) {
            return 0d;
        }
        return cellSize.get();
    }

    ReadOnlyDoubleWrapper cellSizeWrapper() {
        if (cellSize == null) {
            cellSize = new ReadOnlyDoubleWrapper();
            prefWidthProperty().bind(cellSize);
            prefHeightProperty().bind(cellSize);
        }
        return cellSize;
    }

    public ReadOnlyDoubleProperty cellSizeProperty() {
        return cellSizeWrapper().getReadOnlyProperty();
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