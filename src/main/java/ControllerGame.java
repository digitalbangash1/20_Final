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

            currentPlayer= NextPlayer();
            String playerName = currentPlayer.getName();
            System.out.println(playerName +  " turn");

            controllerGUI.gui.showMessage(playerName + " : roll the dice");
            int value1 = dice.roll();
            int value2 = dice.roll();
            int sum=value1 + value2;

            currentPlayer.setPlayerNewPosition(sum);
            int CurrentPosition= currentPlayer.getPlayerPosition();
            int PlayerNewPosition =  (currentPlayer.getPlayerPosition() + currentPlayer.getPlayerNewPo()) % controllerGUI.gui.getFields().length;
            controllerGUI.gui.setDice(value1,value2);

            try {
                controllerGUI.getGameBoard().takePlayerTurn(currentPlayer, PlayerNewPosition, sum, dice);
                currentPlayer.getGuiPlayer().setBalance(currentPlayer.getBalance());
            } catch (NotEnoughBalanceException e) {
                //handleGameOver(currentPlayer);
            }

            MoveCar(CurrentPosition, PlayerNewPosition);
            currentPlayer.setPlayerPosition(PlayerNewPosition);
        }
    }

    public int PlayerStart=-1;
    public Player NextPlayer(){
        PlayerStart++;
        PlayerStart %= controllerGUI.players.length;
        controllerGUI.players[PlayerStart].setPlayerTurn(true);
        return controllerGUI.players[PlayerStart];
    }

    public void MoveCar(int CurrentPosition, int PlayerNewPosition){

        try {
            GUI_Field[] fields = controllerGUI.gui.getFields();
            GUI_Player guiPlayer = currentPlayer.getGuiPlayer();
            fields[CurrentPosition].setCar(guiPlayer,false);
            fields[PlayerNewPosition].setCar(guiPlayer,true);
        }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println(" IndexOutOfBoundsException");
        }
    }
}
