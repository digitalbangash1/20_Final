public class HouseGroundBlock {

    private final HouseColor color;
    private final int count;

    public HouseGroundBlock(HouseColor color, int count){
        this.color = color;
        this.count = count;
    }

    public HouseColor getColor() {
        return color;
    }

    public int getCount() {
        return count;
    }
}
