package io.github.k7t3.javafx;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

class DynamicTableViewTest extends ApplicationTest {

    private DynamicTableView<String> table;

    @Override
    public void start(Stage stage) {
        System.out.println("テストアプリケーションスタート");
        table = new DynamicTableView<>();
        table.setColumnWidth(100);
        table.getItems().addAll("this is test word", "this is test word", "this is test word", "this is test word");
        stage.setScene(new Scene(new StackPane(table), 300, 400));
        stage.show();
    }

    @Test
    public void test() {
        System.out.println("テスト開始");

        FxAssert.verifyThat("." + DefaultDynamicTableCell.DEFAULT_TEXT_STYLE_CLASS, LabeledMatchers.hasText("this is test word"));
    }
}