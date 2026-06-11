package com.ordenly.viewmodels;

import com.ordenly.models.FileItem;
import com.ordenly.models.OperationLog;
import com.ordenly.models.RenameRule;
import com.ordenly.services.FileScanner;
import com.ordenly.services.OrganizerService;
import com.ordenly.services.RenameService;
import com.ordenly.services.ReportService;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.Map;

public class MainViewModel {

    private final ObjectProperty<Path> currentDirectory = new SimpleObjectProperty<>();
    private final ObservableList<FileItem> fileList = FXCollections.observableArrayList();
    private final ObservableList<RenameRule> renameRules = FXCollections.observableArrayList();
    private final ObservableList<OperationLog> logs = FXCollections.observableArrayList();
    private final StringProperty currentMode = new SimpleStringProperty("RENAME");
    private final StringProperty reportText = new SimpleStringProperty("");

    private final FileScanner scanner = new FileScanner();
    private final RenameService renameService = new RenameService();
    private final OrganizerService organizerService = new OrganizerService();
    private final ReportService reportService = new ReportService();

    // --- Properties ---

    public ObjectProperty<Path> currentDirectoryProperty() { return currentDirectory; }
    public ObservableList<FileItem> getFileList() { return fileList; }
    public ObservableList<RenameRule> getRenameRules() { return renameRules; }
    public ObservableList<OperationLog> getLogs() { return logs; }
    public StringProperty currentModeProperty() { return currentMode; }
    public StringProperty reportTextProperty() { return reportText; }

    // --- Acciones ---

    public void loadDirectory(Path directory) {
        currentDirectory.set(directory);
        var files = scanner.scan(directory);
        fileList.setAll(files);
        renameRules.clear();
        reportText.set("");
    }

    public void addRule(RenameRule rule) {
        renameRules.add(rule);
        updatePreview();
    }

    public void removeRule(RenameRule rule) {
        renameRules.remove(rule);
        updatePreview();
    }

    public void clearRules() {
        renameRules.clear();
        updatePreview();
    }

    private void updatePreview() {
        renameService.preview(fileList, renameRules);
        // Forzar refresco de la tabla
        var copy = new java.util.ArrayList<>(fileList);
        fileList.setAll(copy);
    }

    public void applyRename() {
        OperationLog log = renameService.execute(fileList);
        logs.add(log);

        // Recargar directorio después de renombrar
        if (currentDirectory.get() != null) {
            loadDirectory(currentDirectory.get());
        }
    }

    public Map<String, Integer> getOrganizePreview() {
        if (currentDirectory.get() == null) return Map.of();
        return organizerService.preview(currentDirectory.get());
    }

    public void organizeFiles() {
        if (currentDirectory.get() == null) return;
        OperationLog log = organizerService.organize(currentDirectory.get());
        logs.add(log);
        loadDirectory(currentDirectory.get());
    }

    public void generateReport() {
        if (currentDirectory.get() == null) return;
        String report = reportService.generateReport(currentDirectory.get());
        reportText.set(report);

        OperationLog log = new OperationLog(
            "Reporte generado",
            fileList.size(),
            java.util.List.of("Reporte del directorio " + currentDirectory.get().getFileName())
        );
        logs.add(log);
    }
}
