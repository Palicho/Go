package tp.server;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void testMoveRejection() {
        //todo: make this for real too
        Board board = Board.getInstance();

        assertTrue(board.move(Color.BLACK, 2, 2));
        assertFalse(board.move(Color.BLACK, 2, 2));

        board.reset();
    }

    @Test
    public void testStonePlacement() {
        Board board = Board.getInstance();

        board.move(Color.BLACK, 0, 0);
        board.move(Color.WHITE, 18, 18);

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if (i == 0 && j == 0) assertSame(board.getColorAt(i, j), Color.BLACK);
                else if (i == 18 && j == 18) assertSame(board.getColorAt(i, j), Color.WHITE);
                else assertSame(board.getColorAt(i, j), null);
            }
        }

        board.reset();
    }

    @Test
    public void testGroupCreation() {
        Board board = Board.getInstance();
        board.move(Color.WHITE, 0, 0);

        LinkedList<StoneGroup> groups = board.getGroups();
        assertEquals(1, groups.size());

        StoneGroup group = groups.element();
        assertSame(group.getColor(), Color.WHITE);
        assertEquals(1, group.getStones().size());

        board.reset();
    }

    @Test
    public void testGroupMerging() {
        Board board = Board.getInstance();
        board.move(Color.WHITE, 0, 0);
        board.move(Color.WHITE, 0, 2);
        LinkedList<StoneGroup> groups = board.getGroups();
        assertEquals(2, groups.size());
        assertEquals(0, board.getGroupIdAt(0, 0));
        assertEquals(-1, board.getGroupIdAt(0, 1));
        assertEquals(1, board.getGroupIdAt(0, 2));

        board.move(Color.WHITE, 0, 1);
        groups = board.getGroups();
        assertEquals(1, groups.size());
        assertEquals(0, board.getGroupIdAt(0, 0));
        assertEquals(0, board.getGroupIdAt(0, 1));
        assertEquals(0, board.getGroupIdAt(0, 2));

        board.reset();
    }

    @Test
    public void testBorderGeneration() {
        Board board = Board.getInstance();

        board.move(Color.BLACK, 13,13);

        board.move(Color.WHITE, 0, 2);
        board.move(Color.WHITE, 0, 3);
        board.move(Color.WHITE, 1, 0);
        board.move(Color.WHITE, 1, 1);
        board.move(Color.WHITE, 1, 2);
        board.move(Color.WHITE, 2, 0);
        board.move(Color.WHITE, 2, 1);

        board.move(Color.BLACK, 16, 0);
        board.move(Color.BLACK, 17, 0);
        board.move(Color.BLACK, 17, 1);
        board.move(Color.BLACK, 18, 1);
        board.move(Color.BLACK, 18, 2);

        assertEquals(4, board.getGroups().get(0).getBorder().size());
        assertEquals(7, board.getGroups().get(1).getBorder().size());
        assertEquals(5, board.getGroups().get(2).getBorder().size());

        board.reset();
    }

    @Test
    public void testClusterGeneration() {
        Board board = Board.getInstance();
        board.move(Color.WHITE, 1, 0);
        board.move(Color.WHITE, 1, 1);
        board.move(Color.WHITE, 0, 2);

        EmptyCluster cluster = board.getCluster(0, 0);
        assertEquals(2, cluster.size());

        cluster = board.getCluster(3, 3);
        assertEquals(356, cluster.size());

        board.reset();
    }

    @Test
    public void testSekiExecution() {
        Board board = Board.getInstance();

        board.move(Color.WHITE, 0, 2);
        board.move(Color.WHITE, 1, 0);
        board.move(Color.WHITE, 1, 1);

        board.move(Color.BLACK, 17, 0);
        board.move(Color.BLACK, 18, 1);

        board.evaluateTerritoryPoints();

        assertEquals(0, board.getScore(Color.BLACK));
        assertEquals(0, board.getScore(Color.WHITE));

        board.reset();
    }

    @Test
    public void testTerritoryEvaluation() {
        Board board = Board.getInstance();

        board.move(Color.WHITE, 0, 2);
        board.move(Color.WHITE, 0, 3);
        board.move(Color.WHITE, 1, 0);
        board.move(Color.WHITE, 1, 1);
        board.move(Color.WHITE, 1, 2);
        board.move(Color.WHITE, 2, 0);
        board.move(Color.WHITE, 2, 1);

        board.move(Color.BLACK, 16, 0);
        board.move(Color.BLACK, 17, 0);
        board.move(Color.BLACK, 17, 1);
        board.move(Color.BLACK, 18, 1);
        board.move(Color.BLACK, 18, 2);

        board.evaluateTerritoryPoints();

        assertEquals(1, board.getScore(Color.BLACK));
        assertEquals(2, board.getScore(Color.WHITE));

        board.reset();
    }

}
