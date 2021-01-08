public class ControllerGame {

    ControllerGUI controllerGUI = new ControllerGUI();
    public Player player;

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

        player= NextPlayer();
        System.out.println(player.getName()+  "turn");

        controllerGUI.gui.showMessage(" Do you  want to roll the dice?");
        int value1 = dice.roll();
        int value2 = dice.roll();
        int sum=value1 + value2;
        player.setCurrentSquareIndex(controllerGUI.gui, sum);
        controllerGUI.gui.setDice(value1,value2);
    }

    public void MoveCar(){

        int CurrentPosition= player.getPlayerPosition();
        int PlayerNewPosition =  (player.getPlayerPosition() + player.getPlayerNewPo()) % controllerGUI.gui.getFields().length;

        try {
                controllerGUI.gui.getFields()[CurrentPosition].setCar(controllerGUI.gui_player[player.getPlayerNumber()],false);
                controllerGUI.gui.getFields()[PlayerNewPosition].setCar(controllerGUI.gui_player[player.getPlayerNumber()],true);
        }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println(" IndexOutOfBoundsException");
        }

        player.setPlayerPosition(PlayerNewPosition);
    }
}
