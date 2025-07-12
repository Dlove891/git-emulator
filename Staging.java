package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * class that only really sets up the two.
 * staging areas for addition and removal.
 * not yet too verbose.
 * @author Dlove
 */
public class Staging implements Serializable {
    /**
     * the stage where added files go.
     */
    private HashMap<String, String> stagedToAdd;
    /**
     * the stage where everything that is removed goes.
     */
    private HashMap<String, String> stagedToRemove;

    public Staging() {
        this.stagedToAdd = new HashMap<>();
        this.stagedToRemove = new HashMap<>();
    }

    public static void add(HashMap<String, String> hashMap,
                           String key, String val) {
        hashMap.put(key, val);
    }

    public HashMap<String, String> getStagedToAdd() {
        return stagedToAdd;
    }

    public HashMap<String, String> getStagedToRemove() {
        return stagedToRemove;
    }
}
