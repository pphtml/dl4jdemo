package org.superbiz.dto;

public class SecurityDTO {
    private final String symbol;
    private final String name;

    public SecurityDTO(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public static SecurityDTO of(String symbol, String name) {
        return new SecurityDTO(symbol, name);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }
}
