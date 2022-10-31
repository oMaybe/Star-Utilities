package me.karam.utils.info;

public enum Severity {

    HIGH(Color.ANSI_RED),
    MEDIUM(Color.ANSI_YELLOW),
    LOW(Color.ANSI_GREEN),
    INFO(Color.ANSI_PURPLE),
    DEBUG(Color.ANSI_CYAN);
    private String colorNames;
    Severity(String color) {
        colorNames = color;
    }
    public String getColorNames() {
        return colorNames;
    }
}
