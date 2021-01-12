package chance;

import java.util.Random;

public class ChanceCard {
    private String text;
    private String actionType;
    private int value;
    private int move;
    private int chanceCount = 6;

    public ChanceCard(String text, String actionType, int value, int move) {
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

    public String getActionType() {
        return actionType;
    }

    public ChanceCard getRandomChanceCard(ChanceCard[] chanceCards) {
        int idx = new Random().nextInt(chanceCount);
        return chanceCards[idx];
    }
}
