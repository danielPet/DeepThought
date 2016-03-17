import java.util.Comparator;

/**
 * TermNode class for pairing terms and weights.
 * @author Daniel Peterson
 */
public class TermNode implements Comparator<TermNode>, Comparable {
    public String str;
    public Double d;

   /**
    * Initializes a termnode which links a term with its weight.
    * @param t Term string
    * @param e Weight
    */
    public TermNode(String t, Double e) {
        str = t;
        d = e;
    }

   /**
    * Zero-argument constructor for the termnode class.
    */
    public TermNode() {

    }

   /**
    * Overriding the compare method to compare nodes based on their weight
    * @param n1 First node to compare
    * @param n2 Second node to compare
    * @return returns the int value of the comparison (as required by the interface)
    */
    public int compare(TermNode n1, TermNode n2) {
        if (n1.d - n2.d < 0) {
            return -1;
        } else {
            return 1;
        }
    }

   /**
    * Overriding the compare method to compare nodes based on their weight
    * @param n  node to compare this node to
    * @return returns the int value of the comparison (as required by the interface)
    */
    public int compareTo(Object n) {
        TermNode tn = (TermNode) n;
        if (this.d - tn.d < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}

