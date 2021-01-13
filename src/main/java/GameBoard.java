import gui_fields.*;
import gui_main.GUI;

import java.awt.*;

public class GameBoard {

    private int squareCount = 40;
    private int chanceCount = 7;
    private GUI gui;
    private Square[] boardSquares;
    //private GUI_Field[] gui_fields;
    private ChanceCard[] chanceCards;
    private Player[] players;

    public GameBoard(GUI gui) {
        this.gui = gui;
        initializeBoard();
    }

    public void takePlayerTurn(Player currentPlayer, int PlayerNewPosition, int diceValuesSum, Dice dice) throws NotEnoughBalanceException {
        boolean prison = handleAnySquareBefore(currentPlayer, dice);
        if (!prison) {
            int nextIndex = movePlayer(currentPlayer, diceValuesSum);
            Square boardSquare;
            boardSquare = boardSquares[PlayerNewPosition];
            printBoardSquare(currentPlayer, boardSquare);
            evaluateSquare(boardSquare, currentPlayer);

            handleAnySquareAfter(currentPlayer, nextIndex);
        }
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
                handlePaymentSquare(currentPlayer);
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
        gui.showMessage("Du er landet på start! Modtag 2M");
        currentPlayer.increaseBalanceBy(2);
    }

    private int movePlayer(Player currentPlayer, int diceValue) {

        //Calculate next index
        int nextIndex = currentPlayer.getCurrentSquareIndex() + diceValue;
        //Set players current index to this
        int currentIndex = nextIndex % squareCount;
        //Check if the player has once again reached start
        boolean passedStart = checkIndex(nextIndex);

        if (passedStart) {
            currentIndex = nextIndex - squareCount;
            currentPlayer.setCurrentSquareIndex(this.gui, currentIndex);
        } else {
            currentPlayer.setCurrentSquareIndex(this.gui, currentIndex);
        }
        return currentIndex;
    }

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

    public void allSquaresToString() {
        for (int i = 0; i < squareCount; i++) {
            squareToString(boardSquares[i]);
        }
    }

    public void squareToString(Square square) {
        System.out.println("Title: " + square.getTitle());
        System.out.println("SubText: " + square.getTitle());
        System.out.println("Price (value): " + square.getStringPrice());
        System.out.println("Square Type: " + square.getSquareType());
        System.out.println();
    }

    private boolean handleAnySquareBefore(Player currentPlayer, Dice dice) throws NotEnoughBalanceException {
        if (currentPlayer.isInPrison()) {
            if (currentPlayer.hasJailFreeCard()) {
                boolean choice1 = gui.getUserLeftButtonPressed("Du har et kom ud af fængslet kort. Vil du bruge det?", "Ja", "Nej");
                if (choice1) {
                    currentPlayer.setInPrison(false);
                    return false;
                } else {
                    boolean choice = gui.getUserLeftButtonPressed(currentPlayer.getName() + " Er i fængsel. Slå en 6'er for at komme fri eller betal 2M", "Kast", "Betal 2M");
                    if (choice) {
                        int diceValue = dice.roll();
                        this.gui.setDie(diceValue);
                        if (diceValue == 6) {
                            gui.showMessage("Du slog en 6'er og undslap fængslet!");
                            movePlayer(currentPlayer, diceValue);
                            currentPlayer.setInPrison(false);
                            return false;
                        } else {
                            gui.showMessage("Du slog ikke en 6'er!");
                            return true;
                        }
                    } else {
                        currentPlayer.decreaseBalanceBy(2);
                        String button = this.gui.getUserButtonPressed("Du kom ud af fængslet. Kast terningen - tryk på Kast", "Kast");
                        if (button.equals("Kast")) {
                            int diceValue = dice.roll();
                            this.gui.setDie(diceValue);
                            currentPlayer.setInPrison(false);
                            movePlayer(currentPlayer, diceValue);
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
            currentPlayer.increaseBalanceBy(2);
            gui.showMessage("Du har passeret start og modtager 2M!");
        }
    }

    private void handleNothingSquare(Player currentPlayer) {

    }

    private void handlePaymentSquare(Player currentPlayer) throws NotEnoughBalanceException {

        Square boardSquare;
        boardSquare = boardSquares[currentPlayer.getCurrentSquareIndex()];
        int fieldPrice = boardSquare.getFieldPrice();
        Player soldToPlayer = boardSquare.getSoldToPlayer();

        // if the soldToPlayer is the same as the currentPlayer then do not do anything because
        // the currentPlayer already owns this square
        if (soldToPlayer == currentPlayer) {
            gui.showMessage("Du ejer det her felt. Der var du heldig!");
        } else if (soldToPlayer == null) {
            boolean choice = gui.getUserLeftButtonPressed(
                    "Vil du købe dette felt? " + "    " + "Felt: " + boardSquare.getTitle() + "    " + "Pris: " + fieldPrice, "Ja", "Nej");
            // the first player on this square becomes the owner and pays the price
            if (choice) {
                currentPlayer.decreaseBalanceBy(fieldPrice);
                boardSquare.setSoldToPlayer(currentPlayer);
            }

        } else {
            int rent = boardSquare.getRent();
            // other players coming to this square pays to the owner and become one of the renters
            gui.showMessage("Du er landet på et felt for en anden spiller og skal betale dem " + rent);
            soldToPlayer.increaseBalanceBy(rent);
            currentPlayer.decreaseBalanceBy(rent);
            // boardSquare.addRentedToPlayer(currentPlayer);
        }
    }


    private void handleGotoJailSquare(Player currentPlayer) {
        currentPlayer.setInPrison(true);
        gui.showMessage("Du skal i Fængsel!");
        int prisonIndex = getSquareIndexByType(SquareType.Prison);
        currentPlayer.setCurrentSquareIndex(gui, prisonIndex);
    }


    private void handleTakeChanceCardSquare(Player currentPlayer) throws NotEnoughBalanceException {
        gui.showMessage("Du er landet på prøv lykken! Tag et chance kort");
        ChanceCard chanceCard = chanceCards[1].getRandomChanceCard(chanceCards);
        String text = chanceCard.getText();
        gui.displayChanceCard(text);
        String action = chanceCard.getActionType();
        switch (action) {
            case "Start":
                currentPlayer.increaseBalanceBy(2);
                currentPlayer.setCurrentSquareIndex(gui, 0);
                break;
            case "Move":
                int currentIndex = currentPlayer.getCurrentSquareIndex();
                if (text.equals("Ryk 5 felter frem")) {
                    movePlayer(currentPlayer, 5);
                    Square boardSquare = boardSquares[currentPlayer.getCurrentSquareIndex()];
                    evaluateSquare(boardSquare, currentPlayer);
                } else {
                    boolean choice = gui.getUserLeftButtonPressed("Vil du rykke et felt frem eller tage et nyt chancekort?", "Ryk 1 Felt Frem", "Tag nyt chancekort");
                    if (choice) {
                        movePlayer(currentPlayer, 1);
                        Square boardSquare = boardSquares[currentPlayer.getCurrentSquareIndex()];
                        evaluateSquare(boardSquare, currentPlayer);
                    } else {
                        handleTakeChanceCardSquare(currentPlayer);
                    }
                }
                break;
            case "Pay":
                currentPlayer.decreaseBalanceBy(2);
                break;
            case "Prison":
                if (currentPlayer.hasJailFreeCard()) {
                    return;
                } else {
                    currentPlayer.setGetOutOfJailCard();
                }
                break;
            case "PayByOthers":
                for (int i = 0; i < players.length; i++) {
                    if (!players[i].getName().equals(currentPlayer.getName())) {
                        players[i].decreaseBalanceBy(1);
                    }
                    currentPlayer.increaseBalanceBy(1);
                }
                break;
            case "PayByBank":
                currentPlayer.increaseBalanceBy(2);
                break;
        }
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
        this.chanceCards = initializeCards();
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
        int index = 0;
        boardSquares[index++] = start;
        boardSquares[index++] = Rødovrevej;
        boardSquares[index++] = chance1;
        boardSquares[index++] = Hvidovrevej;
        boardSquares[index++] = BetalIndomstSkat;
        boardSquares[index++] = Øresund;
        boardSquares[index++] = Roskildevej;
        boardSquares[index++] = chance1;
        boardSquares[index++] = ValbyLanggade;
        boardSquares[index++] = Allégade;
        boardSquares[index++] = Gratisparkering;
        boardSquares[index++] = FrederiksbergAlle;
        boardSquares[index++] = Tuborg;
        boardSquares[index++] = Bülowsvej;
        boardSquares[index++] = GammelKongevej;
        boardSquares[index++] = DFDS;
        boardSquares[index++] = Bernstorffsvej;
        boardSquares[index++] = chance1;
        boardSquares[index++] = Hellerupvej;
        boardSquares[index++] = Strandvejen;
        boardSquares[index++] = Helle;
        boardSquares[index++] = Trianglen;
        boardSquares[index++] = chance1;
        boardSquares[index++] = Østerbrogade;
        boardSquares[index++] = Grønningen;
        boardSquares[index++] = ØS;
        boardSquares[index++] = Bredgade;
        boardSquares[index++] = KgsNytorv;
        boardSquares[index++] = Carlsberg;
        boardSquares[index++] = Østergade;
        boardSquares[index++] = GaaIFaengsel;
        boardSquares[index++] = Amagertorv;
        boardSquares[index++] = Vimmelskaftet;
        boardSquares[index++] = chance1;
        boardSquares[index++] = Nygade;
        boardSquares[index++] = Bornholm;
        boardSquares[index++] = chance1;
        boardSquares[index++] = Frederiksberggade;
        boardSquares[index++] = Skat;
        boardSquares[index++] = Raadhuspladsen;

        this.boardSquares = boardSquares;

//        GUI_Field[] gui_fields = {
//                MapToGui(start),
//        MapToGui(Burgerbaren),
//        MapToGui(Pizzariaet),
//        MapToGui(chance1),
//        MapToGui(Slikbutikken),
//        MapToGui(Iskiosken),
//        MapToGui(Faengsel),
//        MapToGui(Museet),
//        MapToGui(Biblioteket),
//        MapToGui(chance2),
//        MapToGui(Skateparken),
//        MapToGui(Svoemmingpolen),
//        MapToGui(Gratisparkering),
//        MapToGui(Spillehallen),
//        MapToGui(Biografen),
//        MapToGui(chance3),
//        MapToGui(Legetoejsbutikken),
//        MapToGui(Dyrehandlen),
//        MapToGui(GaaIFaengsel),
//        MapToGui(Bowlinghallen),
//        MapToGui(Zoo),
//        MapToGui(chance4),
//        MapToGui(Vandlandet),
//        MapToGui(Strandpromenaden)
//        };
//
//        this.gui_fields = gui_fields;
    }

//    private GUI_Field MapToGui(Square square) {
//        switch (square.getSquareType()) {
//
//            case Start:
//                return new GUI_Start(square.getTitle(), square.getSubText(), square.getDescription(),square.getBGColor(),square.getFGColor());
//            case DoNothing:
//                return new GUI_Refuge();
//            case Payment:
//                return new GUI_Street(square.getTitle(), square.getSubText(), square.getDescription(),square.getStringPrice(),square.getBGColor(),square.getFGColor());
//            case GotoJail:
//                return new GUI_Jail("default",square.getTitle(),square.getSubText(),square.getDescription(),square.getBGColor(),square.getFGColor());
//            case Prison:
//                return new GUI_Jail("default",square.getTitle(),square.getSubText(),square.getDescription(),square.getBGColor(),square.getFGColor());
//            case TakeChanceCard:
//                return new GUI_Chance();
//        }
//        return new GUI_Empty();
//    }

    private ChanceCard[] initializeCards() {
        ChanceCard[] chanceCards = new ChanceCard[chanceCount];
        ChanceCard chance1 = new ChanceCard("Ryk frem til START. Modtag 2M", "Start", 2, 0);
        ChanceCard chance2 = new ChanceCard("Ryk 5 felter frem", "Move", 0, 5);
        ChanceCard chance3 = new ChanceCard("Ryk 1 felt frem eller tag et chancekort mere", "Move", 0, 1);
        ChanceCard chance4 = new ChanceCard("Du har spist for meget slik. Betal 2M til banken", "Pay", 2, 0);
        ChanceCard chance5 = new ChanceCard("Du løslades uden omkostninger. Behold dette kort indtil du får brugt det", "Prison", 0, 0);
        ChanceCard chance6 = new ChanceCard("Det er din fødselsdag! Alle giver dig 1M. TILLYKKE MED FØDSELSDAGEN!", "PayByOthers", 1, 0);
        ChanceCard chance7 = new ChanceCard("Du har lavet alle dine lektier! Modtag 2M fra banken.", "PayByBank", 2, 0);

        int index = 0;
        chanceCards[index] = chance1;
        chanceCards[index++] = chance2;
        chanceCards[index++] = chance3;
        chanceCards[index++] = chance4;
        chanceCards[index++] = chance5;
        chanceCards[index++] = chance6;
        chanceCards[index++] = chance7;
        return chanceCards;
    }
}
