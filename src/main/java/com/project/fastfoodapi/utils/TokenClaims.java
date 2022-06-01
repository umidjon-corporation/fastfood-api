package com.project.fastfoodapi.utils;

public enum TokenClaims {

    USER_ID("user_id"),

    USER_INFO("user_info"),
    USER_NAME("user_name"),
    USER_NUMBER("user_number"),
    EXPIRE("exp");
    private String key;

    TokenClaims(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
