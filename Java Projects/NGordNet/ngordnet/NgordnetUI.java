package ngordnet;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;
import java.util.Set;

public class NgordnetUI {
    private static final int START = 1505;
    private static final int END = 2008;
    public static void main(String[] args) {
        int startDate = START; // Default start and end years (spans all data).
        int endDate = END;
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");
        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();
        System.out.println("\nBased on ngordnetui.config, using the following: "
                           + wordFile + ", " + countFile + ", " + synsetFile
                           + ", and " + hyponymFile + ".");
        System.out.println("\nFor tips on implementing NgordnetUI, see ExampleUI.java.");
        NGramMap ngm = new NGramMap(wordFile, countFile);
        WordNet wn = new WordNet(synsetFile, hyponymFile);
        WordLengthProcessor wlp = new WordLengthProcessor();
        while (true) {
            System.out.print("> ");
            String line = StdIn.readLine();
            String[] rawTokens = line.split(" ");
            String command = rawTokens[0];
            String[] tokens = new String[rawTokens.length - 1];
            System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
            switch (command) {
                case "quit":
                    return;
                case "help":
                    In inHelp = new In("./ngordnet/help.txt");
                    String helpStr = inHelp.readAll();
                    System.out.println(helpStr);
                    break;
                case "dates":
                    System.out.println("Start date: " + startDate);
                    System.out.println("End date: " + endDate);
                    break;
                case "range":
                    startDate = Integer.parseInt(tokens[0]); 
                    endDate = Integer.parseInt(tokens[1]);
                    break;
                case "count":
                    String wordToCount = tokens[0];
                    Integer year = Integer.parseInt(tokens[1]);
                    int count = ngm.countInYear(wordToCount, year);
                    System.out.println(count);
                    break;
                case "hyponyms":
                    String wordToHypo = tokens[0];
                    Set<String> hypoSet = wn.hyponyms(wordToHypo);
                    Object[] hypoArray = hypoSet.toArray();
                    String hypoString = "[";
                    for (int i = 0; i < hypoArray.length; i += 1) {
                        String currHypo = (String) hypoArray[i];
                        hypoString = hypoString + currHypo;
                        if (i < hypoArray.length - 1) {
                            hypoString = hypoString + ", ";
                        }
                    }
                    hypoString = hypoString + "]";
                    System.out.println(hypoString);
                    break;
                case "history":
                    Plotter.plotAllWords(ngm, tokens, startDate, endDate);
                    break;
                case "hypohist":
                    Plotter.plotCategoryWeights(ngm, wn, tokens, startDate, endDate);
                    break;
                case "wordlength":
                    Plotter.plotProcessedHistory(ngm, startDate, endDate, wlp);
                    break;
                case "zipf":
                    Integer zipfYear = Integer.parseInt(tokens[0]);
                    Plotter.plotZipfsLaw(ngm, zipfYear);
                    break;
                default:
                    System.out.println("Invalid command. Type help for a list of commands.");  
                    break;
            }
        }
    }
} 
