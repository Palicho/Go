package tp.server;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class GameLogicTest {
    GameLogic game = new GameLogic();

    @Test
    public void testMoveRejection() {
        game.board.reset();

        assertEquals("B 2 2", game.move("B 2 2"));
        assertEquals("MOVE", game.move("B 2 2"));
    }

    @Test
    public void testStonePlacement() {
        game.board.reset();

        game.move("B 0 0");
        game.move("W 18 18");

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if (i == 0 && j == 0) assertSame(game.board.getColorAt(i, j), Color.BLACK);
                else if (i == 18 && j == 18) assertSame(game.board.getColorAt(i, j), Color.WHITE);
                else assertSame(game.board.getColorAt(i, j), null);
            }
        }
    }

    @Test
    public void testGroupCreation() {
        game.board.reset();
        game.move("W 0 0");

        LinkedList<StoneGroup> groups = game.board.getGroups();
        assertEquals(1, groups.size());

        StoneGroup group = groups.element();
        assertSame(group.getColor(), Color.WHITE);
        assertEquals(1, group.getStones().size());
    }

    @Test
    public void testComplexStoneStrangulation() {
        game.board.reset();

        game.move("B 1 0");
        game.move("W 1 1");
        game.move("B 2 1");
        game.move("W 3 1");
        game.move("B 3 0");
        game.move("W 2 2");
        game.move("B 2 0");
        game.move("W 0 0");
        game.move("B 2 3");
        game.move("W 4 0");

        assertEquals(6, game.board.getGroups().size());
        assertEquals(4, game.getRemoved().size());
    }

    @Test
    public void testTerritoryEvaluation() {
        game.board.reset();

        game.move("W 0 2");
        game.move("W 0 3");
        game.move("W 1 0");
        game.move("W 1 1");
        game.move("W 1 2");
        game.move("W 2 0");
        game.move("W 2 1");

        game.move("B 16 0");
        game.move("B 17 0");
        game.move("B 17 1");
        game.move("B 18 1");
        game.move("B 18 2");

        assertEquals("END B 1 W 2", game.endGame());
    }

    @Test
    public void testRegularKo() {
        game.board.reset();

        game.move("W 0 1");
        game.move("W 1 0");
        game.move("W 2 1");
        game.move("W 1 2");

        game.move("B 0 2");
        game.move("B 1 3");
        game.move("B 2 2");
        game.move("B 1 1");

        assertEquals("MOVE", game.move("W 1 2"));
    }

    @Test
    public void testBasicBotMove() {
        game.board.reset();
        game.move("B 5 5");
        assertEquals("W 4 5", game.getBotMove());
        assertEquals("W 5 4", game.getBotMove());
        assertEquals("W 6 5", game.getBotMove());
        assertEquals("W 5 6", game.getBotMove());
    }

    @Test
    public void testBotChoice() {
        game.board.reset();
        game.move("B 1 0");
        game.move("B 2 1");
        game.move("B 3 0");
        game.move("B 2 0");
        game.move("B 5 5");

        assertEquals("W 4 5", game.getBotMove());
        assertEquals("W 5 4", game.getBotMove());
        assertEquals("W 6 5", game.getBotMove());
        assertEquals("W 5 6", game.getBotMove());
    }

    /**
     * . x o . . .
     * x x o . . .
     * . x o . . .
     * x x o . . .
     * o o . . . .
     * . . . . . .
     */
    @Test
    public void testBotPassing() {
        game.board.reset();
        game.move("B 1 0");
        game.move("B 0 1");
        game.move("B 1 1");
        game.move("B 2 1");
        game.move("B 3 1");
        game.move("B 3 0");
        game.move("W 0 2");
        game.move("W 1 2");
        game.move("W 2 2");
        game.move("W 3 2");
        game.move("W 4 0");
        game.move("W 4 1");

        assertEquals("PAUSE W", game.getBotMove());
    }
}
