package org.fitnesse.plugins;

//todo: use "git commit --amend if the new commit is less than 10 minutes later
//todo: include the username of the authenticated user to the commit message

public class GitScm {

  private static final GitDelegate gitDelegate = new GitDelegate();

  public static void cmUpdate(String file, String payload) throws Exception {
    gitDelegate.update(file);
    gitDelegate.commit();
  }

  public static void cmEdit(String file, String payload) throws Exception {
  }

  public static void cmDelete(String file, String payload) throws Exception {
    gitDelegate.delete(file);
    gitDelegate.commit();
  }

  public static void cmPreDelete(String file, String payload) throws Exception {
  }

}
