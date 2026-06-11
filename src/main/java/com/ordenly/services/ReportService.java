package com.ordenly.services;

import com.ordenly.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ReportService {

    /**
     * Genera un reporte de texto del directorio:
     * árbol de archivos, conteo por extensión, tamaño total.
     */
    public String generateReport(Path directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════\n");
        sb.append("  REPORTE DE DIRECTORIO\n");
        sb.append("  ").append(directory.toAbsolutePath()).append("\n");
        sb.append("══════════════════════════════════════\n\n");

        // Árbol de archivos
        sb.append("📁 ESTRUCTURA\n");
        sb.append("─────────────────────────────────\n");
        Map<String, Integer> extCount = new TreeMap<>();
        long[] totalSize = {0};
        int[] totalFiles = {0};

        try {
            buildTree(directory, sb, "", extCount, totalSize, totalFiles, 0);
        } catch (IOException e) {
            sb.append("  Error al leer directorio: ").append(e.getMessage()).append("\n");
        }

        // Conteo por extensión
        sb.append("\n📊 ARCHIVOS POR EXTENSIÓN\n");
        sb.append("─────────────────────────────────\n");
        for (var entry : extCount.entrySet()) {
            String ext = entry.getKey().isEmpty() ? "(sin extensión)" : "." + entry.getKey();
            sb.append(String.format("  %-20s %d archivos\n", ext, entry.getValue()));
        }

        // Resumen
        sb.append("\n📈 RESUMEN\n");
        sb.append("─────────────────────────────────\n");
        sb.append("  Total archivos: ").append(totalFiles[0]).append("\n");
        sb.append("  Tamaño total:   ").append(FileUtils.formatSize(totalSize[0])).append("\n");
        sb.append("══════════════════════════════════════\n");

        return sb.toString();
    }

    private void buildTree(Path dir, StringBuilder sb, String indent,
                           Map<String, Integer> extCount, long[] totalSize,
                           int[] totalFiles, int depth) throws IOException {
        if (depth > 3) {
            sb.append(indent).append("  ...\n");
            return;
        }

        try (Stream<Path> stream = Files.list(dir)) {
            List<Path> entries = stream.sorted().toList();

            for (int i = 0; i < entries.size(); i++) {
                Path entry = entries.get(i);
                boolean isLast = (i == entries.size() - 1);
                String connector = isLast ? "└── " : "├── ";
                String childIndent = indent + (isLast ? "    " : "│   ");

                if (Files.isDirectory(entry)) {
                    sb.append(indent).append(connector).append("📁 ")
                      .append(entry.getFileName()).append("/\n");
                    buildTree(entry, sb, childIndent, extCount, totalSize, totalFiles, depth + 1);
                } else {
                    String name = entry.getFileName().toString();
                    long size;
                    try {
                        size = Files.size(entry);
                    } catch (IOException e) {
                        size = 0;
                    }

                    sb.append(indent).append(connector).append(name)
                      .append(" (").append(FileUtils.formatSize(size)).append(")\n");

                    // Estadísticas
                    String ext = getExtension(name);
                    extCount.merge(ext, 1, Integer::sum);
                    totalSize[0] += size;
                    totalFiles[0]++;
                }
            }
        }
    }

    private String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        return (dot > 0 && dot < name.length() - 1) ? name.substring(dot + 1).toLowerCase() : "";
    }
}
