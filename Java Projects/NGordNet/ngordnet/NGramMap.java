package ngordnet;

import java.util.TreeMap;
import edu.princeton.cs.introcs.In;
import java.util.Collection;

public class NGramMap {
    private TreeMap<String, TimeSeries<Integer>> words;
    private TimeSeries<Long> counts;

    /** Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME. */
    public NGramMap(String wordsFilename, String countsFilename) {
        In wordStream = new In(wordsFilename);
        words = new TreeMap<String, TimeSeries<Integer>>();

        while (wordStream.hasNextLine()) {
            String line = wordStream.readLine();
            String[] splitLine = line.split("\\s+"); // Splits line by all whitespaces.
            String currWord = splitLine[0];
            Integer currYear = Integer.parseInt(splitLine[1]);
            Integer currCount = Integer.parseInt(splitLine[2]);

            if (!words.containsKey(currWord)) {
                TimeSeries<Integer> yearCount = new TimeSeries<Integer>();
                yearCount.put(currYear, currCount);
                words.put(currWord, yearCount);
            } else {
                TimeSeries<Integer> yearCount = words.get(currWord);
                yearCount.put(currYear, currCount);
                words.put(currWord, yearCount);
            }
        }

        In countStream = new In(countsFilename);
        counts = new TimeSeries<Long>();

        while (countStream.hasNextLine()) {
            String line = countStream.readLine();
            String[] splitLine = line.split(",");
            Integer currYear = Integer.parseInt(splitLine[0]);
            Long currCount = Long.parseLong(splitLine[1]);
            counts.put(currYear, currCount);
        }
    }
    
    /** Returns the absolute count of WORD in the given YEAR. If the word
      * did not appear in the given year, return 0. */
    public int countInYear(String word, int year) {
        int n = 0;
        if (words.containsKey(word)) {
            TimeSeries<Integer> ts = words.get(word);
            if (ts.containsKey(year)) {
                Integer count = ts.get(year);
                n = count.intValue();
            }
        }
        return n;
    }

    /** Returns a defensive copy of the YearlyRecord of WORD. */
    public YearlyRecord getRecord(int year) {
        YearlyRecord yrCopy = new YearlyRecord();
        for (String currWord : words.keySet()) {
            TimeSeries<Integer> currTS = words.get(currWord);
            if (currTS.containsKey(year)) {
                Integer currCount = currTS.get(year);
                yrCopy.put(currWord, currCount);
            }
        }
        return yrCopy;
    }

    /** Returns the total number of words recorded in all volumes. */
    public TimeSeries<Long> totalCountHistory() {
        return counts;
    }

    // /** Provides the history of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Integer> countHistory(String word, int startYear, int endYear) {
        TimeSeries<Integer> ts = new TimeSeries<Integer>();
        TimeSeries<Integer> wordTS = words.get(word);
        for (Integer currYear : wordTS.keySet()) {
            if ((currYear >= startYear) && (currYear <= endYear)) {
                ts.put(currYear, wordTS.get(currYear));
            }
        }
        return ts;
    }

    // /** Provides a DEFENSIVE copy of the history of WORD. */
    public TimeSeries<Integer> countHistory(String word) {
        TimeSeries<Integer> ts = words.get(word);
        return ts;
    }

    // /** Provides the relative frequency of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> weightHistory(String word, int startYear, int endYear) {
        TimeSeries<Double> wh = weightHistory(word);
        TimeSeries<Double> ts = new TimeSeries<Double>();
        for (Integer currYear : wh.keySet()) {
            if ((currYear >= startYear) && (currYear <= endYear)) {
                ts.put(currYear, wh.get(currYear));
            }
        }
        return ts;
    }

    // /** Provides the relative frequency of WORD. */
    public TimeSeries<Double> weightHistory(String word) {
        TimeSeries<Long> matchedTCH = new TimeSeries<Long>();
        TimeSeries<Integer> wordCH = this.countHistory(word);
        for (Integer currYear: counts.keySet()) {
            if (wordCH.containsKey(currYear)) {
                matchedTCH.put(currYear, counts.get(currYear));
            }
        }
        return wordCH.dividedBy(matchedTCH);
    }

    /** Provides the summed relative frequency of all WORDS between
      * STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> wordsSWH,
                                                  int startYear, int endYear) {
        TimeSeries<Double> sumTS = summedWeightHistory(wordsSWH);
        TimeSeries<Double> targetSeries = new TimeSeries<Double>();
        for (Integer currYear: sumTS.keySet()) {
            if ((currYear >= startYear) && (currYear <= endYear)) {
                targetSeries.put(currYear, sumTS.get(currYear));
            }
        }
        return targetSeries;
    }

    /** Returns the summed relative frequency of all WORDS. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> wordsSWH) {
        TimeSeries<Double> sumTS = new TimeSeries<Double>();
        for (String word : wordsSWH) {
            sumTS = sumTS.plus(weightHistory(word));
        }
        return sumTS;
    }

    /** Provides processed history of all words between STARTYEAR and ENDYEAR as processed
      * by YRP. */
    public TimeSeries<Double> processedHistory(int startYear, int endYear,
                                               YearlyRecordProcessor yrp) {
        TimeSeries<Double> procHist = new TimeSeries<Double>();
        for (Integer currYear : counts.keySet()) {
            if ((currYear >= startYear) && (currYear <= endYear)) {
                procHist.put(currYear, yrp.process(getRecord(currYear)));
            }
        }
        return procHist;
    }

    // /** Provides processed history of all words ever as processed by YRP. */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        TimeSeries<Double> procHist = new TimeSeries<Double>();
        for (Integer currYear : counts.keySet()) {
            procHist.put(currYear, yrp.process(getRecord(currYear)));
        }
        return procHist;
    }
}
