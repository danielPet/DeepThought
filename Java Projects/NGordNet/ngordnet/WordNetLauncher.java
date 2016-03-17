package ngordnet;

public class WordNetLauncher {
    public static void main(String[] args) {
        WordNet wn = new WordNet("./wordnet/synsets.txt", "./wordnet/hyponyms.txt");
        System.out.println(wn.isNoun("jump"));
        System.out.println(wn.isNoun("leap"));
        System.out.println(wn.isNoun("nasal_decongestant"));

        System.out.println("Hyponyms of increase:");
        for (String noun : wn.hyponyms("increase")) {
            System.out.println(noun);
        }

        System.out.println("Hyponyms of jump:");
        for (String noun : wn.hyponyms("jump")) {
            System.out.println(noun);
        }

        System.out.println("Hyponyms of leap:");
        for (String noun : wn.hyponyms("leap")) {
            System.out.println(noun);
        }
    }
}