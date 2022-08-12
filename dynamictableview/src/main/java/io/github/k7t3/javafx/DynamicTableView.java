package io.github.k7t3.javafx;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;

import java.util.function.Supplier;

/**
 * 横幅に応じて動的に列数を変更するテーブルコントロール。
 * @param <T> 取り扱うデータタイプ
 */
public class DynamicTableView<T> extends Control {

    private static final String DEFAULT_STYLE_CLASS = "dynamic-table-view";

    /**
     * 既定のセルの横幅
     */
    private static final double DEFAULT_CELL_WIDTH = 200.0;

    /**
     * 既定のセルの高さ
     */
    private static final double DEFAULT_CELL_HEIGHT = USE_COMPUTED_SIZE;

    /**
     * 既定のPlaceHolder
     */
    private static final Label DEFAULT_PLACE_HOLDER = new Label();

    /**
     * コンストラクタ
     */
    public DynamicTableView() {
        super();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    private final ObjectProperty<Supplier<DynamicTableCell<T>>> cellFactory
            = new SimpleObjectProperty<>(null);

    /**
     * 現在定義されているセルファクトリを返す。
     * @return 現在定義されているセルファクトリ
     */
    public Supplier<DynamicTableCell<T>> getCellFactory() {
        return cellFactoryProperty().get();
    }

    /**
     * セルファクトリプロパティ。
     * @return セルファクトリプロパティ
     */
    public ObjectProperty<Supplier<DynamicTableCell<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    /**
     * {@link DynamicTableCell}を継承したセルクラスを返すファクトリを割り当てる。
     * @param cellFactory {@link DynamicTableCell}を継承したクラスを生成するセルファクトリ。
     */
    public void setCellFactory(Supplier<DynamicTableCell<T>> cellFactory) {
        cellFactoryProperty().set(cellFactory);
    }

    private DoubleProperty cellWidth;

    /**
     * 現在割り当てられているセルの幅を返すメソッド。
     * @return セルの幅
     */
    public double getCellWidth() {
        if (cellWidth == null) {
            return DEFAULT_CELL_WIDTH;
        }
        return cellWidth.get();
    }

    /**
     * 割り当てられているセルの幅を表すプロパティ
     * @return セルの幅を表すプロパティ
     */
    public DoubleProperty cellWidthProperty() {
        if (cellWidth == null) {
            cellWidth = new SimpleDoubleProperty(DEFAULT_CELL_WIDTH);
        }
        return cellWidth;
    }

    /**
     * セルの幅を割り当てるメソッド。
     * @param cellWidth セルの幅
     */
    public void setCellWidth(double cellWidth) {
        cellWidthProperty().set(cellWidth);
    }

    private DoubleProperty cellHeight;

    /**
     * セルの高さを返すメソッド。
     * 既定では{@link javafx.scene.layout.Region#USE_COMPUTED_SIZE}を返す。
     * @return セルの高さ
     */
    public double getCellHeight() {
        if (cellHeight == null) {
            return DEFAULT_CELL_HEIGHT;
        }
        return cellHeight.get();
    }

    /**
     * 割り当てられるセルの高さを表すプロパティ。
     * @return セルの高さを表すプロパティ
     */
    public DoubleProperty cellHeightProperty() {
        if (cellHeight == null) {
            cellHeight = new SimpleDoubleProperty(DEFAULT_CELL_HEIGHT);
        }
        return cellHeight;
    }

    /**
     * セルの高さを割り当てるメソッド。
     * @param cellHeight セルの高さ
     */
    public void setCellHeight(double cellHeight) {
        cellHeightProperty().set(cellHeight);
    }

    private ObjectProperty<Node> placeHolder;

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を返す。
     * 規定値は空の{@link Label}インスタンスが割り当てられる。
     * @return 要素が一つもないことを示すNode。
     */
    public Node getPlaceHolder() {
        if (placeHolder == null) {
            return DEFAULT_PLACE_HOLDER;
        }
        return placeHolderProperty().get();
    }

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を
     * 持つプロパティを返す。既定値は空の{@link Label}インスタンスが割り当てられる。
     * @return 要素が一つもないことを示すNodeプロパティ。
     */
    public ObjectProperty<Node> placeHolderProperty() {
        if (placeHolder == null) {
            placeHolder = new SimpleObjectProperty<>(DEFAULT_PLACE_HOLDER);
        }
        return placeHolder;
    }

    /**
     * {@link DynamicTableView#getItems()}が一つも要素を持たないときに表示する{@link Node}を割り当てる。
     * @param placeHolder 要素が一つもないことを示すNode。
     */
    public void setPlaceHolder(Node placeHolder) {
        placeHolderProperty().set(placeHolder);
    }

    private final ObjectProperty<ObservableList<T>> itemsProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    /**
     * 表示する要素リストを返す。
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
     * 表示する要素リストを割り当てる。
     * @param items 表示する要素リスト
     */
    public void setItems(ObservableList<T> items) {
        itemsProperty().set(items);
    }

    final ReadOnlyObjectWrapper<FilteredList<T>> filteredItemsProperty = new ReadOnlyObjectWrapper<>(new FilteredList<>(itemsProperty.get()));

    /**
     * {@link DynamicTableView#getItems()}をラップした{@link FilteredList}を返す。
     * @return {@link DynamicTableView#getItems()}をラップしたリスト
     */
    public FilteredList<T> getFilteredItems() {
        return filteredItemsProperty().get();
    }

    /**
     * {@link DynamicTableView#getItems()}をラップした{@link FilteredList}プロパティを返す。
     * @return {@link DynamicTableView#getItems()}をラップしたリストプロパティ
     */
    public ReadOnlyObjectProperty<FilteredList<T>> filteredItemsProperty() {
        return filteredItemsProperty.getReadOnlyProperty();
    }

    final ReadOnlyObjectWrapper<SortedList<T>> sortedItemsProperty = new ReadOnlyObjectWrapper<>(new SortedList<>(filteredItemsProperty.get()));

    /**
     * {@link DynamicTableView#getFilteredItems()} ()}をラップした{@link SortedList}を返す。
     * @return {@link DynamicTableView#getFilteredItems()} ()}をラップしたリスト
     */
    public SortedList<T> getSortedItems() {
        return sortedItemsProperty().get();
    }

    /**
     * {@link DynamicTableView#getFilteredItems()} ()}をラップした{@link SortedList}プロパティを返します。
     * @return {@link DynamicTableView#getItems()}をラップしたリストプロパティ
     */
    public ReadOnlyObjectProperty<SortedList<T>> sortedItemsProperty() {
        return sortedItemsProperty.getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<DynamicTableViewSelectionModel<T>> selectionModel;

    ReadOnlyObjectWrapper<DynamicTableViewSelectionModel<T>> selectionModelWrapper() {
        if (selectionModel == null) {
            selectionModel = new ReadOnlyObjectWrapper<>(new DynamicTableViewSelectionModel<>());
        }
        return selectionModel;
    }

    /**
     * このテーブルの{@link javafx.scene.control.MultipleSelectionModel}の実装を返す。
     * @return SelectionModel
     */
    public DynamicTableViewSelectionModel<T> getSelectionModel() {
        return selectionModelWrapper().get();
    }

    /**
     * {@link javafx.scene.control.MultipleSelectionModel}の実装を表すプロパティ。
     * @return SelectionModelプロパティ
     */
    public ReadOnlyObjectProperty<DynamicTableViewSelectionModel<T>> selectionModelProperty() {
        return selectionModelWrapper().getReadOnlyProperty();
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
