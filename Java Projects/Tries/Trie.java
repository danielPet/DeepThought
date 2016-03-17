
/**
 * Trie which uses the external Node class to implicitly store characters
 * as keys in a HashMap.
 * The latter class was made external for use in Alphabetsort.
 * @author Daniel Peterson
 */
public class Trie {
    private Node root = new Node();

    /**
     * Empty constructor for the Trie class.
     */
    public Trie() {

    }

    /**
     * Finds and returns whether the given string is in this Trie.
     * @param s String to be searched for
     * @param isFullWord boolean indicating if s is supposed to be a full word in the Trie
     * @return boolean indicating if the word exists in this Trie.
     */
    public boolean find(String s, boolean isFullWord) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            Character character = s.charAt(i);
            if (node.links.containsKey(character)) {
                node = node.links.get(character);
            } else {
                return false;
            }
        }
        return (node.exists || !isFullWord);
    }

    /**
     * Inserts the given string into the Trie.
     * @param s String to be inserted
     */
    public void insert(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            Character character = s.charAt(i);
            if (!node.links.containsKey(character)) {
                node.links.put(character, new Node());
            }
            node = node.links.get(character);
        }
        node.exists = true;
    }

    /**
     * Returns the root node of the Trie. This is used in AlphabetSort.
     * @return Node root node of the Trie
     */
    public Node getRoot() {
        return root;
    }

}
