// Make sure to make this class a part of the synthesizer package
//package <package name>;
package synthesizer;

//Make sure this class is public
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final means
      * the values cannot be changed at runtime. We'll discuss this and other topics
      * in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .99; // energy decay factor
    private double cap;
    /* Buffer for storing sound data. */
    private BoundedQueue buffer;
    
    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        // TODO: Create a buffer with capacity = SR / frequency. You'll need to
        //       cast the result of this divsion operation into an int. For better
        //       accuracy, use the Math.round() function before casting.
        //       Your buffer should be initially filled with zeros.
        double cap = (SR / frequency);
        cap = Math.round(cap);
        buffer = new ArrayRingBuffer((int) cap);
        for (int i = 0; i < cap ; i++) {
            buffer.enqueue(0);
        }
    }
    
    
    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        // TODO: Dequeue everything in the buffer, and replace it with random numbers
        //       between -0.5 and 0.5. You can get such a number by using:
        //       double r = Math.random() - 0.5;
        //
        //       Make sure that your random numbers are different from each other.
        double r;
        for (int i = 0; i < cap + 1 ; i++) {
            buffer.dequeue();
            r = 0.5;
            //r = Math.random() - 0.5;
            buffer.enqueue(r);
        }
    }
    
    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        // TODO: Dequeue the front sample and enqueue a new sample that is
        //       the average of the two multiplied by the DECAY factor.
        //       Do not call StdAudio.play().
        double primary = buffer.dequeue();
        double newSample = (DECAY / 2) * (primary + sample());
        buffer.enqueue(newSample);
    }
    
    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.peek();
    }
    
}
