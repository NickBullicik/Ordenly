package com.ordenly.views;

import com.ordenly.models.FileItem;
import com.ordenly.utils.FileUtils;
import com.ordenly.viewmodels.MainViewModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class FileTableView extends VBox {

    private final MainViewModel viewModel;
    private final TableView<FileItem> table;

    public FileTableView(MainViewModel viewModel) {
        this.viewModel = viewModel;
        this.table = new TableView<>();

        setSpacing(8);
        setPadding(new Insets(8));

        // Barra superior: botón seleccionar carpeta + ruta
        Label pathLabel = new Label("Selecciona una carpeta para empezar");
        pathLabel.getStyleClass().add("path-label");
        pathLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(pathLabel, Priority.ALWAYS);

        Button openBtn = new Button("📁 Seleccionar carpeta");
        openBtn.getStyleClass().add("primary-button");
        openBtn.setOnAction(e -> selectDirectory());

        HBox toolbar = new HBox(8, openBtn, pathLabel);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        viewModel.currentDirectoryProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pathLabel.setText(newVal.toAbsolutePath().toString());
            }
        });

        // Tabla
        setupTable();
        table.setItems(viewModel.getFileList());
        table.setPlaceholder(new Label("No hay archivos cargados"));
        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(toolbar, table);
    }

    @SuppressWarnings("unchecked")
    private void setupTable() {
        table.setEditable(true);

        // Columna: Seleccionado
        TableColumn<FileItem, Boolean> selectCol = new TableColumn<>("✓");
        selectCol.setCellValueFactory(data -> {
            var prop = new javafx.beans.property.SimpleBooleanProperty(data.getValue().isSelected());
            prop.addListener((obs, oldVal, newVal) -> data.getValue().setSelected(newVal));
            return prop;
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setPrefWidth(40);

        // Columna: Nombre actual
        TableColumn<FileItem, String> nameCol = new TableColumn<>("Nombre actual");
        nameCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getFullOriginalName()));
        nameCol.setPrefWidth(250);

        // Columna: Nombre nuevo
        TableColumn<FileItem, String> newNameCol = new TableColumn<>("Nombre nuevo");
        newNameCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getFullNewName()));
        newNameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    FileItem fileItem = getTableView().getItems().get(getIndex());
                    if (fileItem.isRenamed()) {
                        setStyle("-fx-text-fill: #48BB78; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        newNameCol.setPrefWidth(250);

        // Columna: Extensión
        TableColumn<FileItem, String> extCol = new TableColumn<>("Ext");
        extCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getExtension()));
        extCol.setPrefWidth(60);

        // Columna: Tamaño
        TableColumn<FileItem, String> sizeCol = new TableColumn<>("Tamaño");
        sizeCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                FileUtils.formatSize(data.getValue().getSizeBytes())));
        sizeCol.setPrefWidth(80);

        table.getColumns().addAll(selectCol, nameCol, newNameCol, extCol, sizeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void selectDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta");
        File dir = chooser.showDialog(getScene().getWindow());
        if (dir != null) {
            viewModel.loadDirectory(dir.toPath());
        }
    }
}
