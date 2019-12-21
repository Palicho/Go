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
}
