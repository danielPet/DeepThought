import org.junit.Test;
import static org.junit.Assert.*;

public class PieceTest {
	Board b = new Board(true);

	@Test
    public void testPiece() {
      Piece p = new Piece(true, b, 1, 2, "fire");
      boolean result = p.isFire();
      assertEquals(true, result);
      int res = p.side();
      assertEquals(0, res);
    }

    public static void main(String[] args) {
      jh61b.junit.textui.runClasses(PieceTest.class);
    }
}