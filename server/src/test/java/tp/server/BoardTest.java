package tp.server;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void testMoveRejection() {
        //todo: make this for real too
        Board board = Board.getInstance();

        assertTrue(board.move(Color.BLACK,2,2));
        assertFalse(board.move(Color.BLACK,2,2));

        board.reset();
    }

    @Test
    public void testStonePlacement() {
        Board board = Board.getInstance();

        board.move(Color.BLACK,0,0);
        board.move(Color.WHITE,18,18);

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
        board.move(Color.WHITE,0,0);

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
        board.move(Color.WHITE,0,0);
        board.move(Color.WHITE,0,2);
        LinkedList<StoneGroup> groups = board.getGroups();
        assertEquals(2, groups.size());
        assertEquals(0, board.getGroupIdAt(0, 0));
        assertEquals(-1, board.getGroupIdAt(0, 1));
        assertEquals(1, board.getGroupIdAt(0, 2));

        board.move(Color.WHITE,0,1);
        groups = board.getGroups();
        assertEquals(1, groups.size());
        assertEquals(0, board.getGroupIdAt(0, 0));
        assertEquals(0, board.getGroupIdAt(0, 1));
        assertEquals(0, board.getGroupIdAt(0, 2));

        board.reset();
    }

}
