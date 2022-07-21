package io.github.k7t3.javafx.sample;

import io.github.k7t3.javafx.DynamicTableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SampleApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        DynamicTableView<Data> tableView = new DynamicTableView<>();
        tableView.setCellFactory(CustomTableCell::new);

        for (int i = 0; i < 40; i++) {
            String name = "name " + (i + 1);
            Color color = Color.color(Math.random(), Math.random(), Math.random());
            tableView.getItems().add(new Data(name, color));
        }

        var top = new HBox(new Button(""));
        var right = new VBox(new Button(""));
        var bottom = new HBox(new Button(""));
        var left = new VBox(new Button(""));

        var root = new BorderPane(tableView);
        root.setTop(top);
        root.setRight(right);
        root.setBottom(bottom);
        root.setLeft(left);;

        primaryStage.setScene(new Scene(root, 850, 700));
        primaryStage.show();
    }

}
