package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {    
    /** Constructs a new empty TimeSeries. */
    public TimeSeries() { }

    /** Creates a copy of TS, but only between STARTYEAR and ENDYEAR. 
     * inclusive of both end points. */
    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        Collection<Number> yearSet = ts.years();
        for (Number yearNumber : yearSet) {
            Integer year = yearNumber.intValue();
            if ((year >= startYear) && (year <= endYear)) {
                T datum = ts.get(yearNumber);
                this.put(year, datum);
            }
        }
    }

    /** Creates a copy of TS. */
    public TimeSeries(TimeSeries<T> ts) {
        Collection<Number> yearSet = ts.years();
        for (Number yearNumber : yearSet) {
            Integer year = yearNumber.intValue();
            T datum = ts.get(yearNumber);
            this.put(year, datum);
        }
    }

    /** Returns the quotient of this time series divided by the relevant value in ts.
      * If ts is missing a key in this time series, return an IllegalArgumentException. */
    public TimeSeries<Double> dividedBy(TimeSeries<? extends Number> ts) {
        TimeSeries<Double> targetSeries = new TimeSeries<Double>();
        Collection<Number> yearSet = this.years();
        Collection<Number> tsYearSet = ts.years();
        if (yearSet.containsAll(tsYearSet)) {
            for (Number yearNumber : yearSet) {
                int year = yearNumber.intValue();
                Double datum = this.get(yearNumber).doubleValue();
                Double tsDatum = ts.get(yearNumber).doubleValue();
                targetSeries.put(year, datum / tsDatum);
            }
        } else {
            throw new IllegalArgumentException();
        }
        return targetSeries;
    }

    /** Returns the sum of this time series with the given ts. The result is a 
      * a Double time series (for simplicity). */
    public TimeSeries<Double> plus(TimeSeries<? extends Number> ts) {
        TimeSeries<Double> targetSeries = new TimeSeries<Double>();
        Collection<Number> yearSet = this.years();
        for (Number yearNumber : yearSet) {
            int year = yearNumber.intValue();
            Double datum = this.get(yearNumber).doubleValue();
            targetSeries.put(year, datum);
        }

        Collection<Number> tsYearSet = ts.years();
        for (Number tsYearNumber : tsYearSet) {
            int tsYear = tsYearNumber.intValue();
            if (targetSeries.containsKey(tsYear)) {
                Double value = targetSeries.get(tsYear).doubleValue();
                Double tsValue = ts.get(tsYear).doubleValue();
                targetSeries.put(tsYear, value + tsValue);
            } else {
                Double tsValue = ts.get(tsYear).doubleValue();
                targetSeries.put(tsYear, tsValue);
            }
        }
        return targetSeries;
    }

    /** Returns all years for this time series (in any order). */
    public Collection<Number> years() {
        Collection<Number> yearSet = new ArrayList(this.keySet());
        return yearSet;
    }

    /** Returns all data for this time series. 
      * Must be in the same order as years(). */
    public Collection<Number> data() {
        Collection<Number> yearSet = this.years();
        Collection<Number> dataSet = new ArrayList();
        for (Number yearNumber : yearSet) {
            int year = yearNumber.intValue();
            Number value = this.get(year);
            dataSet.add(value);
        }
        return dataSet;
    }
}
