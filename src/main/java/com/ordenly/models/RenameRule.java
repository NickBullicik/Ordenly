package com.ordenly.models;

/**
 * Interfaz para reglas de renombrado.
 * Cada implementación transforma el nombre de un archivo.
 */
public interface RenameRule {

    String apply(String name, int index);

    String getDescription();

    // --- Implementaciones ---

    class PrefixRule implements RenameRule {
        private final String prefix;
        public PrefixRule(String prefix) { this.prefix = prefix; }

        @Override
        public String apply(String name, int index) {
            return prefix + name;
        }

        @Override
        public String getDescription() {
            return "Prefijo: \"" + prefix + "\"";
        }
    }

    class SuffixRule implements RenameRule {
        private final String suffix;
        public SuffixRule(String suffix) { this.suffix = suffix; }

        @Override
        public String apply(String name, int index) {
            return name + suffix;
        }

        @Override
        public String getDescription() {
            return "Sufijo: \"" + suffix + "\"";
        }
    }

    class ReplaceRule implements RenameRule {
        private final String find;
        private final String replace;
        public ReplaceRule(String find, String replace) {
            this.find = find;
            this.replace = replace;
        }

        @Override
        public String apply(String name, int index) {
            return name.replace(find, replace);
        }

        @Override
        public String getDescription() {
            return "Reemplazar: \"" + find + "\" → \"" + replace + "\"";
        }
    }

    class SequenceRule implements RenameRule {
        private final int start;
        private final int padding;
        public SequenceRule(int start, int padding) {
            this.start = start;
            this.padding = padding;
        }

        @Override
        public String apply(String name, int index) {
            String seq = String.format("%0" + padding + "d", start + index);
            return name + "_" + seq;
        }

        @Override
        public String getDescription() {
            return "Secuencia: inicio=" + start + ", dígitos=" + padding;
        }
    }

    class DatePrefixRule implements RenameRule {
        private final String format;
        public DatePrefixRule(String format) { this.format = format; }

        @Override
        public String apply(String name, int index) {
            String date = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern(format)
            );
            return date + "_" + name;
        }

        @Override
        public String getDescription() {
            return "Prefijo fecha: \"" + format + "\"";
        }
    }
}
