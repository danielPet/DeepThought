import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * AlphabetSort class which sorts the words in a file by the order specified
 * by a given alphabet.
 * Uses my Trie class and the Node class for storing and printing the given words.
 * @author Daniel Peterson
 */
public class AlphabetSort {

    /**
     * Main method for sorting which takes in the given list.
     * @param args The arguments given on the command-line
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Trie sortingTrie = new Trie();
        String alphabet;
        String word;
        if (!s.hasNextLine()) {
            throw new IllegalArgumentException();
        }
        alphabet = s.nextLine();
        HashSet<Character> hs = new HashSet<Character>();
        for (int i = 0; i < alphabet.length(); i++) {
            hs.add(alphabet.charAt(i));
        }
        if (hs.size() != alphabet.length()) {
            throw new IllegalArgumentException();
        }
        if (!s.hasNextLine()) {
            throw new IllegalArgumentException();
        }
        while (s.hasNextLine()) {
            word = s.nextLine();
            sortingTrie.insert(word);
        }
        printWordsInOrder(sortingTrie, alphabet);
    }
 

    /**
     * Method for printing the words in the given Trie in order of the given alphabet.
     * This method differs from that below, as this one deals with the root node.
     * @param t Trie from which the words are to be printed
     * @param alphabet The alphabet with which the word is to be retrieved
     */
    public static void printWordsInOrder(Trie t, String alphabet) {
        String currentWord;
        for (int i = 0; i < alphabet.length(); i++) {
            Character character = alphabet.charAt(i);
            if (t.getRoot().links.containsKey(character)) {
                currentWord = character.toString();
                Node letterNode = t.getRoot().links.get(character);
                recursivePrinter(character.toString(), letterNode, alphabet);
            }
        }
    }

    /**
     * Recursively prints strings while iterating through each letter of the alphabet.
     * @param s Current string being added to for printing
     * @param n Node from which the next characters will be chosen
     * @param alphabet The alphabet which indicates the desired order
     */
    public static void recursivePrinter(String s, Node n, String alphabet) {
        String currentWord;
        if (n.exists) {
            System.out.println(s);
        }
        if (n.links.size() > 0) {
            for (int i = 0; i < alphabet.length(); i++) {
                Character a = alphabet.charAt(i);
                if (n.links.containsKey(a)) {
                    currentWord = s + a.toString();
                    Node next = n.links.get(a);
                    recursivePrinter(currentWord, next, alphabet);
                }
            }
        }
    }
}

