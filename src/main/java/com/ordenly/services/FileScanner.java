package com.ordenly.services;

import com.ordenly.models.FileItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileScanner {

    /**
     * Escanea un directorio y devuelve la lista de archivos (no directorios).
     * Ignora archivos ocultos y de sistema.
     */
    public List<FileItem> scan(Path directory) {
        if (directory == null || !Files.isDirectory(directory)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return !Files.isHidden(p);
                    } catch (IOException e) {
                        return true;
                    }
                })
                .map(this::toFileItem)
                .sorted((a, b) -> a.getOriginalName().compareToIgnoreCase(b.getOriginalName()))
                .toList();
        } catch (IOException e) {
            System.err.println("Error al escanear directorio: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private FileItem toFileItem(Path path) {
        String fileName = path.getFileName().toString();
        String name;
        String extension;

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex + 1);
        } else {
            name = fileName;
            extension = "";
        }

        long size;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            size = 0;
        }

        return new FileItem(path, name, extension, size);
    }
}
