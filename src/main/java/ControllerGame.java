public class ControllerGame {

    ControllerGUI controllerGUI = new ControllerGUI();
    public Player currentPlayer;

    public Dice dice = new Dice();

    ControllerGame() {
        Start();
    }

    private void Start() {

        boolean testHouseBuilding = false;
        boolean gameIsWon = false;
        GameBoard gameBoard = controllerGUI.getGameBoard();
        while (!gameIsWon) {

            switchPlayer();

            try {
                if (testHouseBuilding) {
                    testHouseBuilding(currentPlayer, gameBoard);
                } else {
                    int value1 = dice.roll();
                    int value2 = dice.roll();
                    int sum = value1 + value2;
                    controllerGUI.gui.setDice(value1, value2);
                    gameBoard.takePlayerTurn(currentPlayer, sum, dice);
                }
            } catch (NotEnoughBalanceException e) {
                gameIsWon = true;
                controllerGUI.getGameBoard().gameOver(currentPlayer);
            }
        }
    }

    private void switchPlayer(){
        currentPlayer = NextPlayer();
        String playerName = currentPlayer.getName();
        System.out.println(playerName + " turn");
        controllerGUI.gui.showMessage(playerName + " : roll the dice");
    }

    private void testHouseBuilding(Player player, GameBoard gameBoard) throws NotEnoughBalanceException {

        Square[] squares = controllerGUI.getGameBoard().getBoardSquares();

        Square roskildevej = squares[6];
        Square valbyLanggade = squares[8];
        roskildevej.setSoldToPlayer(player);
        valbyLanggade.setSoldToPlayer(player);
        player.decreaseBalanceBy(roskildevej.getFieldPrice());
        player.decreaseBalanceBy(valbyLanggade.getFieldPrice());

        //Make user land on Allegade
        int value1 = 5;
        int value2 = 4;
        int diceValuesSumForAllegade = value1 + value2;
        controllerGUI.gui.setDice(value1, value2);
        gameBoard.takePlayerTurn(currentPlayer, diceValuesSumForAllegade, dice);


        Square frederiksberg = squares[11];
        Square bulowsvej = squares[13];
        frederiksberg.setSoldToPlayer(player);
        bulowsvej.setSoldToPlayer(player);
        player.decreaseBalanceBy(frederiksberg.getFieldPrice());
        player.decreaseBalanceBy(bulowsvej.getFieldPrice());

        //Make 3 more houses on roskildevej so now we have 4 in total and this will not be shown again to user for another house
        roskildevej.increaseBuildHousesCountBy(3);

        //Make user land on Gammel kongevej to buy house
        value1 = 3;
        value2 = 2;
        int diceValuesSumForGammelKongevej = value1 + value2;
        controllerGUI.gui.setDice(value1, value2);
        gameBoard.takePlayerTurn(currentPlayer, diceValuesSumForGammelKongevej, dice);

        //Switch player
        switchPlayer();

        //Make user land on Roskildevej
        value1 = 2;
        value2 = 4;
        diceValuesSumForAllegade = value1 + value2;
        controllerGUI.gui.setDice(value1, value2);
        gameBoard.takePlayerTurn(currentPlayer, diceValuesSumForAllegade, dice);

        //Make user land on valby langgade
        value1 = 1;
        value2 = 1;
        diceValuesSumForAllegade = value1 + value2;
        controllerGUI.gui.setDice(value1, value2);
        gameBoard.takePlayerTurn(currentPlayer, diceValuesSumForAllegade, dice);

        //Make user land on allegade
        value1 = 1;
        value2 = 0;
        diceValuesSumForAllegade = value1 + value2;
        controllerGUI.gui.setDice(value1, value2);
        gameBoard.takePlayerTurn(currentPlayer, diceValuesSumForAllegade, dice);
    }

    public int PlayerStart = -1;

    public Player NextPlayer() {
        PlayerStart++;
        PlayerStart %= controllerGUI.players.length;
        controllerGUI.players[PlayerStart].setPlayerTurn(true);
        return controllerGUI.players[PlayerStart];
    }

}
