package com.k7t3.javafx.control;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

import java.util.function.BiFunction;

public class DynamicTableView<T> extends TableView<TableDataRowModel<T>> {

    private final DynamicTableViewController<T> controller = new DynamicTableViewController<>(this);

    public DynamicTableView() {
        super();
    }

    private final ObjectProperty<BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>>> cellFactoryProperty
            = new SimpleObjectProperty<>(DefaultDynamicTableCell::new);

    public BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>> getCellFactory() {
        return cellFactoryProperty.get();
    }

    public ObjectProperty<BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>>> cellFactoryProperty() {
        return cellFactoryProperty;
    }

    public void setCellFactory(BiFunction<Integer, ReadOnlyDoubleProperty, DynamicTableCell<T>> cellFactory) {
        this.cellFactoryProperty.set(cellFactory);
    }

    public DoubleProperty requestCellSizeProperty() {
        return controller.requestCellSizeProperty;
    }

    public DoubleProperty cellSizeMultiplyProperty() {
        return controller.cellSizeMultiply;
    }

    public ObservableList<T> getContentItems() {
        return controller.getContentItems();
    }

    public FilteredList<T> getFilteredList() {
        return controller.getFilteredList();
    }

    public SortedList<T> getSortedList() {
        return controller.getSortedList();
    }

    public ReadOnlyIntegerProperty contentCountProperty() {
        return controller.pictureCountProperty.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty selectedCountProperty() {
        return controller.selectedCountProperty.getReadOnlyProperty();
    }

    public ObjectProperty<T> selectedProperty() {
        return controller.selectedProperty;
    }

    public ObservableList<T> selectedPicturesProperty() {
        return controller.selectedItemsProperty;
    }

    @Override
    public String getUserAgentStylesheet() {
        return "/com/k7t3/javafx/control/dynamictableview.css";
    }
}
