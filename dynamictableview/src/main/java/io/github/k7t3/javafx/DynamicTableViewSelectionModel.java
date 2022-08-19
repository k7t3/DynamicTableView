package io.github.k7t3.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * SelectionModelの拡張
 */
public class DynamicTableViewSelectionModel<T> extends MultipleSelectionModel<T> {

    final ObservableList<T> selectedItems = FXCollections.observableArrayList();
    private final ObservableList<T> readOnlySelectedItems = FXCollections.unmodifiableObservableList(selectedItems);

    final ObservableList<Integer> selectedIndices = FXCollections.observableArrayList();
    private final ObservableList<Integer> readOnlySelectedIndices = FXCollections.unmodifiableObservableList(selectedIndices);

    public DynamicTableViewSelectionModel() {
    }

    private boolean isInitialized = false;

    private TableView<TableDataRowModel<T>> tableView;
    private TableView.TableViewSelectionModel<TableDataRowModel<T>> selectionModel;

    private TableDataModel<T> dataModel;

    private ObservableList<T> items;

    void inject(TableView<TableDataRowModel<T>> tableView, TableDataModel<T> dataModel) {
        this.tableView = tableView;
        this.selectionModel = tableView.getSelectionModel();
        this.dataModel = dataModel;
        this.items = dataModel.getItems();

        selectionModel.getSelectedCells().addListener(selectionModelListener);

        isInitialized = true;
    }

    @SuppressWarnings("rawtypes")
    private final ListChangeListener<? super TablePosition> selectionModelListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {

                // 選択が解除されたセルのリスト
                List<? extends TablePosition> changed = c.getRemoved();

                for (int i = changed.size() - 1; -1 < i; i--) {
                    TablePosition position = changed.get(i);
                    int row = position.getRow();
                    int column = position.getColumn();

                    T item = dataModel.get(row, column);
                    if (item != null) {
                        selectedItems.remove(item);

                        Integer index = row * dataModel.getColumnCount() + column;
                        selectedIndices.remove(index);
                    }
                }

                if (selectedItems.isEmpty()) {
                    setSelectedItem(null);
                    setSelectedIndex(-1);
                } else {
                    setSelectedItem(selectedItems.get(selectedItems.size() - 1));
                    setSelectedIndex(selectedIndices.get(selectedIndices.size() - 1));
                }
            }
            if (c.wasAdded()) {

                // 選択されたセルのリスト
                List<? extends TablePosition> list = c.getAddedSubList();

                for (TablePosition pos : list) {
                    var row = pos.getRow();
                    var column = pos.getColumn();

                    var item = dataModel.get(row, column);
                    var index = row * dataModel.getColumnCount() + column;

                    if (item != null) {
                        selectedItems.add(item);
                        selectedIndices.add(index);

                        setSelectedItem(item);
                        setSelectedIndex(index);
                    }
                }

            }
        }
    };

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return readOnlySelectedIndices;
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return readOnlySelectedItems;
    }

    private int row, column;

    private void indexToRowColumn(int itemIndex) {
        var columnCount = dataModel.getColumnCount();
        row = itemIndex / columnCount;
        column = itemIndex - (row * columnCount);
    }

    private void itemToRowColumn(T item) {
        var index = items.indexOf(item);
        if (index < 0) {
            throw new IllegalArgumentException();
        }
        indexToRowColumn(index);
    }

    private TableColumn<TableDataRowModel<T>, ?> getColumn(int column) {
        return tableView.getColumns().get(column);
    }

    private void selectCell(int itemIndex) {
        indexToRowColumn(itemIndex);
        selectionModel.select(row, getColumn(column));
    }

    private void selectCell(T item) {
        itemToRowColumn(item);
        selectionModel.select(row, getColumn(column));
    }

    @Override
    public void selectIndices(int index, int... indices) {
        if (!isInitialized) {
            return;
        }

        selectCell(index);

        for (var i : indices) {
            selectCell(i);
        }
    }

    @Override
    public void selectAll() {
        if (!isInitialized) {
            return;
        }

        selectionModel.selectAll();
    }

    @Override
    public void clearAndSelect(int index) {
        if (!isInitialized) {
            return;
        }
        selectionModel.clearSelection();
        selectCell(index);
    }

    @Override
    public void select(int index) {
        if (!isInitialized) {
            return;
        }
        selectCell(index);
    }

    @Override
    public void select(T obj) {
        if (!isInitialized) {
            return;
        }
        selectCell(obj);
    }

    @Override
    public void clearSelection(int index) {
        if (!isInitialized) {
            return;
        }
        indexToRowColumn(index);
        selectionModel.clearSelection(row, getColumn(column));
    }

    @Override
    public void clearSelection() {
        if (!isInitialized) {
            return;
        }

        selectedItems.clear();
        selectedIndices.clear();

        selectionModel.clearSelection();
    }

    @Override
    public boolean isSelected(int index) {
        if (!isInitialized) {
            return false;
        }
        indexToRowColumn(index);
        return selectionModel.isSelected(row, getColumn(column));
    }

    @Override
    public boolean isEmpty() {
        if (!isInitialized) {
            return true;
        }
        return selectedItems.isEmpty() && selectedIndices.isEmpty();
    }

    @Override
    public void selectPrevious() {
        if (!isInitialized) {
            return;
        }

        var itemIndex = getSelectedIndex();
        if (itemIndex < 1) {
            return;
        }

        select(itemIndex - 1);
    }

    @Override
    public void selectNext() {
        if (!isInitialized) {
            return;
        }

        if (items.isEmpty()) {
            return;
        }

        var itemIndex = getSelectedIndex();
        if (itemIndex + 1 < items.size()) {
            select(itemIndex + 1);
        }
    }

    @Override
    public void selectFirst() {
        if (!isInitialized) {
            return;
        }

        if (items.isEmpty()) {
            return;
        }

        select(0);
    }

    @Override
    public void selectLast() {
        if (!isInitialized) {
            return;
        }

        if (items.isEmpty()) {
            return;
        }

        select(items.size() - 1);
    }
}
