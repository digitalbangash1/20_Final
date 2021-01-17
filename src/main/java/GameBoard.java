import Translator.Texts;
import chance.ChanceCard;
import chance.ChanceCardActionType;
import chance.ChanceDeck;
import gui_fields.GUI_Field;
import gui_fields.GUI_Player;
import gui_main.GUI;

public class GameBoard {

    private int squareCount = 40;
    private GUI gui;
    private Player[] players;
    private Square[] boardSquares;
    private ChanceDeck chanceDeck = new ChanceDeck();
    private HouseGroundBlock[] grounds = new HouseGroundBlock[8];
    private boolean didPlayerUseMoveToStartChanceCard;
    private  int previousPosition;

    /**
     * GameBoard Constructor
     *
     * @param gui     take gui as
     * @param players
     */
    public GameBoard(GUI gui, Player[] players) {
        this.gui = gui;
        this.players = players;
        initializeBoard();
    }

    /**
     * This method deals with the logic when the player has a turn and is not in jail.
     * The different methods are called afterwards.
     *
     * @param currentPlayer
     * @param diceValuesSum
     * @param dice
     * @param
     * @throws NotEnoughBalanceException
     */
    public void takePlayerTurn(Player currentPlayer, int diceValuesSum, Dice dice) throws NotEnoughBalanceException {
        didPlayerUseMoveToStartChanceCard = false;
        previousPosition = currentPlayer.getPlayerPosition();
        boolean prison = handleAnySquareBefore(currentPlayer, dice);

        if (prison) {
            return;
        }

        takeTurn(currentPlayer, diceValuesSum);
    }

    private void takeTurn(Player currentPlayer, int diceValuesSum) throws NotEnoughBalanceException {
        int PlayerNewPosition = CalculateNewPlayerPosition(currentPlayer, diceValuesSum);
        MoveCar(currentPlayer, PlayerNewPosition);
        Square boardSquare = boardSquares[PlayerNewPosition];
        printBoardSquare(currentPlayer, boardSquare);
        evaluateSquare(boardSquare, currentPlayer);
        handleAnySquareAfter(currentPlayer, PlayerNewPosition);
        currentPlayer.setBalance(currentPlayer.getBalance());
        handleBuildingHouse(currentPlayer, boardSquare);
        handlePassingOfStartSquare(currentPlayer, previousPosition);
    }

    public Square[] getBoardSquares() {
        return boardSquares;
    }

    private void handlePassingOfStartSquare(Player currentPlayer, int previousPosition) {
        int currentPosition = currentPlayer.getPlayerPosition();
        boolean didPlayerPassStart = currentPosition >= 0 && currentPosition <= previousPosition;
        if (didPlayerPassStart && !currentPlayer.isInPrison() && !didPlayerUseMoveToStartChanceCard) {
            gui.showMessage(Texts.passedstart);
            currentPlayer.increaseBalanceBy(200);
        }
    }

    private void handleBuildingHouse(Player player, Square boardSquare) throws NotEnoughBalanceException {

        Square[] blueBlockSquares = getOwnSquaresByColor(player, HouseColor.blue);
        Square[] pinkBlockSquares = getOwnSquaresByColor(player, HouseColor.pink);
        Square[] greenBlockSquares = getOwnSquaresByColor(player, HouseColor.green);
        Square[] grayBlockSquares = getOwnSquaresByColor(player, HouseColor.gray);
        Square[] redBlockSquares = getOwnSquaresByColor(player, HouseColor.red);
        Square[] whiteBlockSquares = getOwnSquaresByColor(player, HouseColor.white);
        Square[] yellowBlockSquares = getOwnSquaresByColor(player, HouseColor.yellow);
        Square[] purpleBlockSquares = getOwnSquaresByColor(player, HouseColor.purple);

        int totalCount = blueBlockSquares.length + pinkBlockSquares.length + greenBlockSquares.length +
                grayBlockSquares.length + redBlockSquares.length + whiteBlockSquares.length +
                yellowBlockSquares.length + purpleBlockSquares.length;

        if (totalCount <= 0) {
            return;
        }

        Square[] totalSquares = copyBuildingSquaresIntoArray(totalCount, blueBlockSquares, pinkBlockSquares, greenBlockSquares,
                grayBlockSquares, redBlockSquares, whiteBlockSquares,
                yellowBlockSquares, purpleBlockSquares);
        String[] buildingSquareChoices = new String[totalSquares.length];
        for (int i = 0; i < totalSquares.length; i++) {
            buildingSquareChoices[i] = totalSquares[i].getTitle();
        }
        boolean buildHouseChoice = gui.getUserLeftButtonPressed(Texts.vilDuByggeHus, Texts.ja, Texts.nej);
        if (!buildHouseChoice) {
            return;
        }
        String squareTitleChoiceForHouse = gui.getUserSelection(Texts.hvorViDuByggeHus, buildingSquareChoices);
        Square selectedSquare = null;
        for (int i = 0; i < boardSquares.length; i++) {
            Square s = boardSquares[i];
            if (s.getTitle().equalsIgnoreCase(squareTitleChoiceForHouse)) {
                selectedSquare = s;
                break;
            }
        }

        if (selectedSquare == null) {
            return;
        }

        selectedSquare.increaseBuildHousesCountBy(1);
        player.decreaseBalanceBy(selectedSquare.getHousePrice());
        GUI_Player guiPlayer = player.getGuiPlayer();
        guiPlayer.setName(player.getName() + " (Hus: " + selectedSquare.getBuiltHousesCount() + ")");
    }

    private Square[] copyBuildingSquaresIntoArray(int totalCount, Square[]... sources) {
        int startIndex = 0;
        Square[] totalSquares = new Square[totalCount];
        for (int i = 0; i < sources.length; i++) {
            Square[] source = sources[i];
            for (int j = 0; j < source.length; j++) {
                totalSquares[startIndex] = source[j];
                startIndex++;
            }
        }
        return totalSquares;
    }

    private int getHouseGroundBlockCount(HouseColor color) {
        for (int i = 0; i < grounds.length; i++) {
            HouseGroundBlock block = grounds[i];
            if (block.getColor() == color) {
                return block.getCount();
            }
        }
        return 0;
    }

    private int getSquaresCountOwnedByPlayerWhereHouseCanBeBuilt(Player player, HouseColor color) {
        int count = 0;
        for (int i = 0; i < boardSquares.length; i++) {
            Square s = boardSquares[i];
            if (s.isOwnable() && s.getColor() == color && s.getSoldToPlayer() == player && s.canBuildAnotherHouse()) {
                count++;
            }
        }
        return count;
    }

    private int getSquaresCountOwnedByPlayer(Player player, HouseColor color) {
        int count = 0;
        for (int i = 0; i < boardSquares.length; i++) {
            Square s = boardSquares[i];
            if (s.isOwnable() && s.getColor() == color && s.getSoldToPlayer() == player) {
                count++;
            }
        }
        return count;
    }

    private Square[] getOwnSquaresByColor(Player player, HouseColor color) {

        int blockCount = getHouseGroundBlockCount(color);
        int ownedByPlayerCount = getSquaresCountOwnedByPlayer(player, color);
        int availForBuildingHousesCount = getSquaresCountOwnedByPlayerWhereHouseCanBeBuilt(player, color);

        if (blockCount != ownedByPlayerCount) {
            return new Square[0];
        }

        Square[] ownSquares = new Square[availForBuildingHousesCount];
        int ownSquaresIndex = 0;
        for (int i = 0; i < boardSquares.length; i++) {
            Square s = boardSquares[i];
            if (s.isOwnable() && s.getColor() == color && s.getSoldToPlayer() == player) {
                if (s.canBuildAnotherHouse()) {
                    ownSquares[ownSquaresIndex] = s;
                    ownSquaresIndex++;
                }
            }
        }
        return ownSquares;
    }

    /**
     * This method deals with the win function and anounce player.
     *
     * @param currentPlayer
     */
    public void gameOver(Player currentPlayer) {
        Player winner = players[0];
        for (int i = 1; i < players.length; i++) {
            if (players[i].getBalance() > winner.getBalance()) {
                winner = players[i];
            }
        }
        gui.showMessage("Player " + winner.getName() + " has won the game!");
    }

    /**
     * This method is called every time when the player lands on field.
     * After landing the system check which square it is and initiate the method needed to evaluate
     *
     * @param square
     * @param currentPlayer
     * @throws NotEnoughBalanceException
     */
    private void evaluateSquare(Square square, Player currentPlayer) throws NotEnoughBalanceException {
        switch (square.getSquareType()) {
            case DoNothing:
                handleNothingSquare(currentPlayer);
                break;
            case Start:
                handleStartSquare(currentPlayer);
                break;
            case Payment:
                handlePaymentSquare(currentPlayer, square);
                break;
            case IndomstSkat:
                handleSkatSquare(currentPlayer, square);
                break;
            case Ekstraordinærskat:
                handleEkstaordinaerSkatSquare(currentPlayer, square);
                break;
            case GotoJail:
                handleGotoJailSquare(currentPlayer);
                break;
            case TakeChanceCard:
                handleTakeChanceCardSquare(currentPlayer);
                break;
        }
    }

    /**
     * Method deals with the player landing on the start
     * increases balance by amount
     *
     * @param currentPlayer
     */
    private void handleStartSquare(Player currentPlayer) {

    }


    private boolean checkIndex(int nextIndex) {
        if (nextIndex >= squareCount) {
            int temp = 0;
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method prints the messages to the screen.
     *
     * @param player
     * @param square
     */
    private void printBoardSquare(Player player, Square square) {
        //Special cases, where the field should not be printed
        if (square.getSquareType() == SquareType.Prison) {
            return;
        } else if (square.getSquareType() == SquareType.TakeChanceCard) {
            return;
        }
        this.gui.showMessage(player.getName() + ":" + " Du er landet på " + " ---> " + square.getTitle());
    }

    /**
     * This method handles with when the player is in jail.
     * PLayer is asked about if the player wants to pay or to throw dice.
     *
     * @param currentPlayer
     * @param dice
     * @return
     * @throws NotEnoughBalanceException
     */
    private boolean handleAnySquareBefore(Player currentPlayer, Dice dice) throws NotEnoughBalanceException {
        return handleJail(currentPlayer, dice);
    }

    private boolean handleJail(Player currentPlayer, Dice dice) throws NotEnoughBalanceException {
        if (!currentPlayer.isInPrison()) {
            return false;
        }

        if (currentPlayer.hasJailFreeCard()) {
            gui.showMessage(Texts.outnow);
            currentPlayer.setInPrison(false);
            return false;
        }

        boolean rollChoice = gui.getUserLeftButtonPressed(currentPlayer.getName() + Texts.jailout, Texts.roll, Texts.pay50);
        if (rollChoice) {
            int dice1Value = dice.roll();
            int dice2Value = dice.roll();
            this.gui.setDice(dice1Value, dice2Value);
            if (dice1Value == dice2Value) {
                gui.showMessage(Texts.twosame);
                currentPlayer.setInPrison(false);
                takeTurn(currentPlayer, dice1Value + dice2Value);
                return true; //player hist two same and is out of prison, but does not get another turn
            } else {
                gui.showMessage(Texts.notsame);
                return true;
            }
        } else {
            currentPlayer.decreaseBalanceBy(50);
            gui.showMessage(Texts.ticket);
            currentPlayer.setInPrison(false);
            return false;
        }
    }

    private void handleAnySquareAfter(Player currentPlayer, int nextIndex) {

    }

    private void handleNothingSquare(Player currentPlayer) {

    }

    /**
     * This method handles when the player lands on the payment fields
     *
     * @param currentPlayer
     * @param boardSquare
     * @throws NotEnoughBalanceException
     */
    private void handlePaymentSquare(Player currentPlayer, Square boardSquare) throws NotEnoughBalanceException {


        int fieldPrice = boardSquare.getFieldPrice();
        Player soldToPlayer = boardSquare.getSoldToPlayer();

        // if the soldToPlayer is the same as the currentPlayer then do not do anything because
        // the currentPlayer already owns this square
        if (soldToPlayer == currentPlayer) {
            gui.showMessage(Texts.youownit);
        } else if (soldToPlayer == null) {
            boolean choice = gui.getUserLeftButtonPressed(
                    currentPlayer.getName() + ":" + Texts.willyoubuy + "    " + Texts.field + "   " + boardSquare.getTitle() + "      " + Texts.price + fieldPrice, Texts.ja, Texts.nej);
            // the first player on this square becomes the owner and pays the price
            if (choice) {
                currentPlayer.decreaseBalanceBy(fieldPrice);
                boardSquare.setSoldToPlayer(currentPlayer);
            }

        } else {
            int rent = boardSquare.getRent();
            // other players coming to this square pays to the owner and become one of the renters
            gui.showMessage(Texts.landonotherfield + rent);
            soldToPlayer.increaseBalanceBy(rent);
            currentPlayer.decreaseBalanceBy(rent);
            // boardSquare.addRentedToPlayer(currentPlayer);
        }
    }


    private void handleGotoJailSquare(Player currentPlayer) {
        currentPlayer.setInPrison(true);
        gui.showMessage(Texts.goingToJail);
        int prisonIndex = getSquareIndexByType(SquareType.Prison);
        MoveCar(currentPlayer, prisonIndex);
    }

    private void handleSkatSquare(Player currentPlayer, Square square) throws NotEnoughBalanceException {
        boolean tenPercent = gui.getUserLeftButtonPressed(Texts.indomstSkat, "10%", "200kr,-");
        int amountToPay = 200;
        if (tenPercent) {
            amountToPay = (int) (currentPlayer.getBalance() * 0.1);
        }
        gui.showMessage(Texts.youpay + "  " + amountToPay + Texts.intaxes);
        currentPlayer.decreaseBalanceBy(amountToPay);
    }

    private void handleEkstaordinaerSkatSquare(Player currentPlayer, Square square) throws NotEnoughBalanceException {
        gui.showMessage(Texts.ekstraOrdinaerSkat);
        currentPlayer.decreaseBalanceBy(100);
    }


    /**
     * method is used to handle chance card
     *
     * @param currentPlayer
     * @throws NotEnoughBalanceException
     */
    private void handleTakeChanceCardSquare(Player currentPlayer) throws NotEnoughBalanceException {
        gui.showMessage(currentPlayer.getName() + ":" + Texts.proeveLykken);
        ChanceCard chanceCard = chanceDeck.getRandomChanceCard();
        String text = chanceCard.getText();
        gui.displayChanceCard(text);
        ChanceCardActionType action = chanceCard.getActionType();
        switch (action) {
            case Start:
                didPlayerUseMoveToStartChanceCard = true;
                MoveCar(currentPlayer, 0);
                break;
            case Move:
                int moveBy = chanceCard.getMove();
                MoveCar(currentPlayer, moveBy);
                Square boardSquare = boardSquares[currentPlayer.getPlayerPosition()];
                evaluateSquare(boardSquare, currentPlayer);
                break;
            case Pay:
                currentPlayer.decreaseBalanceBy(chanceCard.getValue());
                break;
            case GetPaid:
                currentPlayer.increaseBalanceBy(chanceCard.getValue());
                break;
            case GotoPrison:
                handleGotoJailSquare(currentPlayer);
                break;
            case Prison:
                if (!currentPlayer.hasJailFreeCard()) {
                    currentPlayer.setGetOutOfJailCard();
                }
                break;
            case PaidbyOthers:
                for (int i = 0; i < players.length; i++) {
                    if (players[i] != currentPlayer) {
                        players[i].decreaseBalanceBy(chanceCard.getValue());
                    }
                    currentPlayer.increaseBalanceBy(25);
                }
                break;
            case Raadhuspladsen:
                MoveCar(currentPlayer, chanceCard.getMove());
                break;
            case CrossingStart:
                if (currentPlayer.getPlayerPosition() > chanceCard.getMove()) {
                    currentPlayer.increaseBalanceBy(chanceCard.getValue());
                    //currentPlayer.setCurrentSquareIndex(gui, 0);
                    MoveCar(currentPlayer, chanceCard.getMove());
                    break;
                }
        }
    }

    /**
     * Calaculates the new poistion of the current player after throw
     *
     * @param currentPlayer
     * @param dicValuesSum
     * @return
     */

    public int CalculateNewPlayerPosition(Player currentPlayer, int dicValuesSum) {
        return (currentPlayer.getPlayerPosition() + dicValuesSum) % gui.getFields().length;
    }

    /**
     * This method moves the car from the old pos to new pos and set players new position. The display is also shown here.
     *
     * @param currentPlayer
     * @param PlayerNewPosition
     */
    public void MoveCar(Player currentPlayer, int PlayerNewPosition) {
        GUI_Field[] fields = gui.getFields();
        int CurrentPosition = currentPlayer.getPlayerPosition();
        int newPosition = CurrentPosition - Math.abs(PlayerNewPosition);
        if (PlayerNewPosition < 0 && newPosition < 0) {
            PlayerNewPosition = fields.length - Math.abs(newPosition);
        }

        GUI_Player guiPlayer = currentPlayer.getGuiPlayer();
        fields[CurrentPosition].setCar(guiPlayer, false);
        fields[PlayerNewPosition].setCar(guiPlayer, true);
        currentPlayer.setPlayerPosition(PlayerNewPosition);
    }

    private int getSquareIndexByType(SquareType squareType) {
        for (int i = 0; i < squareCount; i++) {
            Square boardSquare = boardSquares[i];
            if (boardSquare.getSquareType() == squareType) {
                return i;
            }
        }
        return 0;
    }


    private void initializeBoard() {

        grounds[0] = new HouseGroundBlock(HouseColor.blue, 2);
        grounds[1] = new HouseGroundBlock(HouseColor.pink, 3);
        grounds[2] = new HouseGroundBlock(HouseColor.green, 3);
        grounds[3] = new HouseGroundBlock(HouseColor.gray, 3);
        grounds[4] = new HouseGroundBlock(HouseColor.red, 3);
        grounds[5] = new HouseGroundBlock(HouseColor.white, 3);
        grounds[6] = new HouseGroundBlock(HouseColor.yellow, 3);
        grounds[7] = new HouseGroundBlock(HouseColor.purple, 2);

        Square start = new Square("Start", 0, 0, SquareType.Start);
        Square Rødovrevej = new Square("Rødovrevej", 60, 2, new int[]{10, 30, 90, 160}, SquareType.Payment, HouseColor.blue, 50);
        Square Prøvlykken = new Square("Prøv Lykken", 0, 0, SquareType.TakeChanceCard);
        Square chance1 = new Square("Chance", 0, 0, SquareType.TakeChanceCard);
        Square Hvidovrevej = new Square("Hvidovrevej", 60, 4, new int[]{20, 60, 180, 320}, SquareType.Payment, HouseColor.blue, 50);
        Square BetalIndomstSkat = new Square("Betal Indokmst skat, 10% el. 200", 200, 0, SquareType.IndomstSkat);
        Square Øresund = new Square("Øresund", 200, 25, SquareType.Payment);
        Square Roskildevej = new Square("Roskildevej", 100, 6, new int[]{30, 90, 270, 400}, SquareType.Payment, HouseColor.pink, 50);
        Square ValbyLanggade = new Square("Valby Langade", 100, 6, new int[]{30, 90, 270, 400}, SquareType.Payment, HouseColor.pink, 50);
        Square Allégade = new Square("Allégade", 120, 8, new int[]{40, 100, 300, 450}, SquareType.Payment, HouseColor.pink, 50);
        Square Gratisparkering = new Square("Gratis Parkering", 0, 0, SquareType.Prison);
        Square FrederiksbergAlle = new Square("Frederiksberg Allé", 140, 10, new int[]{50, 150, 450, 625}, SquareType.Payment, HouseColor.green, 100);
        Square Tuborg = new Square("Tuborg", 150, 10, SquareType.Payment);
        Square Bülowsvej = new Square("Bülowsvej", 140, 10, new int[]{50, 150, 450, 625}, SquareType.Payment, HouseColor.green, 100);
        Square GammelKongevej = new Square("Gammel Kongevej", 140, 12, new int[]{60, 180, 500, 700}, SquareType.Payment, HouseColor.green, 100);
        Square DFDS = new Square("D.F.D.S", 200, 25, SquareType.Payment);
        Square Bernstorffsvej = new Square("Bernstorffsvej", 180, 14, new int[]{70, 200, 550, 750}, SquareType.Payment, HouseColor.gray, 100);
        Square Hellerupvej = new Square("Hellerupvej", 180, 14, new int[]{70, 200, 550, 750}, SquareType.Payment, HouseColor.gray, 100);
        Square Strandvejen = new Square("Strandvejen", 180, 16, new int[]{80, 220, 600, 800}, SquareType.Payment, HouseColor.gray, 100);
        Square Helle = new Square("helle", 0, 0, SquareType.DoNothing);
        Square Trianglen = new Square("Trianglen", 200, 18, new int[]{90, 250, 700, 875}, SquareType.Payment, HouseColor.red, 150);
        Square Østerbrogade = new Square("Østerbrogade", 220, 18, new int[]{90, 250, 700, 875}, SquareType.Payment, HouseColor.red, 150);
        Square Grønningen = new Square("Grønningen", 240, 20, new int[]{100, 300, 750, 925}, SquareType.Payment, HouseColor.red, 150);
        Square ØS = new Square("Ø.S", 200, 25, SquareType.Payment);
        Square Bredgade = new Square("Bredgade", 260, 22, new int[]{110, 330, 800, 975}, SquareType.Payment, HouseColor.white, 150);
        Square KgsNytorv = new Square("Kgs. Nytorv", 260, 22, new int[]{110, 330, 800, 975}, SquareType.Payment, HouseColor.white, 150);
        Square Carlsberg = new Square("Carlsberg", 150, 10, SquareType.Payment);
        Square Østergade = new Square("Østergade", 280, 22, new int[]{120, 360, 850, 1025}, SquareType.Payment, HouseColor.white, 150);
        Square GaaIFaengsel = new Square("Gå i Fængsel", 0, 0, SquareType.GotoJail);
        Square Amagertorv = new Square("Amagertorv", 300, 26, new int[]{130, 390, 900, 1100}, SquareType.Payment, HouseColor.yellow, 200);
        Square Vimmelskaftet = new Square("Vimmelskaftet", 300, 26, new int[]{130, 390, 900, 1100}, SquareType.Payment, HouseColor.yellow, 200);
        Square Nygade = new Square("Nygade", 320, 28, new int[]{150, 450, 1000, 1200}, SquareType.Payment, HouseColor.yellow, 200);
        Square Bornholm = new Square("Bornholm", 200, 25, SquareType.Payment);
        Square Frederiksberggade = new Square("Frederiksberggade", 350, 35, new int[]{175, 500, 1100, 1300}, SquareType.Payment, HouseColor.purple, 200);
        Square Skat = new Square("Ekstra-\nordinær\nstatsskat", 100, 0, SquareType.Ekstraordinærskat);
        Square Raadhuspladsen = new Square("Rådhuspladsen", 400, 50, new int[]{200, 600, 1400, 1700}, SquareType.Payment, HouseColor.purple, 200);


        this.boardSquares = new Square[squareCount];
        boardSquares[0] = start;
        boardSquares[1] = Rødovrevej;
        boardSquares[2] = chance1;
        boardSquares[3] = Hvidovrevej;
        boardSquares[4] = BetalIndomstSkat;
        boardSquares[5] = Øresund;
        boardSquares[6] = Roskildevej;
        boardSquares[7] = chance1;
        boardSquares[8] = ValbyLanggade;
        boardSquares[9] = Allégade;
        boardSquares[10] = Gratisparkering;
        boardSquares[11] = FrederiksbergAlle;
        boardSquares[12] = Tuborg;
        boardSquares[13] = Bülowsvej;
        boardSquares[14] = GammelKongevej;
        boardSquares[15] = DFDS;
        boardSquares[16] = Bernstorffsvej;
        boardSquares[17] = chance1;
        boardSquares[18] = Hellerupvej;
        boardSquares[19] = Strandvejen;
        boardSquares[20] = Helle;
        boardSquares[21] = Trianglen;
        boardSquares[22] = chance1;
        boardSquares[23] = Østerbrogade;
        boardSquares[24] = Grønningen;
        boardSquares[25] = ØS;
        boardSquares[26] = Bredgade;
        boardSquares[27] = KgsNytorv;
        boardSquares[28] = Carlsberg;
        boardSquares[29] = Østergade;
        boardSquares[30] = GaaIFaengsel;
        boardSquares[31] = Amagertorv;
        boardSquares[32] = Vimmelskaftet;
        boardSquares[33] = chance1;
        boardSquares[34] = Nygade;
        boardSquares[35] = Bornholm;
        boardSquares[36] = chance1;
        boardSquares[37] = Frederiksberggade;
        boardSquares[38] = Skat;
        boardSquares[39] = Raadhuspladsen;
    }
}