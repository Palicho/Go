package tp.server;

public enum Color {
    BLACK(1), WHITE(2);

    private int value;
    private Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Color getColorByValue(int value) {
        if (value == 1) return BLACK;
        else if (value == 2) return WHITE;
        else return null;
    }

}
