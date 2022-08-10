package io.github.k7t3.javafx;

import javafx.scene.control.TableColumn;

import java.util.function.Supplier;

class DynamicTableColumn<T> extends TableColumn<TableDataRowModel<T>, T> {

    protected final DynamicTableView<T> control;

    final int columnIndex;

    DynamicTableColumn(DynamicTableView<T> control, int index) {
        super();
        this.control = control;
        this.columnIndex = index;

        setCellValueFactory(new DynamicTableCellValueFactory<>());

        Supplier<DynamicTableCell<T>> factory;
        if (control.getCellFactory() != null) {

            factory = control.getCellFactory();

        } else {

            factory = DefaultDynamicTableCell::new;

        }

        setCellFactory(column -> {
            var cell = factory.get();
            cell.prefCellWidthWrapper().bind(control.cellWidthProperty());
            cell.prefCellHeightWrapper().bind(control.cellHeightProperty());
            return cell;
        });

    }

}