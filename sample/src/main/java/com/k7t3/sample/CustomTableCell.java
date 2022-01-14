package com.k7t3.sample;

import com.k7t3.javafx.control.DynamicTableCell;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

public class CustomTableCell extends DynamicTableCell<Data> {

    public CustomTableCell(int index, ReadOnlyDoubleProperty columnWidthProperty) {
        super(index, columnWidthProperty);
    }

    private BorderPane layout;
    private Label nameLabel;
    private Rectangle rectangle;

    @Override
    public void updateItem(Data item, boolean empty) {
        if (item == null || empty) {
            setGraphic(null);
            return;
        }

        if (layout == null) {
            layout = new BorderPane();
            layout.setMouseTransparent(true);
            nameLabel = new Label();
            rectangle = new Rectangle();
            rectangle.widthProperty().bind(columnWidthProperty.multiply(0.9));
            rectangle.heightProperty().bind(columnWidthProperty.subtract(nameLabel.heightProperty()).multiply(0.9));

            layout.setTop(nameLabel);
            layout.setCenter(rectangle);
        }

        nameLabel.setText(item.getName());
        rectangle.setFill(item.getColor());
        setGraphic(layout);
    }

}
