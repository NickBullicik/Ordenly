package com.ordenly.services;

import com.ordenly.models.OperationLog;
import com.ordenly.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OrganizerService {

    /**
     * Genera un preview de cuántos archivos irían a cada categoría.
     */
    public Map<String, Integer> preview(Path directory) {
        Map<String, Integer> counts = new HashMap<>();
        try (Stream<Path> stream = Files.list(directory)) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                String ext = getExtension(path);
                String category = FileUtils.getCategory(ext);
                counts.merge(category, 1, Integer::sum);
            });
        } catch (IOException e) {
            System.err.println("Error al previsualizar: " + e.getMessage());
        }
        return counts;
    }

    /**
     * Organiza los archivos del directorio en subcarpetas por categoría.
     */
    public OperationLog organize(Path directory) {
        List<String> details = new ArrayList<>();
        int moved = 0;

        try (Stream<Path> stream = Files.list(directory)) {
            List<Path> files = stream.filter(Files::isRegularFile).toList();

            for (Path file : files) {
                String ext = getExtension(file);
                String category = FileUtils.getCategory(ext);

                Path targetDir = directory.resolve(category);
                if (!Files.exists(targetDir)) {
                    Files.createDirectory(targetDir);
                }

                Path target = targetDir.resolve(file.getFileName());
                if (Files.exists(target)) {
                    details.add("⚠ Omitido (ya existe): " + file.getFileName());
                    continue;
                }

                Files.move(file, target);
                details.add(file.getFileName() + " → " + category + "/");
                moved++;
            }
        } catch (IOException e) {
            details.add("✗ Error: " + e.getMessage());
        }

        return new OperationLog("Organización", moved, details);
    }

    private String getExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return (dot > 0 && dot < name.length() - 1) ? name.substring(dot + 1) : "";
    }
}
