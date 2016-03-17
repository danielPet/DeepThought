import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public class GitletVCS implements Serializable {
    private ArrayList<File> stagedFiles;
    private ArrayList<File> markedFiles;
    private Integer currentId = 1;

    private TreeMap<Integer, Commit> commitTree;

    private Branch master;
    private Branch currBranch;
    private ArrayList<Branch> branches;

    public GitletVCS() {
        stagedFiles = new ArrayList<File>();
        markedFiles = new ArrayList<File>();
        commitTree = new TreeMap<Integer, Commit>();
        
        Integer currID = 0;
        Commit initCommit = new Commit(currID, "initial commit", null);
        commitTree.put(currID, initCommit);

        File initCommitFolder = new File(".gitlet/" + currID.toString());
        initCommitFolder.mkdir();

        master = new Branch("master", initCommit);
        branches = new ArrayList<Branch>();
        branches.add(master);
        currBranch = master;
    }

    public void stage(File file) {
        stagedFiles.add(file);
    }

    public void clearStaging() {
        stagedFiles.clear();
    }

    public void clearMarks() {
        markedFiles.clear();
    }

    public Integer newID() {
        currentId = currentId + 1;
        return (currentId - 1);
    }

    public Integer currentID() {
        return (currentId - 1);
    }


    // Branch operations.
    public Branch getBranch() {
        return currBranch;
    }

    public void changeBranch(Branch br) {
        currBranch = br;
    }

    public void pointBranch(Commit commit) {
        currBranch.pointTo(commit);
    }

    public ArrayList<File> getStaged() {
        return stagedFiles;
    }

    public ArrayList<File> getMarked() {
        return markedFiles;
    }

    public TreeMap<Integer, Commit> getCommitTree() {
        return commitTree;
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }
}
