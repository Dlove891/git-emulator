package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Commit implements Serializable {
    /**
     * the message of the commit.
     */
    private String _message;
    /**
     * the sha1 of the parent of the commit, unless null.
     */
    private String _parent;
    /**
     * hashmap containing the name of the file.
     * as the key and the sha1 of the file as the value.
     */
    private HashMap<String, String> _blobs;

    /**
     * date the commit takes place. accurate to real time.
     */
    private String date;

    /**
     * initializes a commit.
     * date and time idea is from youtube and other students advising about.
     * simple date format and date imports.
     *
     * @param message
     * @param serialBlobs
     * @param sParent
     */
    public Commit(String message, HashMap<String,
            String> serialBlobs, String sParent) {
        if (sParent != null) {
            SimpleDateFormat D =
                    new SimpleDateFormat("EEE MMM d hh:mm:ss YYYY Z");
            Date dat = new Date();
            String res = D.format(dat);
            this.date = res;
        } else {
            SimpleDateFormat D =
                    new SimpleDateFormat("EEE MMM d hh:mm:ss YYYY Z");
            Date dat = new Date(0);
            String res = D.format(dat);
            this.date = res;
        }
        this._message = message;
        this._blobs = serialBlobs;
        this._parent = sParent;
    }

    /**
     * gets the sha1 of the commit.
     *
     * @param commit
     * @return
     */
    public static String getSha1(Commit commit) {
        byte[] bytes = Utils.serialize(commit);
        String sha = Utils.sha1(bytes);
        return sha;
    }

    public String getmessage() {
        return _message;
    }

    public String getparent() {
        return _parent;
    }

    public HashMap<String, String> getblobs() {
        return _blobs;
    }

    public String getdate() {
        return date;
    }
}
