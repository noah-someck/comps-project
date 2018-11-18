import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;


public class RuntimeSearchWindow implements ToolWindowFactory {

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;
    private static boolean INITIALIZED = false;

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        toolWindow.hide(runnable);
        myToolWindowContent = new JPanel();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        myToolWindowContent.setLayout(new GridBagLayout());
        JLabel label = new JLabel("Search for a string:");
        JTextField searchBar = new JTextField(20);
        myToolWindowContent.add(label);
        myToolWindowContent.add(searchBar);

        JButton myButton = new JButton("Find Next");
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println(DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().getBreakpoints().size());
                String s = searchBar.getText().trim();
                if (!s.equals("")) {
                    searchBar.setText(s);
                    passInputString(s, project);
                    System.out.println(s);
                }
            }

        });
        myToolWindowContent.add(myButton);

        Content content = contentFactory.createContent(myToolWindowContent, "Test Tool Action", false);
        System.out.println(myToolWindowContent);
        toolWindow.getContentManager().addContent(content);

    }

    private void passInputString(String searchQuery, Project project) {
        if (INITIALIZED == false){
            //initialize backup startup thingy
        }
        removeBreakpoint(project);
        //TODO: call their singleton to update BreakpointDataHolder??
        addBreakpoint(project);
    }

    private void removeBreakpoint(Project project) {
        int lineNumber = BreakpointDataHolder.getInstance().getLineNumber();
        String file = BreakpointDataHolder.getInstance().getFile();
        List<Breakpoint> breakpoints = DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().getBreakpoints();
        for (int i = 0; i < breakpoints.size(); i++) {
            XBreakpoint breakpoint = breakpoints.get(i).getXBreakpoint();
            if (!breakpoint.isEnabled()) {
                continue;
            }
            int bpLineNumber = breakpoint.getSourcePosition().getLine();
            String bpFile = breakpoint.getSourcePosition().getFile().getPath();
            if (bpLineNumber == lineNumber && bpFile.equals(file)) {
                DebuggerManagerEx.getInstanceEx(project).getBreakpointManager()
                        .removeBreakpoint(DebuggerManagerEx.getInstanceEx(project)
                                .getBreakpointManager().getBreakpoints().get(i));
                break;
            }
        }
    }

    private void addBreakpoint(Project project) {
        int lineNumber = BreakpointDataHolder.getInstance().getLineNumber();
        String file = BreakpointDataHolder.getInstance().getFile();
        DebuggerManagerEx.getInstanceEx(project).getBreakpointManager()
                .addLineBreakpoint(FileDocumentManager.getInstance()
                                .getDocument(LocalFileSystem.getInstance()
                                        .findFileByIoFile(new File(file))),
                        lineNumber);
    }
}