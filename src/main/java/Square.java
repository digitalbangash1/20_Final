public class Square {

    private final String title;
    private final int price;
    private final int rent;
    private final int[] houseRents;
    private final SquareType squareType;
    private Player soldToPlayer;
    private HouseColor houseColor = HouseColor.none;
    private int housePrice;
    private int builtHousesCount;
    private final int maxHousesCanBeBuiltPerSquare = 4;

    /**
     * Square constructor is created,
     * @param title
     * @param price
     * @param rent
     * @param houseRents
     * @param squareType
     * @param houseColor
     * @param housePrice
     */
    public Square(String title, int price, int rent, int[] houseRents, SquareType squareType, HouseColor houseColor, int housePrice) {
        this.title = title;
        this.price = price;
        this.rent = rent;
        this.houseRents = houseRents;
        this.squareType = squareType;
        this.houseColor = houseColor;
        this.housePrice = housePrice;
    }

    public Square(String title, int price, int rent, SquareType squareType) {
        this(title, price, rent, new int[]{0, 0, 0, 0}, squareType, HouseColor.none, 0);
    }

    public int getHousePrice() {
        return housePrice;
    }

    public int[] getHouseRents() {
        return houseRents;
    }

    public int getBuiltHousesCount() {
        return builtHousesCount;
    }

    public boolean canBuildAnotherHouse() {
        return this.builtHousesCount < maxHousesCanBeBuiltPerSquare;
    }

    public void increaseBuildHousesCountBy(int number) {
        this.builtHousesCount += number;
    }

    public HouseColor getColor() {
        return houseColor;
    }

    public boolean isOwnable() {
        return houseColor != HouseColor.none;
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

    public int getRent() {
        if (!isOwnable() || builtHousesCount == 0) {
            return rent;
        }
        int rentBecauseOfBuiltHousesIndex = builtHousesCount - 1;
        return houseRents[rentBecauseOfBuiltHousesIndex];
    }
}
