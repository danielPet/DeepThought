// Input/Output methods and exceptions.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

// Utilities for checking modification of files.
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

// Utilities for advanced file operations.
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.util.ArrayList;


public class Gitlet {

    private static GitletVCS vcs;
    private static boolean initialized;

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        String command = args[0];

        Path gitletPath = Paths.get(System.getProperty("user.dir"), ".gitlet");
        if (Files.exists(gitletPath)) {
            vcs = (GitletVCS) deSerialize(".gitlet/GitletVCS.ser");
            initialized = true;
        } else {
            initialized = false;
        }

        if (command.equals("init")) {
            init();
        } else if (!initialized) {
            System.out.println("Gitlet is not initialized in the current directory.");
        } else if (command.equals("log")) {
            log();
        } else if (command.equals("global-log")) {
            globalLog();
        } else if (command.equals("status")) {
            status();
        } else if (args.length < 2) {
            return;
        } else if (command.equals("add")) {
            String addedFileName = args[1];
            add(addedFileName);
        } else if (command.equals("commit")) {
            String message = args[1];
            commit(message);
        } else if (command.equals("rm")) {
            String removedFileName = args[1];
            remove(removedFileName);
        } else if (command.equals("find")) {
            String message = args[1];
            find(message);
        } else if (command.equals("checkout")) {
            if (args.length == 2) {
                String name = args[1];
                checkoutBranch(name);
            } else if (args.length == 3) {
                Integer id = Integer.parseInt(args[1]);
                String name = args[2];
                checkoutID(id, name);
            }
        } else if (command.equals("branch")) {
            String branchName = args[1];
            createBranch(branchName);
        } else if (command.equals("rm-branch")) {
            String branchName = args[1];
            destroyBranch(branchName);
        } else if (command.equals("reset")) {
            Integer id = Integer.parseInt(args[1]);
            reset(id);
        } else if (command.equals("merge")) {
            String branchName = args[1];
            merge(branchName);
        } else if (command.equals("rebase")) {
            String branchName = args[1];
            rebase(branchName);
        } else if (command.equals("i-rebase")) {
            String branchName = args[1];
            iRebase(branchName);
        } else {
            return;
        }

        if (initialized) {
            serialize(vcs, ".gitlet/GitletVCS.ser");
        }
    }


    public static void init() {
        if (!initialized) {
            File gitlet = new File(".gitlet");
            gitlet.mkdir();
            vcs = new GitletVCS();
            initialized = true;
        } else {
            System.out.println("A gitlet version control system "
                + "already exists in the current directory.");
        }
    }

    public static void add(String fileName) { // marking for removal
        Boolean canAdd = true;
        File addedFile = new File(fileName);

        // If marked, simply unmark the file.
        if (vcs.getMarked().contains(addedFile)) {
            vcs.getMarked().remove(addedFile);
            return;
        }
        // File already added.
        if (vcs.getStaged().contains(addedFile)) {
            return;
        }
        // If the file has been modified, we can add it.
        if (addedFile.isFile()) {
            if (vcs.getBranch().getCommit().getMap().containsKey(addedFile)) {
                File commitVersion = vcs.getBranch().getCommit().getMap().get(addedFile);
                if ((!isModifiedVersion(addedFile, commitVersion))) {
                    System.out.println("File has not been modified since the last commit.");
                    canAdd = false;
                    return;
                }
            }
        } else {
            canAdd = false;
            System.out.println("File does not exist.");
        }

        if (canAdd) {
            vcs.stage(addedFile);
        }
    }

    public static void commit(String message) {
        // No files are staged
        if (vcs.getStaged().isEmpty()) {
            System.out.println("No files to be commited.");
            return;
        }
        Integer currID = vcs.newID(); // ID iterator with its state tracked in GitletVCS.
        Path folderPath = Paths.get(System.getProperty("user.dir"), ".gitlet", currID.toString());
        File commitFolder = new File(folderPath.toString());
        commitFolder.mkdir();

        Commit newCommit = new Commit(currID, message, vcs.getBranch().getCommit());

        // Inheriting files from the current commit.
        for (File f : vcs.getBranch().getCommit().getMap().keySet()) {
            if ((!vcs.getStaged().contains(f)) && (!vcs.getMarked().contains(f))) {
                newCommit.track(f, vcs.getBranch().getCommit().getFile(f));
            }
        }

        File targetFile; // Where the file will be copied.
        File subDir; // The subdirectory for the file (if given).

        for (File file : vcs.getStaged()) {

            if (file.getName().equals(file.toString())) { // Check if file is in a subdirectory.
                targetFile = new File(folderPath.toString() + "/" + file.getName());
            } else {
                subDir = new File(folderPath.toString() +  "/" + file.getParent());
                subDir.mkdir();
                targetFile = new File(subDir.toString() + "/" + file.getName());
            }

            try {
                Files.copy(file.toPath(), targetFile.toPath());
                newCommit.track(file, targetFile);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }
        vcs.getCommitTree().put(currID, newCommit);
        vcs.pointBranch(newCommit);
        vcs.clearStaging();
        vcs.clearMarks();
    }

    public static void remove(String fileName) {
        File currFile = new File(fileName);
        
        Path filePath = Paths.get(System.getProperty("user.dir"), ".gitlet", 
            vcs.currentID().toString(), fileName);
        File removedFile = new File(filePath.toString());

        if (vcs.getStaged().contains(currFile)) {
            vcs.getStaged().remove(currFile);
        } else if (vcs.getBranch().getCommit().getMap().containsKey(currFile)) {
            vcs.getMarked().add(currFile);
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void log() {
        Branch currBranch = vcs.getBranch();
        Commit currCommit = currBranch.getCommit();

        while (currCommit != null) {
            logPrinter(currCommit);
            currCommit = currCommit.getParent();
        }
    }

    public static void globalLog() {
        for (Integer id : vcs.getCommitTree().descendingKeySet()) { // CAnnot allow repititions.
            logPrinter(vcs.getCommitTree().get(id));
        }
    }

    public static void find(String message) {
        Boolean notFound = true;
        for (Integer id : vcs.getCommitTree().descendingKeySet()) { // CAnnot allow repititions.
            //logPrinter(vcs.getCommitTree().getTree().get(id));
            Commit currCommit = vcs.getCommitTree().get(id);
            String currMessage = currCommit.getMessage();
            if (message.equals(currMessage)) {
                notFound = false;
                System.out.println(id);
            }
        }
        if (notFound) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        String currBranchName = vcs.getBranch().getName();
        System.out.println("=== Branches ===");
        System.out.print("*");
        System.out.println(currBranchName);
        for (Branch br : vcs.getBranches()) {
            if (br != vcs.getBranch()) {
                System.out.println(br.getName());
            }
        }
        System.out.println(""); // Possibly too many spaces here.
        System.out.println("=== Staged Files ===");
        for (File file : vcs.getStaged()) {
            System.out.println(file.toString());
        }
        System.out.println("");
        System.out.println("=== Files Marked for Removal ===");
        for (File file : vcs.getMarked()) {
            System.out.println(file.toString());
        }
    }

    public static void checkoutBranch(String name) {
        if (vcs.getBranch().getName().equals(name)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Branch newBranch = new Branch();
        Boolean isBranch = false;
        for (Branch br : vcs.getBranches()) {
            if (br.getName().equals(name)) {
                newBranch = br;
                isBranch = true;
            }
        }
        if (isBranch) {
            vcs.changeBranch(newBranch);
            try {
                Boolean df = dangerConfirmation();
                if (df) {
                    Commit currCommit = vcs.getBranch().getCommit();
                    for (File sourceFile : currCommit.getMap().keySet()) {
                        File source = currCommit.getMap().get(sourceFile);
                        File target = new File(sourceFile.toString());
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                    }
                } else {
                    return;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        } else {
            checkoutFN(name);
        }
    }

    public static void checkoutFN(String fileName) {
        try {
            File target = new File(fileName);
            Boolean notFound = true;
            Commit currCommit = vcs.getBranch().getCommit();
            for (File oldFile : currCommit.getMap().keySet()) {
                if (target.toString().equals(oldFile.toString())) {
                    notFound = false;
                    Boolean df = dangerConfirmation();
                    if (df) {
                        File source = currCommit.getMap().get(oldFile);
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                    } else {
                        return;
                    }
                }
            }
            if (notFound) {
                System.out.println("File does not exist in the most recent "
                    + "commit, or no such branch exists.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
    }

    public static void checkoutID(Integer id, String fn) {
        File target = new File(fn);
        if (vcs.getCommitTree().containsKey(id)) {
            Commit givenCommit = vcs.getCommitTree().get(id);
            if (givenCommit.getMap().containsKey(target)) {
                try {
                    Boolean df = dangerConfirmation();
                    if (df) {
                        File source = givenCommit.getMap().get(target);
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                    } else {
                        return;
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    return;
                }
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void createBranch(String name) {
        Boolean alreadyExists = false;
        for (Branch br : vcs.getBranches()) {
            if (br.getName().equals(name)) {
                alreadyExists = true;
                System.out.println("A branch with that name already exists.");
            }
        }
        if (!alreadyExists) {
            vcs.getBranches().add(new Branch(name, vcs.getBranch().getCommit()));
        }

    }

    public static void destroyBranch(String name) {
        Boolean exists = false;
        if (vcs.getBranch().getName().equals(name)) {
            System.out.println("Cannot remove the current branch.");
        }
        for (Branch br : vcs.getBranches()) {
            if (br.getName().equals(name)) {
                vcs.getBranches().remove(br);
                exists = true;
            }
        }
        if (!exists) {
            System.out.println("A branch with that name does not exist.");
        }
    }

    public static void reset(Integer id) {
        if (vcs.getCommitTree().containsKey(id)) {
            Commit givenCommit = vcs.getCommitTree().get(id);
            vcs.pointBranch(givenCommit);
            try {
                Boolean df = dangerConfirmation();
                if (df) {
                    for (File target : givenCommit.getMap().keySet()) {
                        File source = givenCommit.getMap().get(target);
                        Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                    }
                } else {
                    return;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void merge(String givenB) {
        Boolean exists = false;
        Branch givenBranch = new Branch();
        if (vcs.getBranch().getName().equals(givenB)) {
            System.out.println("Cannot merge a branch with itself.");
        }
        for (Branch br : vcs.getBranches()) {
            if (br.getName().equals(givenB)) {
                givenBranch = br;
                exists = true;
            }
        }
        if (!exists) {
            System.out.println("A branch with that name does not exist.");
        } else {
            Integer split = splitPoint(givenBranch, vcs.getBranch());
            Commit splitCommit = vcs.getCommitTree().get(split);
            Commit currCommit = vcs.getBranch().getCommit();
            Commit givenCommit = givenBranch.getCommit();
            try {
                Boolean df = dangerConfirmation();
                if (df) {
                    for (File f : givenCommit.getMap().keySet()) {
                        if ((splitCommit.getMap().containsKey(f)) 
                            && (currCommit.getMap().containsKey(f))) {
                            File currVersion = currCommit.getMap().get(f);
                            File splitVersion = splitCommit.getMap().get(f);
                            if ((isModifiedVersion(f, splitVersion)) 
                                && (!isModifiedVersion(currVersion, splitVersion))) {
                                File source = givenCommit.getMap().get(f);
                                File target = f;
                                Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                            } else if ((isModifiedVersion(f, splitVersion)) 
                                && (isModifiedVersion(currVersion, splitVersion))) {
                                File source = givenCommit.getMap().get(f);
                                String p = f.getPath() + ".conflicted";
                                File target = new File(p);
                                Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                            }
                        } else if (!splitCommit.getMap().containsKey(f)) {
                            File source = givenCommit.getMap().get(f);
                            File target = f;
                            Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        }
                    }
                } else {
                    return;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }
    }

    public static void rebase(String bn) {
        Boolean exists = false;
        Branch givenBranch = new Branch();
        if (vcs.getBranch().getName().equals(bn)) {
            System.out.println("Cannot rebase a branch onto itself.");
            return;
        }
        for (Branch br : vcs.getBranches()) {
            if (br.getName().equals(bn)) {
                givenBranch = br;
                exists = true;
            }
        }
        if (!exists) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (vcs.getBranch().hasInHistory(givenBranch)) {
            System.out.println("Already up-to-date");
            return;
        } else {
            return;
        }
    }

    public static void iRebase(String bn) {
        rebase(bn);
    }


// GITLET UTILITY METHODS

    public static boolean dangerConfirmation() {
        // Returns a boolean which is true if the user inputs "yes" and false if "no".
        System.out.println("Warning: The command you entered may " 
            + "alter the files in your working directory. " 
            + "Uncommitted changes may be lost. Are you sure you want to continue? (yes/no)");
        while (true) {
            System.out.print("> ");
            String firstInput = StdIn.readString();
            switch (firstInput) {
                case "yes": 
                    return true;
                case "no": 
                    return false;
                default: 
                    return false;
            }
        }
    }


    public static Integer splitPoint(Branch b1, Branch b2) {
        // Returns the ID of the latest split-point commit which the two branches share.
        ArrayList<Integer> b1ids = new ArrayList<Integer>();

        Commit b1Commit = b1.getCommit();

        while (b1Commit != null) {
            b1ids.add(b1Commit.getID());
            b1Commit = b1Commit.getParent();
        }

        Commit b2Commit = b2.getCommit();

        while (b2Commit != null) {
            if (b1ids.contains(b2Commit.getID())) {
                return b2Commit.getID();
            } else {
                b2Commit = b2Commit.getParent();
            }
        }
        return 0;
    }

    public static void logPrinter(Commit commit) {
        System.out.println("====");
        System.out.println("Commit " + commit.getID().toString() + ".");
        System.out.println(commit.getDate());
        System.out.println(commit.getMessage());
        System.out.println("");
    }

    private static Boolean isModifiedVersion(File newF, File oldF) {
        try {
            FileInputStream fstream1 = new FileInputStream(newF);
            FileInputStream fstream2 = new FileInputStream(oldF);

            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
            DataInputStream in2 = new DataInputStream(fstream2);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
            String strLine1, strLine2;
            while (((strLine1 = br1.readLine()) != null) && ((strLine2 = br2.readLine()) != null)) {
                if (!strLine1.equals(strLine2)) {
                    return true;
                }
            }
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

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
