import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class DiceTest {
Dice dice = new Dice();
    @Test
  public   void roll() {
        int val = dice.roll();
        assertTrue(0 < val && val <= 6);
    }
}