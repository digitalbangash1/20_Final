package chance;

public class ChanceCard {
    private String text;
    private ChanceCardActionType actionType;
    private int value;
    private int move;

    public ChanceCard(String text, ChanceCardActionType actionType, int value, int move) {
        this.text = text;
        this. actionType = actionType;
        this.value = value;
        this.move = move;
    }

    public String getText() {
        return text;
    }

    public int getMove() {
        return move;
    }

    public int getValue() {
        return value;
    }

    public ChanceCardActionType getActionType() {
        return actionType;
    }


}
