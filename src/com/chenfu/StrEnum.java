package com.chenfu;

public enum StrEnum {

    FIRSTVT("FIRSTVT"),LASTVT("LASTVT");

    private final String type;

    private StrEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
