package io.github.k7t3.javafx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.Map;

class TableDataRowModel<T> {

    private final int rowIndex;

    private final TableDataModel<T> model;

    TableDataRowModel(int rowIndex, TableDataModel<T> model) {
        this.rowIndex = rowIndex;
        this.model = model;
    }

    public T get(int columnIndex) {
        return model.get(rowIndex, columnIndex);
    }

    private final Map<Integer, SimpleObjectProperty<T>> properties = new HashMap<>(10);

    ObservableValue<T> getProperty(int columnIndex) {
        SimpleObjectProperty<T> property = properties.computeIfAbsent(columnIndex, i -> new SimpleObjectProperty<>());
        property.set(get(columnIndex));
        return property;
    }

    void update() {
        // プロパティの値を更新
        properties.forEach((key, value) -> value.set(get(key)));
    }

}