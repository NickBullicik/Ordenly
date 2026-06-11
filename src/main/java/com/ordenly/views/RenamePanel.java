package com.ordenly.views;

import com.ordenly.models.RenameRule;
import com.ordenly.viewmodels.MainViewModel;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RenamePanel extends VBox {

    private final MainViewModel viewModel;

    public RenamePanel(MainViewModel viewModel) {
        this.viewModel = viewModel;

        setSpacing(12);
        setPadding(new Insets(12));

        Label title = new Label("Reglas de renombrado");
        title.getStyleClass().add("panel-title");

        // Selector de tipo de regla
        ComboBox<String> ruleType = new ComboBox<>();
        ruleType.getItems().addAll("Prefijo", "Sufijo", "Buscar y reemplazar", "Secuencia numérica", "Prefijo con fecha");
        ruleType.setPromptText("Tipo de regla...");
        ruleType.setMaxWidth(Double.MAX_VALUE);

        // Campos dinámicos
        VBox fieldsBox = new VBox(8);
        TextField field1 = new TextField();
        TextField field2 = new TextField();
        Spinner<Integer> spinnerStart = new Spinner<>(0, 9999, 1);
        Spinner<Integer> spinnerPadding = new Spinner<>(1, 6, 3);

        ruleType.setOnAction(e -> {
            fieldsBox.getChildren().clear();
            String selected = ruleType.getValue();
            if (selected == null) return;
            switch (selected) {
                case "Prefijo" -> {
                    field1.setPromptText("Texto del prefijo...");
                    fieldsBox.getChildren().add(field1);
                }
                case "Sufijo" -> {
                    field1.setPromptText("Texto del sufijo...");
                    fieldsBox.getChildren().add(field1);
                }
                case "Buscar y reemplazar" -> {
                    field1.setPromptText("Buscar...");
                    field2.setPromptText("Reemplazar con...");
                    fieldsBox.getChildren().addAll(field1, field2);
                }
                case "Secuencia numérica" -> {
                    fieldsBox.getChildren().addAll(
                        new Label("Inicio:"), spinnerStart,
                        new Label("Dígitos:"), spinnerPadding
                    );
                }
                case "Prefijo con fecha" -> {
                    field1.setPromptText("Formato (ej: yyyy-MM-dd)");
                    field1.setText("yyyy-MM-dd");
                    fieldsBox.getChildren().add(field1);
                }
            }
        });

        // Botón añadir
        Button addBtn = new Button("+ Añadir regla");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> {
            String selected = ruleType.getValue();
            if (selected == null) return;
            RenameRule rule = switch (selected) {
                case "Prefijo" -> new RenameRule.PrefixRule(field1.getText());
                case "Sufijo" -> new RenameRule.SuffixRule(field1.getText());
                case "Buscar y reemplazar" -> new RenameRule.ReplaceRule(field1.getText(), field2.getText());
                case "Secuencia numérica" -> new RenameRule.SequenceRule(spinnerStart.getValue(), spinnerPadding.getValue());
                case "Prefijo con fecha" -> new RenameRule.DatePrefixRule(field1.getText());
                default -> null;
            };
            if (rule != null) {
                viewModel.addRule(rule);
                field1.clear();
                field2.clear();
            }
        });

        // Lista de reglas añadidas
        ListView<RenameRule> rulesList = new ListView<>(viewModel.getRenameRules());
        rulesList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RenameRule item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDescription());
            }
        });
        rulesList.setPrefHeight(150);
        VBox.setVgrow(rulesList, Priority.ALWAYS);

        // Botones de acción
        Button clearBtn = new Button("Limpiar todo");
        clearBtn.setOnAction(e -> viewModel.clearRules());

        Button applyBtn = new Button("✓ Aplicar cambios");
        applyBtn.getStyleClass().add("success-button");
        applyBtn.setMaxWidth(Double.MAX_VALUE);
        applyBtn.setOnAction(e -> {
            int count = (int) viewModel.getFileList().stream()
                .filter(f -> f.isSelected() && f.isRenamed()).count();
            if (count == 0) return;

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar renombrado");
            confirm.setHeaderText("¿Renombrar " + count + " archivos?");
            confirm.setContentText("Esta acción no se puede deshacer.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    viewModel.applyRename();
                }
            });
        });

        HBox actionRow = new HBox(8, clearBtn, applyBtn);
        HBox.setHgrow(applyBtn, Priority.ALWAYS);

        getChildren().addAll(title, ruleType, fieldsBox, addBtn, new Separator(), rulesList, new Separator(), actionRow);
    }
}
