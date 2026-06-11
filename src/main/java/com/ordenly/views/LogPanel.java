package com.ordenly.views;

import com.ordenly.models.OperationLog;
import com.ordenly.viewmodels.MainViewModel;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogPanel extends VBox {

    public LogPanel(MainViewModel viewModel) {
        setSpacing(4);
        setPadding(new Insets(8));
        setPrefHeight(150);

        Label title = new Label("Registro de operaciones");
        title.getStyleClass().add("log-title");

        ListView<OperationLog> logList = new ListView<>(viewModel.getLogs());
        logList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OperationLog item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    getStyleClass().add("log-cell");
                }
            }
        });
        logList.setPlaceholder(new Label("Sin operaciones aún"));
        VBox.setVgrow(logList, Priority.ALWAYS);

        // Auto-scroll al final cuando se añaden nuevos logs
        viewModel.getLogs().addListener((javafx.collections.ListChangeListener<OperationLog>) c -> {
            if (!viewModel.getLogs().isEmpty()) {
                logList.scrollTo(viewModel.getLogs().size() - 1);
            }
        });

        getChildren().addAll(title, logList);
    }
}
