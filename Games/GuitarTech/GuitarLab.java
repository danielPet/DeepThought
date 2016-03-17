// Input/Output methods and exceptions.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import edu.princeton.cs.introcs.StdIn;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;



public class GuitarLab {
    private static GLData data;
    private static boolean initialized;

    public static void main(String[] args) {
        // Checking if music repository is initialized.
        Path dataPath = Paths.get(System.getProperty("user.dir"), ".gld");
        if (Files.exists(dataPath)) {
            data = (GLData) deSerialize(".gld/GLData.ser");
            initialized = true;
        } else {
            initialized = false;
        }



        while (true) {
            System.out.print("GL> ");
            String line = StdIn.readLine();
            String[] rawTokens = line.split(" ");
            String command = rawTokens[0];
            String[] tokens = new String[rawTokens.length - 1];
            System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
            switch (command) {
                case "quit":
                    if (initialized) {
                        serialize(data, ".gld/GLData.ser");
                    } else {
                        System.out.println("There was an error.");
                    }
                    return;
                case "help":
                    help();
                    break;
                case "play":
                    if (tokens.length < 1) {
                        System.out.println("Enter a note (A, B, etc.)");
                    } else if (tokens.length == 1) {
                        String note = tokens[0];
                        playNote(note);
                    } else if ((tokens.length == 2) && (tokens[0].equalsIgnoreCase("frequency"))) {
                        double freq = Double.parseDouble(tokens[1]);
                        playFrequency(freq);
                    } else if ((tokens.length == 3) && (tokens[0].equalsIgnoreCase("all")) && (tokens[1].equalsIgnoreCase("notes"))) {
                        playAllNotes();
                    }
                    break;
                case "initialize": 
                    init();
                    break;
                default:
                    System.out.println("Invalid command. Type 'help' for a list.");
                    break;
            }
        }
    }
/*
    if (args.length == 0) {
            System.out.println("Command required. Use the command 'help' for a list of commands.");
            return;
        }

        String command = args[0];

        if (command.equals("help")) {
            help();
        } else if (command.equals("play")) {
            System.out.println(args.length);
        }
        else if (command.equals("initialize")) {
            init();
        } else if (!initialized) {
            System.out.println("This process requires initialization.");
        }

        if (initialized) {
            serialize(data, ".gld/GLData.ser");
        }
    }*/



    public static void help() {
        System.out.println("The currently implemented commands are:");
        System.out.println("help -- Lists commands.");
    }

    public static void init() {
        System.out.println("Initializing here.");
        if (!initialized) {
            File gld = new File(".gld");
            gld.mkdir();
            data = new GLData();
            initialized = true;
        } else {
            System.out.println("A repository already exists here.");
        }
    }

    public static void playNote(String note) {
        if (note.length() == 1) {
            NoteNode nn = new NoteNode(note, 0);
            nn.playNote();
        } else if (note.equalsIgnoreCase("standard")) {
            NoteNode nn = new NoteNode("E", 0);
            nn.playNote();
            nn = new NoteNode("A", 0);
            nn.playNote();
            nn = new NoteNode("D", 0);
            nn.playNote();
            nn = new NoteNode("G", 0);
            nn.playNote();
            nn = new NoteNode("B", 0);
            nn.playNote();
            nn = new NoteNode("e", 0);
            nn.playNote();
        } else {
            System.out.println("Not a valid note to play.");
        }
        
    }

    public static void playAllNotes(String note) {
        if (note.length() == 1) {
            NoteNode nn = new NoteNode(note, 0);
            nn.playNote();
        } else if (note.equalsIgnoreCase("standard")) {
            NoteNode nn = new NoteNode("E", 0);
            nn.playNote();
            nn = new NoteNode("A", 0);
            nn.playNote();
            nn = new NoteNode("D", 0);
            nn.playNote();
            nn = new NoteNode("G", 0);
            nn.playNote();
            nn = new NoteNode("B", 0);
            nn.playNote();
            nn = new NoteNode("e", 0);
            nn.playNote();
        } else {
            System.out.println("Not a valid note to play.");
        }
        
    }
/*        double E_FREQ = 82.41;
        double A_FREQ = 110.00;
        double D_FREQ = 146.83;
        double G_FREQ = 196.00;
        double B_FREQ = 246.94;
        double e_FREQ = 329.63;
        synthesizer.GuitarString string = new synthesizer.GuitarString(E_FREQ);
        if (note.length() == 1) {
            if (note.equals("E")) {
                string = new synthesizer.GuitarString(E_FREQ);
            } else if (note.equalsIgnoreCase("a")) {
                string = new synthesizer.GuitarString(A_FREQ);
            } else if (note.equalsIgnoreCase("d")) {
                string = new synthesizer.GuitarString(D_FREQ);
            } else if (note.equalsIgnoreCase("g")) {
                string = new synthesizer.GuitarString(G_FREQ);
            } else if (note.equalsIgnoreCase("b")) {
                string = new synthesizer.GuitarString(B_FREQ);
            }  else if (note.equals("e")) {
                string = new synthesizer.GuitarString(e_FREQ);
            }
            int i = 1;
            string.pluck();
            while (i < 100000) {
                double sample = string.sample();
                StdAudio.play(sample);
                string.tic();
                i += 1;
            }
        } else if (note.equalsIgnoreCase("standard")) {
            for (int j = 0; j < 6; j += 1) {
                if (j == 0) {
                    string = new synthesizer.GuitarString(E_FREQ);
                } else if (j == 1) {
                    string = new synthesizer.GuitarString(A_FREQ);
                } else if (j == 2) {
                    string = new synthesizer.GuitarString(D_FREQ);
                } else if (j == 3) {
                    string = new synthesizer.GuitarString(G_FREQ);
                } else if (j == 4) {
                    string = new synthesizer.GuitarString(B_FREQ);
                } else if (j == 5) {
                    string = new synthesizer.GuitarString(e_FREQ);
                }
                int i = 1;
                string.pluck();
                while (i < 100000) {
                    double sample = string.sample();
                    StdAudio.play(sample);
                    string.tic();
                    i += 1;
                }
            }
        } else {
            System.out.println("Invalid key to play.");
        }*/
    

    public static void playFrequency(double freq) {
        synthesizer.GuitarString string = new synthesizer.GuitarString(freq);
        int i = 1;
        string.pluck();
        while (i < 100000) {
            double sample = string.sample();
            StdAudio.play(sample);
            string.tic();
            i += 1;
        }
    }



// UTILITIES

    public static void serialize(Object obj, String serName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(serName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        }
    }

    public static Object deSerialize(String serName) {
        Object obj = null;
        try {
            FileInputStream fileIn = new FileInputStream(serName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            obj = in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }
        return obj;
    }
}