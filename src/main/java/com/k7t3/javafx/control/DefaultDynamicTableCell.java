package com.k7t3.javafx.control;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DefaultDynamicTableCell<T> extends DynamicTableCell<T> {

    public static final String DEFAULT_TEXT_STYLE_CLASS = "default-text-label";

    private StackPane layout;
    private Label text;

    public DefaultDynamicTableCell(int index, ReadOnlyDoubleProperty columnWidthProperty) {
        super(index, columnWidthProperty);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        if (item == null || empty) {
            setGraphic(null);
            return;
        }

        String display = item.toString();

        if (layout == null) {
            text = new Label();
            text.getStyleClass().add(DEFAULT_TEXT_STYLE_CLASS);
            layout = new StackPane(text);
        }

        text.setText(display);
        setGraphic(layout);
    }

}
