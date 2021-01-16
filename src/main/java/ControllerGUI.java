import gui_fields.GUI_Car;
import gui_fields.GUI_Field;
import gui_fields.GUI_Player;
import gui_main.GUI;

import java.awt.*;

public class ControllerGUI {

   public Player[] players;
   public GUI gui;
   private GameBoard gameBoard;

    ControllerGUI(){
        gui = new GUI();
        PlayerStart();
    }

    public Player[] getPlayers() {
        return players;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    private void PlayerStart(){
        int number = Integer.parseInt(RequestPlayerAmount());
        System.out.println(number);
        players= new Player[number];
        for (int i = 0; i < number ; i++){
            String PlayerName = gui.getUserString("Enter your name");
            if (PlayerName.equals("")){
                PlayerName = gui.getUserString("Player ") + (i+1);
            }

            GUI_Player guiPlayer = new GUI_Player(PlayerName, 1500, carType(i));
            gui.addPlayer(guiPlayer);
            gui.getFields()[0].setCar(guiPlayer,true);

            players[i] = new Player(guiPlayer,i);
            //PlayerSetup(players[i],number);

            System.out.println(i);
        }

        gameBoard = new GameBoard(gui, players);
    }

//    public void PlayerSetup(Player player, int playeramout){
//
//       if (gui_player==null){
//           gui_player = new GUI_Player[playeramout];
//       }
//       gui_player[player.getPlayerNumber()] = new GUI_Player(player.getName(),player.getBalance(), carType(player.getPlayerNumber()));
//        gui.addPlayer(gui_player[player.getPlayerNumber()]);
//       gui.getFields()[0].setCar(gui_player[player.getPlayerNumber()],true);
//    }


    public GUI_Car carType(int PlayerNumber){

        Color color =CarColor(PlayerNumber);
        String choose =gui.getUserSelection("Hvad type skal du have for din bil","CAR","UFO","TRACTOR","RACECAR");

        switch (choose){
            case "CAR":
                return new GUI_Car(color,color,GUI_Car.Type.CAR,GUI_Car.Pattern.FILL);
            case "RACECAR":
                return new GUI_Car(color,color,GUI_Car.Type.RACECAR,GUI_Car.Pattern.FILL);
            case "TRACTOR":
                return new GUI_Car(color,color,GUI_Car.Type.TRACTOR,GUI_Car.Pattern.FILL);
            case "UFO":
            default:return new GUI_Car(color,color,GUI_Car.Type.UFO,GUI_Car.Pattern.FILL);
        }
    }

    public Color CarColor( int playerNumber){
        switch (playerNumber){

            case 0: return Color.BLACK;
            case 1: return Color.RED;
            case 2: return Color.yellow;
            case 3: return Color.LIGHT_GRAY;
            case 4: return Color.cyan;
            case 5: return Color.green;
            default:return Color.BLUE;
        }
    }

    public String RequestPlayerAmount(){
        return gui.getUserSelection("Hvor manage spiller skal der være","2","3","4","5","6");
    }
}
