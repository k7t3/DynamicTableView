package io.github.k7t3.javafx;

import javafx.scene.Node;
import javafx.scene.control.Label;

class DefaultDynamicTableCell<T> extends DynamicTableCell<T> {

    public static final String DEFAULT_TEXT_STYLE_CLASS = "default-text-label";

    public DefaultDynamicTableCell() {
        super();
    }

    private Label text;

    @Override
    protected Node createView() {
        text = new Label();
        text.getStyleClass().add(DEFAULT_TEXT_STYLE_CLASS);
        return text;
    }

    @Override
    protected void updateItem(T item) {
        text.setText(item.toString());
    }

}
