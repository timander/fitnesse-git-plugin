package org.fitnesse.plugins;

import fitnesse.ComponentFactory;
import fitnesse.components.CommandRunner;

import java.io.FileInputStream;
import java.util.Properties;


//todo: use "git commit --amend if the new commit is less than 10 minutes later
//todo: include the username of the authenticated user to the commit message

public class GitScm {
    public static void cmUpdate(String file, String payload) throws Exception {
        execute("cmUpdate", gitPath() + " add " + file);
        commit();
    }

    public static void cmEdit(String file, String payload) throws Exception {
    }

    public static void cmDelete(String file, String payload) throws Exception {
        execute("cmDelete", gitPath() + " rm -rf --cached " + file);
        commit();
    }

    public static void cmPreDelete(String file, String payload) throws Exception {
    }

    private static void commit() throws Exception {
        execute("commit", gitPath() + " commit --message \"FitNesse Commit\"");
    }

    private static void execute(String method, String command) throws Exception {
        CommandRunner runner = new CommandRunner(command, "");
        runner.run();
        if (runner.getOutput().length() + runner.getError().length() > 0) {
            System.err.println(method + " command: " + command);
            System.err.println(method + " exit code: " + runner.getExitCode());
            System.err.println(method + " out:" + runner.getOutput());
            System.err.println(method + " err:" + runner.getError());
        }
    }

    private static String gitPath() throws Exception {
        FileInputStream inputStream = new FileInputStream(ComponentFactory.PROPERTIES_FILE);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        String gitPath = properties.getProperty("git.path");
        return gitPath != null ? gitPath : "/usr/local/bin/git";
    }

}
