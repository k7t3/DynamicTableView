package io.github.k7t3.javafx.sample;

import io.github.k7t3.javafx.DynamicTableCell;
import io.github.k7t3.javafx.DynamicTableView;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageViewTestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DynamicTableView<BaseballTeam> tableView = new DynamicTableView<>();
        tableView.setCellFactory(BaseballTeamCell::new);
        tableView.getItems().addAll(getAllProfessionalTeams());

        primaryStage.setScene(new Scene(new StackPane(tableView)));
        primaryStage.show();
    }

    public static List<BaseballTeam> getAllProfessionalTeams() {
        Path dir = Paths.get("sample");

        List<BaseballTeam> list = new ArrayList<>();
        list.add(new BaseballTeam("東京ヤクルトスワローズ", dir.resolve("swallows.gif").toUri().toString()));
        list.add(new BaseballTeam("阪神タイガース", dir.resolve("tigers.gif").toUri().toString()));
        list.add(new BaseballTeam("読売ジャイアンツ", dir.resolve("giants.gif").toUri().toString()));
        list.add(new BaseballTeam("広島東洋カープ", dir.resolve("carp.gif").toUri().toString()));
        list.add(new BaseballTeam("中日ドラゴンズ", dir.resolve("dragons.gif").toUri().toString()));
        list.add(new BaseballTeam("横浜DeNAベイスターズ", dir.resolve("baystars.gif").toUri().toString()));
        list.add(new BaseballTeam("埼玉西部ライオンズ", dir.resolve("lions.gif").toUri().toString()));
        list.add(new BaseballTeam("北海道日本ハムファイターズ", dir.resolve("fighters.gif").toUri().toString()));
        list.add(new BaseballTeam("東北楽天ゴールデンイーグルス", dir.resolve("eagles.gif").toUri().toString()));
        list.add(new BaseballTeam("福岡ソフトバンクホークス", dir.resolve("hawks.gif").toUri().toString()));
        list.add(new BaseballTeam("オリックス・バファローズ", dir.resolve("buffaloes.gif").toUri().toString()));
        list.add(new BaseballTeam("千葉ロッテマリーンズ", dir.resolve("marines.gif").toUri().toString()));

        return list;
    }

    private static class BaseballTeam {
        private final String name;
        private final String imageUrl;

        public BaseballTeam(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        private Image image;

        public Image getImage() {
            if (image == null) {
                image = new Image(imageUrl, true);
            }
            return image;
        }
    }

    private static class BaseballTeamCell extends DynamicTableCell<BaseballTeam> {

        public BaseballTeamCell(int index, ReadOnlyDoubleProperty cellSizeProperty) {
            super(index, cellSizeProperty);
        }

        private Label teamName;
        private ImageView teamImage;

        @Override
        protected Node createView() {
            teamName = new Label();
            teamImage = new ImageView();
            teamImage.setPreserveRatio(true);
            teamImage.fitWidthProperty().bind(cellSizeProperty.multiply(0.8));

            BorderPane layout = new BorderPane();
            layout.setTop(teamName);
            layout.setCenter(teamImage);
            return layout;
        }

        @Override
        protected void updateItem(BaseballTeam item) {
            teamName.setText(item.getName());
            teamImage.setImage(item.getImage());
        }
    }
}
