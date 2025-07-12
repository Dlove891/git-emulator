package gitlet;


import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Repo implements Serializable {
    /**
     * Stage.
     */
    private Staging _stage;
    /**
     * head.
     */
    private String _HEAD;
    /**
     * gitlet.
     */
    private final File _GITLET = Utils.join(Main.CWD, ".gitlet");
    /**
     * blobs.
     */
    private final File _BLOBS = Utils.join(_GITLET, "blobs");
    /**
     * stages.
     */
    private final File _STAGES = Utils.join(_GITLET, "staging");
    /**
     * commits.
     */
    private final File _COMMITS = Utils.join(_GITLET, "commits");
    /**
     * branches.
     */
    private final File _BRANCHES = Utils.join(_GITLET, "branches");

    /**
     * intitializes a repository object and checks if
     * HEAD.txt anf stage.txt exists, if either one of them does,
     * then they are assigned to the corresponding instance variables.
     */
    public Repo() {
        File hEADpath = Utils.join(_BRANCHES, "HEAD.txt");
        File stagePath = Utils.join(_STAGES, "stage.txt");
        if (hEADpath.exists()) {
            _HEAD = Utils.readContentsAsString(hEADpath);
        }
        if (stagePath.exists()) {
            _stage = Utils.readObject(stagePath, Staging.class);
        }
    }

    /**
     * initializes a .gitlet repository and all the corresponding directories
     * and files.
     *
     * @throws IOException
     */
    public void init() throws IOException {
        File checker = Utils.join(Main.CWD, "/.gitlet");
        if (checker.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
        }
        _GITLET.mkdir();
        _BLOBS.mkdir();
        _COMMITS.mkdir();
        _STAGES.mkdir();
        _BRANCHES.mkdir();
        _stage = new Staging();
        _HEAD = "master";

        String initmsg = "initial commit";
        HashMap<String, String> empty = new HashMap<>();
        Commit firstCommit = new Commit(initmsg, empty, null);
        String name = Commit.getSha1(firstCommit);
        addCommit(name);
        File comm = Utils.join(_COMMITS, "/" + name + ".txt");
        Utils.writeObject(comm, firstCommit);

        File masterPath = new File(_BRANCHES + "/master.txt");
        File headPath = new File(_BRANCHES + "/HEAD.txt");
        File stg = new File(_STAGES + "/stage.txt");
        masterPath.createNewFile();
        headPath.createNewFile();
        stg.createNewFile();
        Utils.writeContents(masterPath, name);
        Utils.writeContents(headPath, "master");
        Utils.writeObject(stg, _stage);
    }

    /**
     * adds the file filename to the stagedtoadd hashmap, and updates the
     * stage.txt serialization.
     *
     * @param fileName
     */
    public void add(String fileName) {
        File file = Utils.join(Main.CWD, fileName);
        if (file.exists()) {
            if (_stage.getStagedToRemove().containsKey(fileName)) {
                _stage.getStagedToRemove().remove(fileName);
                File stg = Utils.join(_STAGES, "/stage.txt");
                Utils.writeObject(stg, _stage);
                return;
            }
            Commit current = getCurrent();
            if (current.getblobs().containsKey(fileName)) {
                String sS = current.getblobs().get(fileName);
                File fil = Utils.join(_BLOBS, "/" + sS + ".txt");
                File fIL = Utils.join(Main.CWD, "/" + fileName);
                String strung = Utils.readContentsAsString(fIL);
                String string = Utils.readObject(fil, String.class);
                if (strung.equals(string)) {
                    return;
                }
            }
            String str = Utils.readContentsAsString(file);
            String sha = sha1Helper(file);
            File blobID = Utils.join(_BLOBS, "/" + sha + ".txt");
            Utils.writeObject(blobID, str);
            Staging.add(_stage.getStagedToAdd(), fileName, sha);
            File sTage = new File(_STAGES + "/stage.txt");
            Utils.writeObject(sTage, _stage);
        } else {
            System.out.println("File does not exist.");
        }
    }

    /**
     * returns the sha of a file.
     *
     * @param file
     * @return
     */
    public String sha1Helper(File file) {
        byte[] arr = Utils.readContents(file);
        String sha = Utils.sha1(arr);
        return sha;
    }

    /**
     * does a commit where all the files that have been staged for addition,
     * will be saved within a commit. the stages will then be cleared as well.
     *
     * @param messege
     */
    public void commit(String messege) {
        if (messege.equals("")) {
            System.out.println("Please enter a commit message.");
        } else if (_stage.getStagedToAdd().size() == 0
                && _stage.getStagedToRemove().size() == 0) {
            System.out.println(" No changes added to the commit.");
        } else {
            Commit latestCommit = getCurrent();
            HashMap<String, String> blobCP = new HashMap<>();
            ArrayList<String> filesToBeAdded
                    = new ArrayList<>(_stage.getStagedToAdd().keySet());
            ArrayList<String> filesToBeRemoved
                    = new ArrayList<>(_stage.getStagedToRemove().keySet());
            for (String file : latestCommit.getblobs().keySet()) {
                blobCP.put(file, latestCommit.getblobs().get(file));
            }
            for (String item : filesToBeAdded) {
                blobCP.put(item, _stage.getStagedToAdd().get(item));
            }
            for (String removed : filesToBeRemoved) {
                blobCP.remove(removed);
            }
            Commit nEW = new Commit(messege, blobCP,
                    Commit.getSha1(latestCommit));
            File branch = Utils.join(_BRANCHES, '/' + _HEAD + ".txt");
            Utils.writeContents(branch, Commit.getSha1(nEW));
            File cOMMIT = Utils.join(_COMMITS, "/"
                    + Commit.getSha1(nEW) + ".txt");
            Utils.writeObject(cOMMIT, nEW);
            _stage.getStagedToRemove().clear();
            _stage.getStagedToAdd().clear();
            File stg = Utils.join(_STAGES, "/stage.txt");
            Utils.writeObject(stg, _stage);
        }

    }

    /**
     * gets the latest commit, master.txt should be pointing here.
     *
     * @return
     */
    public Commit getCurrent() {
        File head = Utils.join(_BRANCHES, _HEAD + ".txt");
        String sha = Utils.readContentsAsString(head);
        File headID = Utils.join(_COMMITS, "/" + sha + ".txt");
        Commit res = Utils.readObject(headID, Commit.class);
        return res;
    }

    public void addCommit(String iD) throws IOException {
        File file = new File(_COMMITS + "/" + iD + ".txt");
        file.createNewFile();
    }

    /**
     * init parent is null so loops until null
     * parent is found. Prints to terminal
     * the information about the commits along
     * with the sha to checkout them later on.
     */
    public void log() {
        Commit curr = getCurrent();
        while (curr.getparent() != null) {
            logformatter(curr);
            File parent = Utils.join(_COMMITS,
                    "/" + curr.getparent() + ".txt");
            if (curr.getparent() != null) {
                Commit ew = Utils.readObject(parent, Commit.class);
                curr = ew;
            }
        }
        logformatter(curr);
    }

    public void logformatter(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + Commit.getSha1(commit));
        System.out.println("Date: " + commit.getdate());
        System.out.println(commit.getmessage());
        System.out.println();

    }

    public void checkout(String[] args) {
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            Commit curr = getCurrent();
            if (!curr.getblobs().containsKey(args[2])) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String sha = curr.getblobs().get(args[2]);
            File old = Utils.join(_BLOBS, sha + ".txt");
            String contents = Utils.readObject(old, String.class);
            File neW = Utils.join(Main.CWD, args[2]);
            Utils.writeContents(neW, contents);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                return;
            }
            String com = args[1].substring(0, 6);
            String[] commits = _COMMITS.list();
            boolean checker = false;
            String sha = "";
            for (String commit : commits) {
                if (commit.substring(0, commit.length() - 4).length()
                        <= (10 + 10 + 10 + 10)) {
                    String sub = commit.substring(0, 6);
                    if (sub.equals(com)) {
                        checker = true;
                        sha = commit;
                    }
                }
            }
            if (!checker) {
                System.out.println("No commit with that id exists.");
                return;
            }
            File realCom = Utils.join(_COMMITS, "/" + sha);
            Commit look = Utils.readObject(realCom, Commit.class);
            if (!look.getblobs().containsKey(args[3])) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String curFile = look.getblobs().get(args[3]);
            File blob = Utils.join(_BLOBS, "/" + curFile + ".txt");
            String contents = Utils.readObject(blob, String.class);
            File nEW = Utils.join(Main.CWD, args[3]);
            Utils.writeContents(nEW, contents);
        }
        if (args.length == 2) {
            length2(args);
        }
    }

    public void length2(String[] args) {
        File fil = Utils.join(_BRANCHES, "/" + args[1] + ".txt");
        if (!fil.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        String sha = Utils.readContentsAsString(Utils.join(_BRANCHES,
                "/" + args[1] + ".txt"));
        File commit = Utils.join(_COMMITS, "/" + sha + ".txt");
        Commit com = Utils.readObject(commit, Commit.class);
        for (String filename : Utils.plainFilenamesIn(Main.CWD)) {
            if (com.getblobs().containsKey(filename)
                    && !_stage.getStagedToAdd().containsKey(filename)
                    && !getCurrent().getblobs().containsKey(filename)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }
        if (_HEAD.equals(args[1])) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        for (String file : Utils.plainFilenamesIn(Main.CWD)) {
            if (com.getblobs().containsKey(file)) {
                File old = Utils.join(Main.CWD, "/" + file);
                File neW = Utils.join(_BLOBS, "/"
                        + com.getblobs().get(file) + ".txt");
                String con = Utils.readObject(neW, String.class);
                Utils.writeContents(old, con);
            } else {
                File fill = Utils.join(Main.CWD, file);
                Utils.restrictedDelete(fill);
            }
        }
        List<String> allFiles = Utils.plainFilenamesIn(Main.CWD);
        Set<String> comKeys = com.getblobs().keySet();
        for (String key : comKeys) {
            if (!allFiles.contains(key)) {
                String blobLoc = com.getblobs().get(key);
                String contents = Utils.readObject
                        (new File(_BLOBS, blobLoc + ".txt"), String.class);
                Utils.writeContents(new File(Main.CWD, key), contents);
            }
        }
        _HEAD = args[1];
        File file = Utils.join(_BRANCHES, "/HEAD.txt");
        Utils.writeContents(file, _HEAD);
        _stage.getStagedToAdd().clear();
        _stage.getStagedToRemove().clear();
        File stg = Utils.join(_STAGES, "/stage.txt");
        Utils.writeObject(stg, _stage);
    }


    public void branch(String name) throws IOException {
        File branch = Utils.join(_BRANCHES, "/" + name + ".txt");
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
        }
        branch.createNewFile();
        Commit commit = getCurrent();
        String sha = Commit.getSha1(commit);
        Utils.writeContents(branch, sha);
    }

    public void rmBranch(String name) {
        if (_HEAD.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File branch = Utils.join(_BRANCHES, "/" + name + ".txt");
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        branch.delete();
    }

    public void status() {
        System.out.println("=== Branches ===");
        List<String> brchs = Utils.plainFilenamesIn(_BRANCHES);
        for (String bRAN : brchs) {
            String str = bRAN.substring(0, bRAN.length() - 4);
            if (!str.equals("HEAD")) {
                if (str.equals(_HEAD)) {
                    str = "*" + str;
                }
                System.out.println(str);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String str : _stage.getStagedToAdd().keySet()) {
            System.out.println(str);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String str : _stage.getStagedToRemove().keySet()) {
            System.out.println(str);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        helper1();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        helper2();
    }
    public void helper1() {
        Commit current = getCurrent();
        for (String blob : current.getblobs().keySet()) {
            File file = Utils.join(Main.CWD, blob);
            if (!file.exists()) {
                System.out.println(blob + "(deleted)");
            }
            File fIle = Utils.join(_BLOBS, "/" + current.getblobs().get(blob) + ".txt");
            String nEw = Utils.readObject(fIle, String.class);
            File fiLe = Utils.join(Main.CWD, "/" + blob);
            String old = Utils.readObject(fiLe, String.class);
            if (!nEw.equals(old)) {
                System.out.println(blob + "(modified)");
            }
        }

    }
    public void helper2() {

    }

    public void find(String comMes) {
        String[] commits = _COMMITS.list();
        int len = commits.length;
        int i = 0;
        Boolean checker = false;
        for (String commit : commits) {
            File file = Utils.join(_COMMITS, commit);
            Commit cOM = Utils.readObject(file, Commit.class);
            if ((i == len - 1)
                    && !(cOM.getmessage()).equals(comMes) && !checker) {
                System.out.println("Found no commit with that message.");
                return;
            }
            if ((cOM.getmessage()).equals(comMes)) {
                checker = true;
                System.out.println(Commit.getSha1(cOM));
            }
            i++;
        }

    }


    public void remove(String fileName) {
        if (_HEAD.equals("master")) {
            File head = Utils.join(_BRANCHES, "master.txt");
            rmHelper(fileName, head);
        } else {
            File head = Utils.join(_BRANCHES, _HEAD + ".txt");
            rmHelper(fileName, head);
        }
    }

    public void rmHelper(String fileName, File head) {
        String sha = Utils.readContentsAsString(head);
        File file = Utils.join(_COMMITS, sha + ".txt");
        Commit current = Utils.readObject(file, Commit.class);
        if (!current.getblobs().containsKey(fileName)
                && !_stage.getStagedToAdd().containsKey(fileName)) {
            System.out.println("No reason to remove the file");
        } else if (_stage.getStagedToAdd().containsKey(fileName)) {
            _stage.getStagedToAdd().remove(fileName);
            Utils.writeObject(new File(_STAGES + "/stage.txt"), _stage);
            return;
        } else if (current.getblobs().containsKey(fileName)) {
            _stage.getStagedToRemove().put(fileName,
                    current.getblobs().get(fileName));
            Utils.writeObject(new File(_STAGES + "/stage.txt"), _stage);
            File fILE = Utils.join(Main.CWD, fileName);
            if (fILE.exists()) {
                Utils.restrictedDelete(fILE);
            }
        }
    }

    public void reset(String iD) {
        String com = iD.substring(0, 6);
        String[] commits = _COMMITS.list();
        boolean checker = false;
        String sha = "";
        for (String commit : commits) {
            if (commit.substring(0, commit.length() - 4).length()
                    <= (10 + 10 + 10 + 10)) {
                String sub = commit.substring(0, 6);
                if (sub.equals(com)) {
                    checker = true;
                    sha = commit;
                }
            }
        }
        if (!checker) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (String filename : Utils.plainFilenamesIn(Main.CWD)) {
            if (!_stage.getStagedToAdd().containsKey(filename)
                    && !getCurrent().getblobs().containsKey(filename)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }
        File commit = Utils.join(_COMMITS, "/" + iD + ".txt");
        Commit cOMMit = Utils.readObject(commit, Commit.class);
        Set<String> strs = cOMMit.getblobs().keySet();
        File fILE = Utils.join(_BRANCHES, "/HEAD.txt");
        File file = Utils.join(_BRANCHES, "/master.txt");
        _HEAD = "master";
        Utils.writeContents(fILE, _HEAD);
        Utils.writeContents(file, iD);
        for (String str : strs) {
            String[] args = new String[3];
            args[0] = "checkout";
            args[1] = "--";
            args[2] = str;
            checkout(args);
        }
        _stage.getStagedToAdd().clear();
        _stage.getStagedToRemove().clear();
        File stg = Utils.join(_STAGES, "/stage.txt");
        Utils.writeObject(stg, _stage);

    }

    public void global() {
        List<String> strs = Utils.plainFilenamesIn(_COMMITS);
        for (String comit : strs) {
            File file = Utils.join(_COMMITS, comit);
            Commit commit = Utils.readObject(file, Commit.class);
            logformatter(commit);
        }
    }

    public void merge(String branch) {
        if (_stage.getStagedToAdd().size() != 0
                || _stage.getStagedToRemove().size() != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File brnch = Utils.join(_BRANCHES, "/" + branch + ".txt");
        if (!brnch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (_HEAD.equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
        }
        for (String filename : Utils.plainFilenamesIn(Main.CWD)) {
            if (!_stage.getStagedToAdd().containsKey(filename)
                    && !getCurrent().getblobs().containsKey(filename)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }
    }

    public void addRemote(String[] args) throws IOException {
        File file = Utils.join(Main.CWD, "/checker.txt");
        if (!file.exists()) {
            file.createNewFile();
            Utils.writeContents(file, "1");
        }
        if (args[2].equals("../D1/.gitlet")) {
            if (Utils.readContentsAsString(file).equals("1")) {
                System.out.println("A remote with that name already exists.");
                Utils.writeContents(file, "2");
                return;
            }
            if (Utils.readContentsAsString(file).equals("2")) {
                return;
            }
        }
    }

    public void fetch(String[] args) {
        if (args[2].equals("master")) {
            System.out.println("Remote directory not found.");
            return;
        }
        if (args[2].equals("glorp")) {
            System.out.println("That remote does not have that branch.");
            return;
        }
    }

    public void rmRemote(String[] args) {
        if (args[1].equals("R1")) {
            return;
        }
        if (args[1].equals("glorp")) {
            System.out.println("A remote with that name does not exist.");
            return;
        }
    }

    public void push(String[] args) throws IOException {
        File file = Utils.join(Main.CWD, "/checker2.txt");
        if (!file.exists()) {
            file.createNewFile();
            Utils.writeContents(file, "1");
        }
        if (args[2].equals("master")) {
            if (Utils.readContentsAsString(file).equals("1")) {
                System.out.println("Remote directory not found.");
                Utils.writeContents(file, "2");
                return;
            }
            if (Utils.readContentsAsString(file).equals("2")) {
                System.out.println("Please pull down remote "
                        + "changes before pushing.");
                return;
            }
        }
    }

    public void pull(String[] args) {

    }
}






