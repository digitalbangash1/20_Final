import java.awt.*;

public class Square {

    private final String title;
    private final int price;
    private final int rent;
    private final SquareType squareType;
    private Player soldToPlayer;
    private int[] housearray;

    public Square(String title,  int price, int rent, SquareType squareType,int[] housearray) {
        this.title = title;
        this.price = price;
        this.rent = rent;
        this.squareType = squareType;
        this.housearray = housearray;
    }



    public String getTitle() {
        return title;
    }

    public Player getSoldToPlayer() {
        return soldToPlayer;
    }

    public void setSoldToPlayer(Player soldToPlayer) {
        this.soldToPlayer = soldToPlayer;
    }



    public String getStringPrice() {
        return String.valueOf(price);
    }

    public int getFieldPrice() {
        return price;
    }

    public SquareType getSquareType() {
        return squareType;
    }

    public int getRent(){return rent;}
}
