package org.fitnesse.plugins;

import fitnesse.components.CommandRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;


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

    @Test
    public void commitMessage_forFirstCommit() throws Exception {
        GitDelegate gd = new GitDelegate();

        Date beginDate = new Date();
        Thread.sleep(1500L);
        String firstCommitMessage = gd.commitMessage();
        Thread.sleep(1500L);
        Date endDate = new Date();

        assertTrue(firstCommitMessage.contains("FitNesse auto-commit"));
        String commitToken = firstCommitMessage.substring(firstCommitMessage.indexOf("[") + 1, firstCommitMessage.indexOf("]"));
        Date commitTokenDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).parse(commitToken);

        assertTrue(commitTokenDate.before(endDate));

        assertTrue("token date " + commitTokenDate + "is not after" + endDate, commitTokenDate.after(beginDate));
        assertTrue("token date " + commitTokenDate + "is not before" + endDate, commitTokenDate.before(endDate));

        String expectedMessage =
                "FitNesse auto-commit from " + InetAddress.getLocalHost().getHostName() + " with token [" + commitToken + "]";
        assertEquals(expectedMessage, firstCommitMessage);
    }

    @Test
    public void commitMessage_forAmend() throws Exception {
        GitDelegate gd = new GitDelegate();

        String firstCommitMessage = gd.commitMessage();
        String amendMessage = gd.commitMessage();

        assertTrue(firstCommitMessage.contains("FitNesse auto-commit"));
        assertEquals(firstCommitMessage, amendMessage);
    }

}
