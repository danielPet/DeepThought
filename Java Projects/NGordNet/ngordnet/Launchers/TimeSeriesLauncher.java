package ngordnet;

import java.util.Collection;

public class TimeSeriesLauncher {
    public static void main(String[] args) {
        TimeSeries<Double> ts = new TimeSeries<Double>();

        /* You will not need to implement the put method, since your
           TimeSeries class should extend the TreeMap class. */
        ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);

        /* Gets the years and data of this TimeSeries. 
         * Note, you should never cast these to another type, even
         * if you happen to know how the Collection<Number> is implemented. */
        Collection<Number> years = ts.years();
        Collection<Number> data = ts.data();

        for (Number yearNumber : years) {
            /* This awkward conversion is necessary since you cannot
             * do yearNumber.get(yearNumber), since get expects as
             * Integer since TimeSeries always require an integer
             * key. 
             *
             * Your output may be in any order. */
            int year = yearNumber.intValue();
            double value = ts.get(year);
            System.out.println("In the year " + year + " the value was " + value);
        }

        for (Number dataNumber : data) {

            double datum = dataNumber.doubleValue();
            System.out.println("In some year, the value was " + datum);
        }  

        TimeSeries<Integer> ts2 = new TimeSeries<Integer>();
        ts2.put(1991, 10);
        ts2.put(1992, -5);
        ts2.put(1993, 1);

        TimeSeries<Double> tSum = ts.plus(ts2);
        System.out.println(tSum.get(1991)); // should print 10
        System.out.println(tSum.get(1992)); // should print -1.4  

        TimeSeries<Double> ts3 = new TimeSeries<Double>();
        ts3.put(1991, 5.0);
        ts3.put(1992, 1.0);
        ts3.put(1993, 100.0);

        TimeSeries<Double> tQuotient = ts2.dividedBy(ts3);

        System.out.println(tQuotient.get(1991)); // should print 2.0

        TimeSeries<Double> quotient = ts.dividedBy(ts2);
    }
}