import Translator.Texts;
import chance.ChanceCard;
import chance.ChanceCardActionType;
import chance.ChanceDeck;
import gui_fields.GUI_Field;
import gui_fields.GUI_Player;
import gui_main.GUI;

public class GameBoard {

    private int squareCount = 40;
    private int chanceCount = 7;
    private GUI gui;
    private Player[] players;
    private Square[] boardSquares;
    private ChanceDeck chanceDeck = new ChanceDeck();

    public GameBoard(GUI gui, Player[] players) {
        this.gui = gui;
        this.players = players;
        initializeBoard();
    }

    public void takePlayerTurn(Player currentPlayer, int diceValuesSum, Dice dice) throws NotEnoughBalanceException {
        boolean prison = handleAnySquareBefore(currentPlayer, dice);
        if (!prison) {
            int PlayerNewPosition = CalculateNewPlayerPosition(currentPlayer, diceValuesSum);
            Square boardSquare;
            boardSquare = boardSquares[PlayerNewPosition];
            printBoardSquare(currentPlayer, boardSquare);
            evaluateSquare(boardSquare, currentPlayer);
            handleAnySquareAfter(currentPlayer, PlayerNewPosition);
            currentPlayer.setBalance(currentPlayer.getBalance());
            MoveCar(currentPlayer, PlayerNewPosition);
        }
    }

    public void gameOver(Player currentPlayer) {
        Player winner = players[0];
        for (int i = 1; i < players.length; i++) {
            if (players[i].getBalance() > winner.getBalance()) {
                winner = players[i];
            }
        }
        gui.showMessage("Player " + winner.getName() + " has won the game!");
    }

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
            case GotoJail:
                handleGotoJailSquare(currentPlayer);
                break;
            case TakeChanceCard:
                handleTakeChanceCardSquare(currentPlayer);
                break;
        }
    }

    private void handleStartSquare(Player currentPlayer) {
        gui.showMessage(Texts.start);
        currentPlayer.increaseBalanceBy(200);
    }

//    private int movePlayer(Player currentPlayer, int diceValue) {
//
//        //Calculate next index
//        int nextIndex = currentPlayer.getCurrentSquareIndex() + diceValue;
//        //Set players current index to this
//        int currentIndex = nextIndex % squareCount;
//        //Check if the player has once again reached start
//        boolean passedStart = checkIndex(nextIndex);
//
//        if (passedStart) {
//            currentIndex = nextIndex - squareCount;
//            currentPlayer.setCurrentSquareIndex(this.gui, currentIndex);
//        } else {
//            currentPlayer.setCurrentSquareIndex(this.gui, currentIndex);
//        }
//        return currentIndex;
//    }

    private boolean checkIndex(int nextIndex) {
        if (nextIndex >= squareCount) {
            int temp = 0;
            return true;
        } else {
            return false;
        }
    }

    private void printBoardSquare(Player player, Square square) {
        //Special cases, where the field should not be printed
        if (square.getSquareType() == SquareType.Prison) {
            return;
        } else if (square.getSquareType() == SquareType.TakeChanceCard) {
            return;
        }
        this.gui.showMessage(player.getName() + ", felt: " + square.getTitle());
    }

    private boolean handleAnySquareBefore(Player currentPlayer, Dice dice) throws NotEnoughBalanceException {
        if (currentPlayer.isInPrison()) {
            if (currentPlayer.hasJailFreeCard()) {
                boolean choice1 = gui.getUserLeftButtonPressed(Texts.jailfreecard, Texts.ja, Texts.nej);
                if (choice1) {
                    currentPlayer.setInPrison(false);
                    return false;
                } else {
                    boolean choice = gui.getUserLeftButtonPressed(currentPlayer.getName() + Texts.jailout, Texts.roll, Texts.pay50);
                    if (choice) {
                        int diceValue = dice.roll();
                        this.gui.setDie(diceValue);
                        if (diceValue == 6) {
                            gui.showMessage(Texts.twosame);
                            MoveCar(currentPlayer, diceValue);
                            currentPlayer.setInPrison(false);
                            return false;
                        } else {
                            gui.showMessage(Texts.notsame);
                            return true;
                        }
                    } else {
                        currentPlayer.decreaseBalanceBy(50);
                        String button = this.gui.getUserButtonPressed(Texts.cameoutofjail, Texts.roll);
                        if (button.equals(Texts.roll)) {
                            int diceValue = dice.roll();
                            this.gui.setDie(diceValue);
                            currentPlayer.setInPrison(false);
                            MoveCar(currentPlayer, diceValue);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void handleAnySquareAfter(Player currentPlayer, int nextIndex) {
        if (nextIndex >= squareCount) {
            currentPlayer.increaseBalanceBy(200);
            gui.showMessage(Texts.passedstart);
        }
    }

    private void handleNothingSquare(Player currentPlayer) {

    }

    private void handlePaymentSquare(Player currentPlayer, Square boardSquare) throws NotEnoughBalanceException {

        //Square boardSquare;
        //boardSquare = boardSquares[currentPlayer.getPlayerPosition()];
        int fieldPrice = boardSquare.getFieldPrice();
        Player soldToPlayer = boardSquare.getSoldToPlayer();

        // if the soldToPlayer is the same as the currentPlayer then do not do anything because
        // the currentPlayer already owns this square
        if (soldToPlayer == currentPlayer) {
            gui.showMessage(Texts.youownit);
        } else if (soldToPlayer == null) {
            boolean choice = gui.getUserLeftButtonPressed(
                    Texts.willyoubuy + "    " + Texts.field + boardSquare.getTitle() + "      " + Texts.price + fieldPrice, Texts.ja, Texts.nej);
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
        currentPlayer.setPlayerPosition(prisonIndex);
    }


    private void handleTakeChanceCardSquare(Player currentPlayer) throws NotEnoughBalanceException {
        gui.showMessage(Texts.proeveLykken);
        ChanceCard chanceCard = chanceDeck.getRandomChanceCard();
        String text = chanceCard.getText();
        gui.displayChanceCard(text);
        ChanceCardActionType action = chanceCard.getActionType();
        switch (action) {
            case Start:
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
                //hideOldPosition();
                //currentPlayer.setPlayerPosition(0);
                //currentPlayer.setPlayerNewPosition(chanceCard.getMove());
                MoveCar(currentPlayer, 0);
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

    public int CalculateNewPlayerPosition(Player currentPlayer, int dicValuesSum) {
        return (currentPlayer.getPlayerPosition() + dicValuesSum) % gui.getFields().length;
    }

    public void MoveCar(Player currentPlayer, int PlayerNewPosition) {

//        try {
        int CurrentPosition = currentPlayer.getPlayerPosition();
        GUI_Field[] fields = gui.getFields();
        GUI_Player guiPlayer = currentPlayer.getGuiPlayer();
        fields[CurrentPosition].setCar(guiPlayer, false);
        fields[PlayerNewPosition].setCar(guiPlayer, true);
        currentPlayer.setPlayerPosition(PlayerNewPosition);

//        }catch (IndexOutOfBoundsException e){
//            e.printStackTrace();
//            System.out.println(" IndexOutOfBoundsException");
//        }
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
        // Initialize chance cards
        //this.chanceCards = initializeCards();
        // Squares
        Square start = new Square("Start", 0, 0, SquareType.Start);
        Square Rødovrevej = new Square("Rødovrevej", 60, 20, SquareType.Payment);
        Square Prøvlykken = new Square("Prøv Lykken", 0, 0, SquareType.TakeChanceCard);
        Square chance1 = new Square("Chance", 0, 0, SquareType.TakeChanceCard);
        Square Hvidovrevej = new Square("Hvidovrevej", 60, 20, SquareType.Payment);
        Square BetalIndomstSkat = new Square("Betal Indokmst skat, 10 el. 200", 200, 0, SquareType.Payment);
        Square Øresund = new Square("Øresund", 200, 75, SquareType.Payment);
        Square Roskildevej = new Square("Roskildevej", 100, 40, SquareType.Payment);
        Square ValbyLanggade = new Square("Valby Langade", 100, 40, SquareType.Payment);
        Square Allégade = new Square("Allégade", 120, 45, SquareType.Payment);
        Square Gratisparkering = new Square("Gratis Parkering", 0, 0, SquareType.DoNothing);
        Square FrederiksbergAlle = new Square("Frederiks-\nberg Allé", 140, 50, SquareType.Payment);
        Square Tuborg = new Square("Tuborg", 150, 10, SquareType.Payment);
        Square Bülowsvej = new Square("Bülowsvej", 140, 50, SquareType.Payment);
        Square GammelKongevej = new Square("Gammel Kongevej", 140, 50, SquareType.Payment);
        Square DFDS = new Square("D.F.D.S", 200, 75, SquareType.Payment);
        Square Bernstorffsvej = new Square("Bernstorffsvej", 180, 60, SquareType.Payment);
        Square Hellerupvej = new Square("Hellerupvej", 180, 60, SquareType.Payment);
        Square Strandvejen = new Square("Strandvejen", 180, 60, SquareType.Payment);
        Square Helle = new Square("helle", 0, 0, SquareType.DoNothing);
        Square Trianglen = new Square("Trianglen", 200, 70, SquareType.Payment);
        Square Østerbrogade = new Square("Østerbro-\ngade", 220, 70, SquareType.Payment);
        Square Grønningen = new Square("Grønningen", 240, 80, SquareType.Payment);
        Square ØS = new Square("Ø.S", 200, 75, SquareType.Payment);
        Square Bredgade = new Square("Bredgade", 260, 80, SquareType.Payment);
        Square KgsNytorv = new Square("Kgs. Nytorv", 260, 80, SquareType.Payment);
        Square Carlsberg = new Square("Carlsberg", 150, 10, SquareType.Payment);
        Square Østergade = new Square("Østergade", 280, 85, SquareType.Payment);
        Square GaaIFaengsel = new Square("Gå i Fængsel", 0, 0, SquareType.GotoJail);
        Square Amagertorv = new Square("Amagertorv", 300, 95, SquareType.Payment);
        Square Vimmelskaftet = new Square("Vimmel-\nskaftet", 300, 95, SquareType.Payment);
        Square Nygade = new Square("Nygade", 320, 100, SquareType.Payment);
        Square Bornholm = new Square("Bornholm", 200, 75, SquareType.Payment);
        Square Frederiksberggade = new Square("Frederiks-\nberggade", 350, 120, SquareType.Payment);
        Square Skat = new Square("Ekstra-\nordinær\nstatsskat", 100, 0, SquareType.Payment);
        Square Raadhuspladsen = new Square("Rådhuspladsen", 400, 150, SquareType.Payment);


        Square[] boardSquares = new Square[squareCount];
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

        this.boardSquares = boardSquares;
    }
}