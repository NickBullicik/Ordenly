package com.ordenly.services;

import com.ordenly.models.FileItem;
import com.ordenly.models.OperationLog;
import com.ordenly.models.RenameRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenameService {

    /**
     * Calcula los nuevos nombres sin tocar disco.
     * Modifica el campo newName de cada FileItem seleccionado.
     */
    public void preview(List<FileItem> files, List<RenameRule> rules) {
        int index = 0;
        for (FileItem file : files) {
            if (!file.isSelected()) {
                file.setNewName(file.getOriginalName());
                continue;
            }
            String name = file.getOriginalName();
            for (RenameRule rule : rules) {
                name = rule.apply(name, index);
            }
            file.setNewName(name);
            index++;
        }
    }

    /**
     * Ejecuta el renombrado en disco.
     * Valida que no haya colisiones antes de proceder.
     */
    public OperationLog execute(List<FileItem> files) {
        List<FileItem> toRename = files.stream()
            .filter(FileItem::isSelected)
            .filter(FileItem::isRenamed)
            .toList();

        // Validar colisiones
        Set<String> newNames = new HashSet<>();
        for (FileItem file : toRename) {
            if (!newNames.add(file.getFullNewName().toLowerCase())) {
                return new OperationLog(
                    "Error: colisión de nombres",
                    0,
                    List.of("El nombre \"" + file.getFullNewName() + "\" se repite")
                );
            }
        }

        List<String> details = new ArrayList<>();
        int success = 0;

        for (FileItem file : toRename) {
            Path source = file.getOriginalPath();
            Path target = source.getParent().resolve(file.getFullNewName());

            if (Files.exists(target) && !target.equals(source)) {
                details.add("⚠ Omitido (ya existe): " + file.getFullNewName());
                continue;
            }

            try {
                Files.move(source, target);
                details.add(file.getFullOriginalName() + " → " + file.getFullNewName());
                success++;
            } catch (IOException e) {
                details.add("✗ Error: " + file.getFullOriginalName() + " — " + e.getMessage());
            }
        }

        return new OperationLog("Renombrado", success, details);
    }
}
