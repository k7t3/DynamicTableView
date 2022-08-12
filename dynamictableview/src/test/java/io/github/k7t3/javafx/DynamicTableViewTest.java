package io.github.k7t3.javafx;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableViewTest extends ApplicationTest {

    private DynamicTableView<String> table;

    @Override
    public void start(Stage stage) {
        System.out.println("テストアプリケーションスタート");
        table = new DynamicTableView<>();
        table.setCellWidth(100);
        table.setCellHeight(100);
        table.getItems().addAll("this is test word", "this is test word", "this is test word", "this is test word");
        stage.setScene(new Scene(new StackPane(table), 320, 400));
        stage.show();
    }

    @Test
    public void test() {
        System.out.println("テスト開始");

        FxAssert.verifyThat("." + DefaultDynamicTableCell.DEFAULT_TEXT_STYLE_CLASS, LabeledMatchers.hasText("this is test word"));
    }

    @Test
    public void testSelectionModel() {
        var selectionModel = table.getSelectionModel();

        var items = List.of(
                "first", "second", "third",
                "forth", "fifth", "sixth",
                "seventh", "eighth", "ninth",
                "tenth"
        );
        table.getItems().setAll(items);

        selectionModel.selectAll();
        assertIterableEquals(items, selectionModel.getSelectedItems());
        selectionModel.clearSelection();

        selectionModel.select(1);

        assertEquals(items.get(1), selectionModel.getSelectedItem());

        selectionModel.select(2);
        assertEquals(items.get(2), selectionModel.getSelectedItem());

        assertEquals(2, selectionModel.getSelectedItems().size());

        selectionModel.clearSelection();
        assertTrue(selectionModel.isEmpty());

        selectionModel.select("fifth");

        selectionModel.selectPrevious();
        assertEquals("forth", selectionModel.getSelectedItem());

        selectionModel.selectPrevious();
        assertEquals("third", selectionModel.getSelectedItem());

        selectionModel.clearSelection();

        selectionModel.select("fifth");
        selectionModel.selectNext();
        assertEquals("sixth", selectionModel.getSelectedItem());

        selectionModel.selectNext();
        assertEquals("seventh", selectionModel.getSelectedItem());

        System.out.println("done");
    }
}