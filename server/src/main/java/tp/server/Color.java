package tp.server;

public enum Color {
    BLACK(0), WHITE(1);

    private int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Color getColorByValue(int value) {
        if (value == 0) return BLACK;
        else if (value == 1) return WHITE;
        else return null;
    }

}
