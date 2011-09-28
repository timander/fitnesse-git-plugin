package org.fitnesse.plugins;

import fitnesse.components.CommandRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitDelegateTest {

  @Mock
  private CommandExecutor executor;

  @InjectMocks
  private GitDelegate gitDelegate = new GitDelegate() {
    @Override
    protected String gitPath() {
      return "git";
    }

    @Override
    protected String commitMessage() {
      return "fake message";
    }
  };

  @Test
  public void update() throws Exception {
    gitDelegate.update("content.txt");
    verify(executor).exec("git add content.txt");
  }

  @Test
  public void commit() throws Exception {
    CommandRunner mockCommandRunner = Mockito.mock(CommandRunner.class);
    when(mockCommandRunner.getOutput()).thenReturn("something else");
    Mockito.when(executor.exec("git log -1 --pretty=oneline")).thenReturn(mockCommandRunner);
    gitDelegate.commit();
    verify(executor).exec("git log -1 --pretty=oneline");
    verify(executor).exec("git commit --message \"fake message\"");
  }

  @Test
  public void amend() throws Exception {
    CommandRunner mockCommandRunner = Mockito.mock(CommandRunner.class);
    when(mockCommandRunner.getOutput()).thenReturn("fake message");
    Mockito.when(executor.exec("git log -1 --pretty=oneline")).thenReturn(mockCommandRunner);
    gitDelegate.commit();
    verify(executor).exec("git log -1 --pretty=oneline");
    verify(executor).exec("git commit --amend --message \"fake message\"");
  }

  @Test
  public void delete() throws Exception {
    gitDelegate.delete("properties.xml");
    verify(executor).exec("git rm -rf --cached properties.xml");
  }

}
