package org.fitnesse.plugins;

import fitnesse.ComponentFactory;
import fitnesse.components.CommandRunner;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

public class GitDelegate {

  private static String MACHINE_NAME = null;
  private static String COMMIT_TIMESTAMP = null;
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
    } else {
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
      if (COMMIT_TIMESTAMP == null) COMMIT_TIMESTAMP = new Date().toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return "FitNesse auto-commit from " + MACHINE_NAME + " [" + COMMIT_TIMESTAMP + "]";
  }

  protected String gitPath() {
    try {
      FileInputStream inputStream = new FileInputStream(ComponentFactory.PROPERTIES_FILE);
      Properties properties = new Properties();
      properties.load(inputStream);
      inputStream.close();
      String gitPath = properties.getProperty("git.path");
      return gitPath != null ? gitPath : "/usr/local/bin/git";
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
