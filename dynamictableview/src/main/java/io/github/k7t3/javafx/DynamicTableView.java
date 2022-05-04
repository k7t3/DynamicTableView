package io.github.k7t3.javafx;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.function.BiFunction;

/**
 * 横幅に応じて動的に列数を変更するテーブルコントロールです。
 * @param <T> 取り扱うデータタイプ
 */
public class DynamicTableView<T> extends Control {

    private static final String DEFAULT_STYLE_CLASS = "dynamic-table-view";

    /**
     * 列の規定の幅(200d)を表します。
     */
    public static double DEFAULT_COLUMN_WIDTH = 200d;

    public DynamicTableView() {
        super();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setAccessibleRole(AccessibleRole.TABLE_VIEW);
    }

    private final ObjectProperty<BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>>> cellFactoryProperty
            = new SimpleObjectProperty<>(null);

    /**
     * 現在定義されているセルファクトリを返します。
     * @return 現在定義されているセルファクトリ
     */
    public BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>> getCellFactory() {
        return cellFactoryProperty().get();
    }

    /**
     * セルファクトリプロパティ。
     * @return セルファクトリプロパティ
     */
    public ObjectProperty<BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>>> cellFactoryProperty() {
        return cellFactoryProperty;
    }

    /**
     * {@link DynamicTableCell}を継承したセルクラスを返すファクトリを割り当てます。
     * @param cellFactory {@link DynamicTableCell}を継承したクラスを生成するセルファクトリ。
     */
    public void setCellFactory(BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>> cellFactory) {
        this.cellFactoryProperty().set(cellFactory);
    }

    private final DoubleProperty columnWidthProperty = new SimpleDoubleProperty(DEFAULT_COLUMN_WIDTH);

    /**
     * テーブルの列の幅を返します。既定値は{@link DynamicTableView#DEFAULT_COLUMN_WIDTH}です。
     * @return 列の幅
     */
    public double getColumnWidth() {
        return columnWidthProperty().get();
    }

    /**
     * 列の幅を持つプロパティ。既定値は{@link DynamicTableView#DEFAULT_COLUMN_WIDTH}
     * @return 列の幅を持つプロパティ
     */
    public DoubleProperty columnWidthProperty() {
        return columnWidthProperty;
    }

    /**
     * 列の幅を割り当てます。
     * @param cellSize 列の幅
     */
    public void setColumnWidth(double cellSize) {
        this.columnWidthProperty().set(cellSize);
    }

    private final ObjectProperty<Node> placeHolderProperty = new SimpleObjectProperty<>(new Label());

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を返します。
     * 既定値は{@link TableView}に基づきます。
     * @return 要素が一つもないことを示すNode。
     */
    public Node getPlaceHolder() {
        return placeHolderProperty().get();
    }

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を
     * 持つプロパティを返します。既定値は{@link TableView}に基づきます。
     * @return 要素が一つもないことを示すNodeプロパティ。
     */
    public ObjectProperty<Node> placeHolderProperty() {
        return placeHolderProperty;
    }

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を割り当てます。
     * @param placeHolder 要素が一つもないことを示すNode。
     */
    public void setPlaceHolder(Node placeHolder) {
        this.placeHolderProperty().set(placeHolder);
    }

    private final ObjectProperty<ObservableList<T>> itemsProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    /**
     * 表示する要素リストを返します。
     * @return 表示する要素リスト
     */
    public ObservableList<T> getItems() {
        return itemsProperty().get();
    }

    /**
     * 表示する要素リストを持つプロパティ。
     * @return 表示する要素リストを持つプロパティ。
     */
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return itemsProperty;
    }

    /**
     * 表示する要素リストを割り当てます。
     * @param items 表示する要素リスト
     */
    public void setItems(ObservableList<T> items) {
        itemsProperty().set(items);
    }

    // これ内部に持つ必要ある？外からsetItemsでFilterなりSortedなり適用したリスト当てはめればいいんじゃ？
    final ReadOnlyObjectWrapper<FilteredList<T>> filteredItemsProperty = new ReadOnlyObjectWrapper<>(new FilteredList<>(itemsProperty.get()));

    /**
     * {@link DynamicTableView#getItems()}をラップしたリストを返します。
     * @return {@link DynamicTableView#getItems()}をラップしたリスト
     */
    public FilteredList<T> getFilteredItems() {
        return filteredItemsProperty().get();
    }

    /**
     * {@link DynamicTableView#getItems()}をラップしたリストプロパティを返します。
     * @return {@link DynamicTableView#getItems()}をラップしたリストプロパティ
     */
    public ReadOnlyObjectProperty<FilteredList<T>> filteredItemsProperty() {
        return filteredItemsProperty.getReadOnlyProperty();
    }

    // これ内部に持つ必要ある？外からsetItemsでFilterなりSortedなり適用したリスト当てはめればいいんじゃ？
    final ReadOnlyObjectWrapper<SortedList<T>> sortedItemsProperty = new ReadOnlyObjectWrapper<>(new SortedList<>(filteredItemsProperty.get()));

    /**
     * {@link DynamicTableView#getItems()}をラップしたリストを返します。
     * @return {@link DynamicTableView#getItems()}をラップしたリスト
     */
    public SortedList<T> getSortedItems() {
        return sortedItemsProperty().get();
    }

    /**
     * {@link DynamicTableView#getItems()}をラップしたリストプロパティを返します。
     * @return {@link DynamicTableView#getItems()}をラップしたリストプロパティ
     */
    public ReadOnlyObjectProperty<SortedList<T>> sortedItemsProperty() {
        return sortedItemsProperty.getReadOnlyProperty();
    }

    final ReadOnlyObjectWrapper<T> selectedItemProperty = new ReadOnlyObjectWrapper<>();

    /**
     * 選択しているアイテムを返します。複数のアイテムを選択しているときは
     * 最後に選択されたアイテムが返されます。
     * @return 最後に選択されたアイテム
     */
    public T getSelectedItem() {
        return selectedItemProperty().get();
    }

    /**
     * 選択しているアイテムのプロパティを返す。複数のアイテムを選択しているときは
     * 最後に選択されたアイテムが返されます。
     * @return 選択しているアイテムのプロパティ
     */
    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return selectedItemProperty.getReadOnlyProperty();
    }

    final ObservableList<T> selectedItems = FXCollections.observableArrayList();

    private final ObservableList<T> unmodifiableSelectedItems = FXCollections.unmodifiableObservableList(selectedItems);

    /**
     * 選択しているアイテムのリストを返します。
     * @return 選択しているアイテムのリスト
     */
    public ObservableList<T> getUnmodifiableSelectedItems() {
        return unmodifiableSelectedItems;
    }

    /**
     * 選択項目をクリアするためのアクション。内部テーブルの作成時に注入される。
     */
    Runnable clearSelectionAction;

    /**
     * 選択項目をクリアし、
     * {@link DynamicTableView#getSelectedItem()}がnullを返すようになります。
     */
    public void clearSelection() {
        if (clearSelectionAction == null)
            return;
        clearSelectionAction.run();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DynamicTableViewSkin<>(this);
    }

    private String styleSheet;

    @Override
    public String getUserAgentStylesheet() {
        if (styleSheet == null) {
            styleSheet = getClass().getResource("dynamictableview.css").toExternalForm();
        }
        return styleSheet;
    }
}
