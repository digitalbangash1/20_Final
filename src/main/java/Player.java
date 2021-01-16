import gui_fields.GUI_Player;
import gui_main.GUI;

public class Player {

    private GUI_Player player;
    //private int currentSquareIndex;
    private boolean isInPrison;
    private boolean getOutOfJailCard;
    private int playerNumber;
    //private int playerNewPosition;
    private int PlayerPosition;
    private boolean PlayerTurn;
    private String name;

    /**
     * Player contructor
     * @param player
     * @param playerNumber
     */
    public Player(GUI_Player player, int playerNumber) {
        this.name = player.getName();
        this.player = player;
        this.playerNumber = playerNumber;
        this.isInPrison = false;
        this.getOutOfJailCard = false;
    }

    public void setPlayerTurn(boolean playerTurn) {
        PlayerTurn = playerTurn;
    }

    public int getPlayerPosition() {
        return PlayerPosition;
    }

    public void setPlayerPosition(int playerPosition) {
        PlayerPosition = playerPosition;
    }

//    public int getPlayerNewPo() {
//        return playerNewPosition;
//    }
//
//    public void setPlayerNewPosition(int playerNewPosition) {
//        this.playerNewPosition = playerNewPosition;
//    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public GUI_Player getGuiPlayer() {return this.player;}

    public String getName() {
        return this.name;
    }

    public int getBalance() {
        return this.player.getBalance();
    }

    public void setBalance(int balance) {
        this.player.setBalance(balance);
    }

    //public int getCurrentSquareIndex() {
    //    return currentSquareIndex;
   //}

//    public void setCurrentSquareIndex(GUI gui, int currentPositionIndex) {
//        //Remove player from old field
//        gui.getFields()[this.currentSquareIndex].setCar(this.player, false);
//        this.currentSquareIndex = currentPositionIndex;
//        gui.getFields()[this.currentSquareIndex].setCar(this.player, true);
//    }

    public void increaseBalanceBy(int amount) {
        int currentBalance = this.player.getBalance();
        this.player.setBalance(currentBalance + amount);
    }

    public void decreaseBalanceBy(int amount) throws NotEnoughBalanceException {
        int remainingBalance = this.player.getBalance() - amount;
        if (remainingBalance < 0) {
            throw new NotEnoughBalanceException();
        }

        this.player.setBalance(remainingBalance);
    }

    public boolean hasJailFreeCard() {return getOutOfJailCard;}

    public void setGetOutOfJailCard() {
        getOutOfJailCard = !getOutOfJailCard;
    }

    public boolean isInPrison() {
        return isInPrison;
    }

    public void setInPrison(boolean inPrison) {
        isInPrison = inPrison;
    }

    public boolean isBankrupt() {
        return (this.player.getBalance() <= 0);
    }
}
