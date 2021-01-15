import gui_fields.GUI_Field;
import gui_fields.GUI_Player;

public class ControllerGame {

    ControllerGUI controllerGUI = new ControllerGUI();
    public Player currentPlayer;

    public Dice dice = new Dice();

    ControllerGame() {
        Start();
    }

    private void Start() {

        boolean gameIsWon = false;
        GameBoard gameBoard = controllerGUI.getGameBoard();
        while (!gameIsWon) {

            currentPlayer = NextPlayer();
            String playerName = currentPlayer.getName();
            System.out.println(playerName + " turn");

            controllerGUI.gui.showMessage(playerName + " : roll the dice");
            int value1 = dice.roll();
            int value2 = dice.roll();
            int sum = value1 + value2;
            controllerGUI.gui.setDice(value1, value2);

            try {
                gameBoard.takePlayerTurn(currentPlayer, sum, dice);
            } catch (NotEnoughBalanceException e) {
                gameIsWon = true;
                controllerGUI.getGameBoard().gameOver(currentPlayer);
            }
        }
    }

    public int PlayerStart = -1;

    public Player NextPlayer() {
        PlayerStart++;
        PlayerStart %= controllerGUI.players.length;
        controllerGUI.players[PlayerStart].setPlayerTurn(true);
        return controllerGUI.players[PlayerStart];
    }

}
