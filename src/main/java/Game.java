//import gui_fields.GUI_Player;
//import gui_main.GUI;
//
//public class Game {
//
//    private int playerCount;
//    private int minimumPlayerCount = 2;
//    private int maximumPlayerCount = 4;
//    private Player[] players;
//    private Dice dice = new Dice();
//    private GUI gui;
//    private GameBoard gameBoard;
//    private ControllerGUI controllerGUI = new ControllerGUI();
//
//    public Game() {
//        this.gameBoard = new GameBoard(players);
//        GUI gui = new GUI(gameBoard.getFields());
//        this.gui = gui;
//        gameBoard.setGUI(gui);
//        players = initializeGame();
//        gameBoard.setPlayers(players);
//    }
//
//    private int PlayerStart=-1;
//    private Player nextPlayer(){
//        PlayerStart++;
//        PlayerStart %= controllerGUI.player.length;
//        controllerGUI.player[PlayerStart].setPlayerTurn(true);
//        return controllerGUI.player[PlayerStart];
//    }
//
//    public void start() {
//        Player currentPlayer = nextPlayer();
//        while (true) {
//           try {
//                gameBoard.takePlayerTurn(currentPlayer, dice);
//            } catch (NotEnoughBalanceException e) {
//                handleGameOver(currentPlayer);
//                break;
//            }
//
//            currentPlayer = nextPlayer();
//        }
//    }
//
//    private void handleGameOver(Player currentPlayer) {
//        Player winingPlayer = players[0];
//        for (int i = 0; i < playerCount; i++) {
//            Player player = players[i];
//            if(player == currentPlayer){
//                continue;
//            }
//            if(player.getBalance() > winingPlayer.getBalance()){
//                winingPlayer = player;
//            }
//        }
//
//        gui.showMessage(" ### SPIL SLUT ###" + "\n" + "Vinder" + "\n" + "Navn: " + winingPlayer.getName());
//
//    }
//
////    private Player[] initializeGame() {
////        this.gui.showMessage("                                                                        Velkommen til Matador!");
////
////        this.playerCount = gui.getUserInteger("Indtas antal spiller: " + minimumPlayerCount + " - " + maximumPlayerCount);
////        //playerCount = nextIntFromScanner();
////        while(playerCount < minimumPlayerCount || playerCount > maximumPlayerCount) {
////            this.gui.showMessage("Antal spillere skal være mellem " + minimumPlayerCount + " og " + maximumPlayerCount);
////            playerCount = gui.getUserInteger("Indtast antal spiller: " + minimumPlayerCount + " - " + maximumPlayerCount);
////        }
////        int initialBalance = getPlayerInitialBalance();
////        Player[] players = new Player[playerCount];
////        for (int i = 0; i < playerCount; i++) {
////            String name = gui.getUserString("Indtast spiller " + String.valueOf(i+1) + "'s navn: ");
////            int age = gui.getUserInteger("Indtast spiller " + String.valueOf(i+1) + "'s alder: ");
////            while(age == -1) {
////                age = gui.getUserInteger("Indtast venligst spiller " + String.valueOf(i+1) + "'s alder: ");
////            }
////
////            players[i] = new Player(name, initialBalance);
////            this.gui.addPlayer(players[i].getGuiPlayer());
////            this.gui.getFields()[0].setCar(players[i].getGuiPlayer(), true);
////        }
////        return players;
////    }
//
//
////    private int getPlayerInitialBalance() {
////        if (playerCount == 2) {
////            return 20;
////        }
////        if (playerCount == 3) {
////            return 18;
////        }
////        return 16;
////    }
//
//}
