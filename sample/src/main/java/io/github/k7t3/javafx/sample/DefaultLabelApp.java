package io.github.k7t3.javafx.sample;

import io.github.k7t3.javafx.DynamicTableView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DefaultLabelApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        DynamicTableView<String> tableView = new DynamicTableView<>();
        tableView.getItems().addAll(
                "apple",
                "lemon",
                "orange",
                "raspberry",
                "cherry",
                "banana",
                "peach",
                "strawberry",
                "pineapple"
        );

        var scene = new Scene(new StackPane(tableView), 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
