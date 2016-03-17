public class NoteNode {

    String string;
    int fret, offset;
    double frequency;
    synthesizer.GuitarString note;
    double[] freqs = {82.4, 87.3, 92.5, 98.0, 103.8, 110.0, 116.5, 123.5, 130.8, 138.6, 146.8, 155.6, 164.8, 174.6, 185.0, 196.0, 207.6, 220.0, 233.1, 246.9, 261.6, 277.2, 293.6, 311.1, 329.6, 349.2, 370.0, 392.0, 415.3, 440.0, 466.1, 493.8, 523.2, 554.3, 587.3, 622.2, 659.2};

// 37 freqs
    public static void main(String[] args) {
    }

    public NoteNode(String s, int f) {
        string = s;
        fret = f;

        if (string.equals("E")) {
            offset = fret;
        } else if (string.equalsIgnoreCase("a")) {
            offset = 5 + fret;
        } else if (string.equalsIgnoreCase("d")) {
            offset = 10 + fret;
        } else if (string.equalsIgnoreCase("g")) {
            offset = 15 + fret;
        } else if (string.equalsIgnoreCase("b")) {
            offset = 19 + fret;
        }  else if (string.equals("e")) {
            offset = 24 + fret;
        }

        frequency = freqs[offset];
        note = new synthesizer.GuitarString(frequency);


    }
    public void playNote() {
        int i = 1;
        note.pluck();
        while (i < 15000) {
            double sample = note.sample();
            StdAudio.play(sample);
            note.tic();
            i += 1;
        }
        return;
    }

/*
    public String getName() {
        return this.name;
    }
    public int getCode() {
        return this.code;
    }*/
}