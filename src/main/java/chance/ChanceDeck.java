package chance;

import java.util.Random;

public class ChanceDeck {

    private Random random = new Random();
    private ChanceCard[] chanceCards;

    public ChanceDeck(){
        initializeDeck();
    }

    private void initializeDeck() {
        chanceCards = new ChanceCard[20];
        chanceCards[0] = new ChanceCard("Ryk frem til START.", ChanceCardActionType.Start, 0, 0);
        chanceCards[1] = new ChanceCard("Ryk 3 felter frem", ChanceCardActionType.Move, 0, 3);
        chanceCards[2] = new ChanceCard("Ryk 3 felter tilbage", ChanceCardActionType.Move, 0, -3);
        chanceCards[3] = new ChanceCard("Tag til Rådhuspladsen", ChanceCardActionType.Raadhuspladsen, 0, 39);
        chanceCards[4] = new ChanceCard("Efter auktionen på Assistenshuset, hvor de havde pantsat deres tøj,modtager de ekstra 108 kroner", ChanceCardActionType.GetPaid, 108, 0);
        chanceCards[5] = new ChanceCard("Deres præmieobligation er udtrykket. De modtager 100 kr af banken.", ChanceCardActionType.GetPaid, 100, 0);
        chanceCards[6] = new ChanceCard("De har solgt deres gamle klude.Modtag 20 kr.", ChanceCardActionType.GetPaid, 20, 0);
        chanceCards[7] = new ChanceCard("Ryk 3 felter tilbage", ChanceCardActionType.Move, 0, -3);
        chanceCards[8] = new ChanceCard("Værdien af egen avl fra nyttehaven udgør 200 kr som de modtager af banken", ChanceCardActionType.GetPaid, 200, 0);
        chanceCards[9] = new ChanceCard("Ryk frem til Grønningen, hvis De passerer start indkasser da kr 200 kr", ChanceCardActionType.CrossingStart, 200, 24);
        chanceCards[10] = new ChanceCard("Betal for vognvask og smøring", ChanceCardActionType.Pay, 10, 0);
        chanceCards[11] = new ChanceCard("Tag til Rådhuspladsen", ChanceCardActionType.Raadhuspladsen, 0, 39);
        chanceCards[12] = new ChanceCard("Grundet dyrtiden har De fået gageforhøjelse, modtag kr 25.", ChanceCardActionType.GetPaid, 25, 0);
        chanceCards[13] = new ChanceCard("Gå i fængsel.Du modtager ikke 200 kr for at passer start", ChanceCardActionType.GotoPrison, 0, 10);
        chanceCards[14] = new ChanceCard("Ryk frem til Øresundsbåden", ChanceCardActionType.CrossingStart, 200, 25);
        chanceCards[15] = new ChanceCard("Gå i fængsel.Du modtager ikke 200 kr for at passer start", ChanceCardActionType.GotoPrison, 0, 10);
        chanceCards[16] = new ChanceCard("Gå i fængsel.Du modtager ikke 200 kr for at passer start", ChanceCardActionType.GotoPrison, 0, 10);
        chanceCards[17] = new ChanceCard("Du løslades uden omkostninger. Behold dette kort indtil du får brugt det", ChanceCardActionType.Prison, 0, 0);
        chanceCards[18] = new ChanceCard("Du løslades uden omkostninger. Behold dette kort indtil du får brugt det", ChanceCardActionType.Prison, 0, 0);
        chanceCards[19] = new ChanceCard("Modtager 25 kr af hver medspiller", ChanceCardActionType.PaidbyOthers, 25, 0);
    }

    public ChanceCard getRandomChanceCard() {
        int index = random.nextInt(chanceCards.length);
        return chanceCards[index];
    }
}
