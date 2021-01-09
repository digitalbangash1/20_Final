import gui_fields.GUI_Field;
import gui_fields.GUI_Player;

public class ControllerGame {

    ControllerGUI controllerGUI = new ControllerGUI();
    public Player currentPlayer;

    public Dice dice = new Dice();
    private boolean win=false;

    ControllerGame(){
        Start();
    }

    private void Start(){
        while (!win){
            Roll();
            MoveCar();
        }
    }

    public int PlayerStart=-1;
    public Player NextPlayer(){
        PlayerStart++;
        PlayerStart %= controllerGUI.players.length;
        controllerGUI.players[PlayerStart].setPlayerTurn(true);
        return controllerGUI.players[PlayerStart];
    }

    public void Roll(){

        currentPlayer= NextPlayer();
        String playerName = currentPlayer.getName();
        System.out.println(playerName +  " turn");

        controllerGUI.gui.showMessage(playerName + " : roll the dice");
        int value1 = dice.roll();
        int value2 = dice.roll();
        int sum=value1 + value2;
        currentPlayer.setPlayerNewPosition(sum);
        controllerGUI.gui.setDice(value1,value2);

        try {
            controllerGUI.getGameBoard().takePlayerTurn(currentPlayer, sum, dice);
            currentPlayer.getGuiPlayer().setBalance(currentPlayer.getBalance());
        } catch (NotEnoughBalanceException e) {
            //handleGameOver(currentPlayer);
        }
    }

    public void MoveCar(){

        int CurrentPosition= currentPlayer.getPlayerPosition();
        int PlayerNewPosition =  (currentPlayer.getPlayerPosition() + currentPlayer.getPlayerNewPo()) % controllerGUI.gui.getFields().length;

        try {
            GUI_Field[] fields = controllerGUI.gui.getFields();
            GUI_Player guiPlayer = currentPlayer.getGuiPlayer();
            fields[CurrentPosition].setCar(guiPlayer,false);
            fields[PlayerNewPosition].setCar(guiPlayer,true);
        }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println(" IndexOutOfBoundsException");
        }

        currentPlayer.setPlayerPosition(PlayerNewPosition);
    }
}