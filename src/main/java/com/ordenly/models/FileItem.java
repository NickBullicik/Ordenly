package com.ordenly.models;

import java.nio.file.Path;

public class FileItem {

    private final Path originalPath;
    private final String originalName;
    private String newName;
    private final String extension;
    private final long sizeBytes;
    private boolean selected;

    public FileItem(Path originalPath, String originalName, String extension, long sizeBytes) {
        this.originalPath = originalPath;
        this.originalName = originalName;
        this.newName = originalName;
        this.extension = extension;
        this.sizeBytes = sizeBytes;
        this.selected = true;
    }

    public Path getOriginalPath() { return originalPath; }
    public String getOriginalName() { return originalName; }
    public String getNewName() { return newName; }
    public void setNewName(String newName) { this.newName = newName; }
    public String getExtension() { return extension; }
    public long getSizeBytes() { return sizeBytes; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    /** Nombre completo con extensión */
    public String getFullOriginalName() {
        return extension.isEmpty() ? originalName : originalName + "." + extension;
    }

    public String getFullNewName() {
        return extension.isEmpty() ? newName : newName + "." + extension;
    }

    public boolean isRenamed() {
        return !originalName.equals(newName);
    }

    @Override
    public String toString() {
        return getFullOriginalName() + " → " + getFullNewName();
    }
}
