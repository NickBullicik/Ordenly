package com.ordenly.views;

import com.ordenly.viewmodels.MainViewModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainView extends BorderPane {

    private final MainViewModel viewModel;

    public MainView() {
        this.viewModel = new MainViewModel();

        // Sidebar izquierda
        VBox sidebar = createSidebar();

        // Centro: tabla de archivos
        FileTableView fileTable = new FileTableView(viewModel);

        // Panel derecho: acciones según modo
        RenamePanel renamePanel = new RenamePanel(viewModel);
        OrganizePanel organizePanel = new OrganizePanel(viewModel);
        VBox reportPanel = createReportPanel();

        StackPane actionPane = new StackPane(renamePanel, organizePanel, reportPanel);
        actionPane.setPrefWidth(280);

        // Mostrar/ocultar paneles según modo
        viewModel.currentModeProperty().addListener((obs, oldMode, newMode) -> {
            renamePanel.setVisible("RENAME".equals(newMode));
            renamePanel.setManaged("RENAME".equals(newMode));
            organizePanel.setVisible("ORGANIZE".equals(newMode));
            organizePanel.setManaged("ORGANIZE".equals(newMode));
            reportPanel.setVisible("REPORT".equals(newMode));
            reportPanel.setManaged("REPORT".equals(newMode));
        });
        // Estado inicial
        organizePanel.setVisible(false);
        organizePanel.setManaged(false);
        reportPanel.setVisible(false);
        reportPanel.setManaged(false);

        // Inferior: log panel
        LogPanel logPanel = new LogPanel(viewModel);

        // Layout principal
        setLeft(sidebar);
        setCenter(fileTable);
        setRight(actionPane);
        setBottom(logPanel);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(12, 8, 12, 8));
        sidebar.setPrefWidth(160);
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label logo = new Label("FileForge");
        logo.getStyleClass().add("logo");

        ToggleGroup modeGroup = new ToggleGroup();

        ToggleButton renameBtn = new ToggleButton("✏ Renombrar");
        renameBtn.setToggleGroup(modeGroup);
        renameBtn.setSelected(true);
        renameBtn.setMaxWidth(Double.MAX_VALUE);

        ToggleButton organizeBtn = new ToggleButton("📂 Organizar");
        organizeBtn.setToggleGroup(modeGroup);
        organizeBtn.setMaxWidth(Double.MAX_VALUE);

        ToggleButton reportBtn = new ToggleButton("📊 Reporte");
        reportBtn.setToggleGroup(modeGroup);
        reportBtn.setMaxWidth(Double.MAX_VALUE);

        modeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                oldToggle.setSelected(true);
                return;
            }
            if (newToggle == renameBtn) viewModel.currentModeProperty().set("RENAME");
            else if (newToggle == organizeBtn) viewModel.currentModeProperty().set("ORGANIZE");
            else if (newToggle == reportBtn) viewModel.currentModeProperty().set("REPORT");
        });

        sidebar.getChildren().addAll(logo, new Separator(), renameBtn, organizeBtn, reportBtn);
        return sidebar;
    }

    private VBox createReportPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(12));

        Label title = new Label("Reporte de directorio");
        title.getStyleClass().add("panel-title");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.textProperty().bind(viewModel.reportTextProperty());
        reportArea.getStyleClass().add("report-area");
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        Button generateBtn = new Button("📊 Generar reporte");
        generateBtn.getStyleClass().add("primary-button");
        generateBtn.setMaxWidth(Double.MAX_VALUE);
        generateBtn.setOnAction(e -> viewModel.generateReport());

        panel.getChildren().addAll(title, generateBtn, reportArea);
        return panel;
    }
}
