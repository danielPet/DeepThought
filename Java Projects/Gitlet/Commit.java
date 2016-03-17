import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.util.Date;

/*A commit object which contains fields for an ID, a message, a date, its parent commit,
and a TreeMap of trackedFiles.*/

public class Commit implements Serializable {
    private Integer id;
    private String message;
    private Date date;
    private String dateString;
    private Commit parent;

    // Maps standard filename to its pathname in the .gitlet folder.
    private TreeMap<File, File> trackedFiles;

    public Commit(Integer idNum, String messageStr, Commit preceding) {
        id = idNum;
        message = messageStr;
        parent = preceding;
        trackedFiles = new TreeMap<File, File>();

        date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        dateString = format.format(date);
    }

    public Integer getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return dateString;
    }

    public Commit getParent() {
        return parent;
    }

    public void track(File f, File fGitPath) {
        trackedFiles.put(f, fGitPath);
    }

    public File getFile(File f) {
        return trackedFiles.get(f);
    }

    public TreeMap<File, File> getMap() {
        return trackedFiles;
    }
}
