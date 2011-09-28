package org.fitnesse.plugins;

import fitnesse.ComponentFactory;
import fitnesse.components.CommandRunner;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;


public class GitDelegate {

    private String MACHINE_NAME = null;
    protected String COMMIT_TOKEN = null;
    private CommandExecutor executor = new CommandExecutor();

    public void update(String file) throws Exception {
        executor.exec("git add " + file);
    }

    public void delete(String file) throws Exception {
        executor.exec(gitPath() + " rm -rf --cached " + file);
    }

    public void commit() throws Exception {
        if (shouldAmend()) {
            executor.exec(gitPath() + " commit --amend --message \"" + commitMessage() + "\"");
        }
        else {
            COMMIT_TOKEN = freshToken();
            executor.exec(gitPath() + " commit --message \"" + commitMessage() + "\"");
        }
    }

    private boolean shouldAmend() throws Exception {
        CommandRunner runner = executor.exec(gitPath() + " log -1 --pretty=oneline");
        runner.run();
        return runner.getOutput().contains(commitMessage());
    }

    protected String commitMessage() {
        try {
            if (MACHINE_NAME == null) MACHINE_NAME = InetAddress.getLocalHost().getHostName();
            if (COMMIT_TOKEN == null) COMMIT_TOKEN = freshToken();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "FitNesse auto-commit from " + MACHINE_NAME + " with token [" + COMMIT_TOKEN + "]";
    }

    private String freshToken() {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date());
    }

    protected String gitPath() {
        try {
            FileInputStream inputStream = new FileInputStream(ComponentFactory.PROPERTIES_FILE);
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            String gitPath = properties.getProperty("git.path");
            return gitPath != null ? gitPath : "/usr/local/bin/git";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
