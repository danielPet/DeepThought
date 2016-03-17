package ngordnet;

import java.util.Collection;

public class WordLengthProcessor implements YearlyRecordProcessor {
    public double process(YearlyRecord yearlyRecord) {
        Collection<String> words = yearlyRecord.words();
        Long allLetters = 0L;
        Long totalCount = 0L;
        Double totalAvgWL = 0D;
        for (String word : words) {
            allLetters += word.length() * yearlyRecord.count(word);
            totalCount += yearlyRecord.count(word);
        }
        totalAvgWL = allLetters.doubleValue() / totalCount.doubleValue();
        return totalAvgWL;
    }
}
