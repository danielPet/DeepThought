import java.io.Serializable;

public class Branch implements Serializable {
    private String name;
    private Commit currCommit;

    public Branch() {
        this.name = "empty";
        this.currCommit = null;
    }

    public Branch(String branchName, Commit pointingAt) {
        this.name = branchName;
        this.currCommit = pointingAt;
    }

    public void pointTo(Commit pointingAt) {
        this.currCommit = pointingAt;
    }

    public String getName() {
        return name;
    }

    public Commit getCommit() {
        return currCommit;
    }

    public Boolean hasInHistory(Branch b) {
        Commit c = currCommit;
        while (c != null) {
            if (c.getID() == b.getCommit().getID()) {
                return true;
            }
            c = c.getParent();
        }
        return false;
    }
}
