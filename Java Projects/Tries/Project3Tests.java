import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * My project 3 test cases used (along with command-line troubleshooting).
 * My alphabetsort implementation used the file test.in for tests which I
 * ran at the commandline.
 * @author Daniel Peterson
 */
public class Project3Tests {
		public Trie t;
		public AlphabetSort aSort;
		public Autocomplete aComplete;

		@Before
		public void setUp() {
			In in = new In("wiktionary.txt");
	        int N = in.readInt();
	        String[] terms = new String[N];
	        double[] weights = new double[N];
	        for (int i = 0; i < N; i++) {
	            weights[i] = in.readDouble();   // read the next weight
	            in.readChar();                  // scan past the tab
	            terms[i] = in.readLine();       // read the next term
	        }

			aComplete = new Autocomplete(terms, weights);
		}

		@Test
		public void testAdd() {
			t = new Trie();
			t.insert("hello");
	        t.insert("hey");
	        t.insert("goodbye");
	        t.insert("heeeey");

	        assertEquals(true, t.find("hell", false));
			assertEquals(true, t.find("hello", true));
			assertEquals(true, t.find("good", false));
			assertEquals(false, t.find("bye", false));
			assertEquals(false, t.find("heyy", false));
			assertEquals(false, t.find("hell", true));

		}

		@Test
		public void testAutoccomplete() {
			String prefix = "auto";
            for (String term : aComplete.topMatches(prefix, 5)) {
                StdOut.printf("%14.1f  %s\n", aComplete.weightOf(term), term);
            }
		}


		/* Run the unit tests in this file. */
		public static void main(String... args) {
				jh61b.junit.textui.runClasses(Project3Tests.class);
		}       
}