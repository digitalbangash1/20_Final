import java.awt.*;

public class Square {

    private final String title;
    private final String description;
    private final String subText;
    private final int price;
    private final Color BG_color;
    private final Color FG_color;
    private final SquareType squareType;
    private Player soldToPlayer;

    public Square(String title, String subText, String description, int price, Color BG_color, Color FG_color, SquareType squareType) {
        this.title = title;
        this.description= description;
        this.subText = subText;
        this.price = price;
        this.BG_color = BG_color;
        this.FG_color = FG_color;
        this.squareType = squareType;
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

    public String getDescription() {
        return description;
    }

    public String getSubText() {
        return subText;
    }

    public Color getBGColor() {
        return BG_color;
    }

    public Color getFGColor() {
        return FG_color;
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
}
