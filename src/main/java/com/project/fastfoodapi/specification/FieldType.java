package com.project.fastfoodapi.specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum FieldType {

    BOOLEAN {
        public Object parse(String value) {
            return Boolean.valueOf(value);
        }
    },

    CHAR {
        public Object parse(String value) {
            return value.charAt(0);
        }
    },

    DATE {
        public Object parse(String value) {
            Object date = null;
            try {
                date = LocalDateTime.parse(value);
            } catch (Exception ignored) {}

            return date;
        }
    },

    DOUBLE {
        public Object parse(String value) {
            return Double.valueOf(value);
        }
    },

    INTEGER {
        public Object parse(String value) {
            return Integer.valueOf(value);
        }
    },

    LONG {
        public Object parse(String value) {
            return Long.valueOf(value);
        }
    },

    STRING {
        public Object parse(String value) {
            return value;
        }
    },
    OBJECT{
        public Object parse(String value) {
            return value;
        }
    }
    ;

    public abstract Object parse(String value);

}
