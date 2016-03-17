package ngordnet;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;

public class YearlyRecord {
    /** Creates a new empty YearlyRecord. */
    private int N = 0;
    private static boolean beenSorted; // Will stay true if no more puts.
    private HashMap<String, Integer> recordMap;
    private TreeMap<String, Integer> sortedRecordMap;
    private TreeMap<String, Integer> rankMap = new TreeMap<String, Integer>();

    public YearlyRecord() {
        recordMap = new HashMap<String, Integer>();
        sortedRecordMap = new TreeMap<String, Integer>();
        beenSorted = true;
    }

    /** Creates a YearlyRecord using the given data. */
    public YearlyRecord(HashMap<String, Integer> otherCountMap) {
        recordMap = otherCountMap;
        sortedRecordMap = sortByValue(recordMap);
        N = recordMap.size();
        beenSorted = true;
    }

    /** Returns the number of times WORD appeared in this year. */
    public int count(String word) {
        if (recordMap.containsKey(word)) {
            return recordMap.get(word);
        }
        return 0;
    }

    /** Records that WORD occurred COUNT times in this year. */
    public void put(String word, int count) {
        if (!recordMap.containsKey(word)) {
            N += 1;
        }
        recordMap.put(word, count);
        beenSorted = false;
    }

    /** Returns the number of words recorded this year. */
    public int size() {
        return N;
    }

    /** Returns all words in ascending order of count. */
    public Collection<String> words() {
        if (!beenSorted) {
            sortedRecordMap = sortByValue(recordMap);
        }
        Collection<String> sortedWordList = new ArrayList(sortedRecordMap.keySet());
        return sortedWordList;
    }

    /** Returns all counts in ascending order of count. */
    public Collection<Number> counts() {
        if (!beenSorted) {
            sortedRecordMap = sortByValue(recordMap);
        }
        Collection<Number> sortedWordList = new ArrayList(sortedRecordMap.values());
        return sortedWordList;
    }

    /** Returns rank of WORD. Most common word is rank 1. 
      * If two words have the same rank, break ties arbitrarily. 
      * No two words should have the same rank.
      */
    public int rank(String word) {
        int rankTracker = N;
        if (!beenSorted) {
            sortedRecordMap = sortByValue(recordMap);
            rankMap.clear();
        }
        if (rankMap.containsKey(word)) {
            rankTracker = rankMap.get(word);
        } else {
            for (String currKey : this.words()) {
                if (currKey == word) {
                    rankMap.put(currKey, rankTracker);
                    return rankTracker;
                }
                rankTracker -= 1;
            }
        }
        return rankTracker;
    }

    private static TreeMap<String, Integer> sortByValue(HashMap<String, Integer> map) {
        ValueComparator vc =  new ValueComparator(map);
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
        sortedMap.putAll(map);
        beenSorted = true;
        return sortedMap;
    }
}
