package io.github.k7t3.javafx.sample;

import io.github.k7t3.javafx.DynamicTableCell;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

public class CustomTableCell extends DynamicTableCell<Data> {

    public CustomTableCell() {
        super();
    }

    private Label nameLabel;
    private Rectangle rectangle;

    @Override
    protected Node createView() {
        BorderPane layout = new BorderPane();
        nameLabel = new Label();
        rectangle = new Rectangle();
        rectangle.widthProperty().bind(prefCellWidthProperty().subtract(nameLabel.heightProperty()));
        rectangle.heightProperty().bind(prefCellHeightProperty().subtract(nameLabel.heightProperty()));
        layout.setTop(nameLabel);
        layout.setCenter(rectangle);
        return layout;
    }

    @Override
    protected void updateItem(Data item) {
        nameLabel.setText(item.getName());
        rectangle.setFill(item.getColor());
    }

}
