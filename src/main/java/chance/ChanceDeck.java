package chance;

public class ChanceDeck {
    private ChanceCard[] chanceCards;

    public ChanceCard[] initializeCards() {
        chanceCards = new ChanceCard[16];
        chanceCards[0]  = new ChanceCard("Ryk frem til START.","Start",0,0);
        chanceCards[1]  = new ChanceCard("Ryk 3 felter frem","Move",0,3);
        chanceCards[2]  = new ChanceCard("Ryk 3 felter tilbage","Move",0,-3);
        chanceCards[3]  = new ChanceCard("Tag til Rådhuspladsen","Rådhuspladsen",0,39);
        chanceCards[4]  = new ChanceCard("Efter auktionen på Assistenshuset, hvor de havde pantsat deres tøj,modtager de ekstra 108 kroner","GetPaid",108,0);
        chanceCards[5]  = new ChanceCard("Deres præmieobligation er udtrykket. De modtager 100 kr af banken.","GetPaid",100,0);
        chanceCards[6]  = new ChanceCard("De har solgt deres gamle klude.Modtag 20 kr.","GetPaid",20,0);
        chanceCards[7]  = new ChanceCard("Ryk 3 felter tilbage","Move",0,-3);
        chanceCards[8]  = new ChanceCard("Værdien af egen avl fra nyttehaven udgør 200 som de modtager af banken","GetPaid",200,0);
        chanceCards[9]  = new ChanceCard("Ryk frem til Grønningen, hvis De passerer start indkasser da kr 4000","CrossingStart",200,24);
        chanceCards[10] = new ChanceCard("Ryk frem til Grønningen. Hvis de passerer Start modtager du 200 kroner","Grønningen",200,0);
        chanceCards[11] = new ChanceCard("Betal for vognvask og smøring","Pay",10,0);
        chanceCards[12] = new ChanceCard("Tag til Rådhuspladsen","Rådhuspladsen",0,39);
        chanceCards[13] = new ChanceCard("Grundet dyrtiden har De fået gageforhøjelse, modtag kr 25.","GetPaid",25,0);
        chanceCards[14] = new ChanceCard("Gå i fængsel.Du modtager ikke 200 kr for at passer start","Gå i Fængsel",0,10);
        chanceCards[15] = new ChanceCard("Ryk frem til Øresundsbåden","CrossingStart",200,25);







    }
}
