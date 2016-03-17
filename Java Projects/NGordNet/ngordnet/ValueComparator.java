package ngordnet;

import java.util.Comparator;
import java.util.Map;

    /** Portions of comparator class below adapted from (though written by me):
      * http://stackoverflow.com/questions/109383/
      * how-to-sort-a-mapkey-value-on-the-values-in-java
      */

class ValueComparator implements Comparator<String> {
    Map<String, Integer> map;
    public ValueComparator(Map<String, Integer> basisMap) {
        this.map = basisMap;
    }
    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return 1;
        } else {
            return -1;
        }
    }
}
