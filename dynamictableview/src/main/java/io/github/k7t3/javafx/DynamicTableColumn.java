package io.github.k7t3.javafx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableColumn;

import java.util.function.Supplier;

class DynamicTableColumn<T> extends TableColumn<TableDataRowModel<T>, T> {

    protected final DynamicTableView<T> control;

    final int columnIndex;

    /**
     * 列の幅を表すプロパティ。
     * {@link DynamicTableView#cellSizeProperty()}とバインドされます。
     */
    final DoubleProperty columnSizeProperty;

    DynamicTableColumn(DynamicTableView<T> control, int index) {
        super();
        this.control = control;
        this.columnIndex = index;
        this.columnSizeProperty = new SimpleDoubleProperty();

        setCellValueFactory(new DynamicTableCellValueFactory<>());

        Supplier<DynamicTableCell<T>> factory;
        if (control.getCellFactory() != null) {

            factory = control.getCellFactory();

        } else {

            factory = DefaultDynamicTableCell::new;

        }

        setCellFactory(column -> {
            var cell = factory.get();
            cell.cellSizeWrapper().bind(columnSizeProperty);
            return cell;
        });

    }

}