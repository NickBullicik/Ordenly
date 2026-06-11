package com.ordenly.utils;

import java.util.List;
import java.util.Map;

public final class FileUtils {

    private FileUtils() {}

    private static final Map<String, List<String>> CATEGORIES = Map.of(
        "Imágenes", List.of("jpg", "jpeg", "png", "gif", "webp", "svg", "bmp", "ico"),
        "Documentos", List.of("pdf", "doc", "docx", "txt", "md", "odt", "rtf", "xlsx", "xls", "pptx", "csv"),
        "Vídeos", List.of("mp4", "mov", "avi", "mkv", "wmv", "flv", "webm"),
        "Audio", List.of("mp3", "wav", "flac", "aac", "ogg", "wma", "m4a"),
        "Código", List.of("java", "py", "js", "ts", "html", "css", "json", "xml", "sql", "c", "cpp", "h", "dart", "kt", "rb", "go", "rs", "php")
    );

    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    public static String getCategory(String extension) {
        if (extension == null || extension.isEmpty()) return "Otros";
        String ext = extension.toLowerCase();
        for (var entry : CATEGORIES.entrySet()) {
            if (entry.getValue().contains(ext)) {
                return entry.getKey();
            }
        }
        return "Otros";
    }

    public static Map<String, List<String>> getCategories() {
        return CATEGORIES;
    }
}
