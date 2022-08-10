package io.github.k7t3.javafx;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.Node;
import javafx.scene.control.TableCell;

/**
 * <p>{@link DynamicTableView}に表示するセルクラスです。</p>
 * @param <T> 表示するデータオブジェクト
 */
public abstract class DynamicTableCell<T> extends TableCell<TableDataRowModel<T>, T> {

    private static final String DEFAULT_STYLE_CLASS = "dynamic-table-cell";

    public DynamicTableCell() {
        super();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    private ReadOnlyDoubleWrapper prefCellWidth;

    public double getPrefCellWidth() {
        if (prefCellWidth == null) {
            return USE_COMPUTED_SIZE;
        }
        return prefCellWidth.get();
    }
    
    ReadOnlyDoubleWrapper prefCellWidthWrapper() {
        if (prefCellWidth == null) {
            prefCellWidth = new ReadOnlyDoubleWrapper(USE_COMPUTED_SIZE);
            prefWidthProperty().bindBidirectional(prefCellWidth);
        }
        return prefCellWidth;
    }
    
    public ReadOnlyDoubleProperty prefCellWidthProperty() {
        return prefCellWidthWrapper().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper prefCellHeight;

    public double getPrefCellHeight() {
        if (prefCellHeight == null) {
            return USE_COMPUTED_SIZE;
        }
        return prefCellHeight.get();
    }

    ReadOnlyDoubleWrapper prefCellHeightWrapper() {
        if (prefCellHeight == null) {
            prefCellHeight = new ReadOnlyDoubleWrapper(USE_COMPUTED_SIZE);
            prefHeightProperty().bindBidirectional(prefCellHeight);
        }
        return prefCellHeight;
    }

    public ReadOnlyDoubleProperty prefCellHeightProperty() {
        return prefCellHeightWrapper().getReadOnlyProperty();
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