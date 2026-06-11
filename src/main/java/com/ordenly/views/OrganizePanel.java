package com.ordenly.views;

import com.ordenly.viewmodels.MainViewModel;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;

public class OrganizePanel extends VBox {

    private final MainViewModel viewModel;
    private final VBox previewBox;

    public OrganizePanel(MainViewModel viewModel) {
        this.viewModel = viewModel;
        this.previewBox = new VBox(4);

        setSpacing(12);
        setPadding(new Insets(12));

        Label title = new Label("Organizar por tipo");
        title.getStyleClass().add("panel-title");

        Label description = new Label(
            "Clasifica automáticamente los archivos en subcarpetas según su tipo: " +
            "Imágenes, Documentos, Vídeos, Audio, Código y Otros."
        );
        description.setWrapText(true);
        description.getStyleClass().add("description-label");

        Button previewBtn = new Button("👁 Ver previsualización");
        previewBtn.setMaxWidth(Double.MAX_VALUE);
        previewBtn.setOnAction(e -> updatePreview());

        VBox.setVgrow(previewBox, Priority.ALWAYS);

        Button organizeBtn = new Button("📂 Organizar archivos");
        organizeBtn.getStyleClass().add("success-button");
        organizeBtn.setMaxWidth(Double.MAX_VALUE);
        organizeBtn.setOnAction(e -> {
            Map<String, Integer> preview = viewModel.getOrganizePreview();
            int total = preview.values().stream().mapToInt(i -> i).sum();
            if (total == 0) return;

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar organización");
            confirm.setHeaderText("¿Organizar " + total + " archivos?");
            confirm.setContentText("Se crearán subcarpetas y se moverán los archivos. Esta acción no se puede deshacer.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    viewModel.organizeFiles();
                    previewBox.getChildren().clear();
                    previewBox.getChildren().add(new Label("✓ Organización completada"));
                }
            });
        });

        getChildren().addAll(title, description, previewBtn, new Separator(), previewBox, new Separator(), organizeBtn);
    }

    private void updatePreview() {
        previewBox.getChildren().clear();
        Map<String, Integer> preview = viewModel.getOrganizePreview();

        if (preview.isEmpty()) {
            previewBox.getChildren().add(new Label("No hay archivos para organizar"));
            return;
        }

        for (var entry : preview.entrySet()) {
            Label label = new Label("  " + entry.getKey() + ": " + entry.getValue() + " archivos");
            label.getStyleClass().add("preview-item");
            previewBox.getChildren().add(label);
        }
    }
}
