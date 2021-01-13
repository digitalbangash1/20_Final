import gui_fields.GUI_Player;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
class PlayerTest {


    GUI_Player gui_player = new GUI_Player("Asaad",5000);
    Player player = new Player( gui_player,1);

    @Test
    void TestNamePositive() {
        assertEquals("Asaad", player.getName());
    }
    @Test
    void TestNameNegative() {
        assertNotEquals("Ali", player.getName());
    }

    @Test
    void TestBalancePositive() {
        assertEquals(5000, player.getBalance());
    }
    @Test
    void TestBalanceNegative() {
        assertNotEquals(4000, player.getBalance());
    }



}