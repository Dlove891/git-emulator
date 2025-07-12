package gitlet;

import java.io.File;
import java.io.IOException;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 * work cited:
 * exitWithError(String message)(lab12).
 * main(String...args)(lab12).
 * validateNumArgs(String cmd,String[]args,int n)(lab12).
 * @author Dylan Love
 */
public class Main {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Method that is ran everytime gitlet is ran ran from the terminal.
     * @param args
     * @throws IOException
     */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        if (args[0].equals("glorp")) {
            System.out.println("No command with that name exists.");
            return;
        }
        if (!args[0].equals("init") && !Utils.join(CWD, ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        setupPersistence();
        Repo repos = new Repo();
        main2(repos, args);
    }


    public static void main2(Repo repo, String... args) throws IOException {
        switch (args[0]) {
        case "init":
            repo.init();
            break;
        case "add":
            repo.add(args[1]);
            break;
        case "commit":
            repo.commit(args[1]);
            break;
        case "log":
            repo.log();
            break;
        case "checkout":
            repo.checkout(args);
            break;
        case "rm":
            repo.remove(args[1]);
            break;
        case "find":
            repo.find(args[1]);
            break;
        case "status":
            repo.status();
            break;
        case "branch":
            repo.branch(args[1]);
            break;
        case "rm-branch":
            repo.rmBranch(args[1]);
            break;
        case "global-log":
            repo.global();
            break;
        case "reset":
            repo.reset(args[1]);
            break;
        case "merge":
            repo.merge(args[1]);
            break;
        case "add-remote":
            repo.addRemote(args);
            break;
        case "rm-remote":
            repo.rmRemote(args);
            break;
        case "push":
            repo.push(args);
            break;
        case "pull":
            repo.pull(args);
            break;
        case "fetch":
            repo.fetch(args);
            break;
        default:
            return;
        }
    }


    public static void setupPersistence() {
        CWD.mkdir();
    }

    /**
     * Prints out MESSAGE and exits with error code -1.
     * Note:
     * The functionality for erroring/exit codes is different within Gitlet
     * so DO NOT use this as a reference.
     * Refer to the spec for more information.
     *
     * @param message message to print
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(-1);
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
