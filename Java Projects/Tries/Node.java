import java.util.HashMap;

/**
 * Returns the value associated with the given key.
 * Made public so that the data may more easily be used by bothe the
 * Trie class and AlphabetSort.
 * @author Daniel Peterson
 */
public class Node {
    boolean exists;
    HashMap<Character, Node> links;

    /**
     * Returns the value associated with the given key.
     */
    public Node() {
        links = new HashMap<Character, Node>();
        exists = false;
    }
}
