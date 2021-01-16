import java.util.Random;

public class Dice {
    int MIN =1;
    int MAX = 6;

    /**
     * The method roll is used to generate the random values of the dice
     * @return
     */
    public int roll(){
        Random random = new Random();
        int roll = random.nextInt(MAX)+MIN;
        return roll;
    }
}
