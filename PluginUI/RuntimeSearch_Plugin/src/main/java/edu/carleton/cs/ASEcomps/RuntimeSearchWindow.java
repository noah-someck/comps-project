package edu.carleton.cs.ASEcomps;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.xdebugger.breakpoints.XBreakpoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;


public class RuntimeSearchWindow implements ToolWindowFactory {

    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;
    private static ComboBox comboBox;
    private static JTextField searchBar;
    private static JButton button;

    private static boolean firstClick = true;
    private static ClientThread currentClient;


    // TODO only allow RuntimeSearch to run once at a time
    // TODO tool window not opening every time
    // TODO extra click in order to finish program

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ProjectHolder.createProjectHolder(project);

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
        String[] choices = { "string","object","variable","regex"};

        JLabel label = new JLabel("Search for: ");
        searchBar = new JTextField(20);
        comboBox = new ComboBox(choices);
        button = new JButton("Find Next");

        myToolWindowContent.add(label);
        myToolWindowContent.add(comboBox);
        myToolWindowContent.add(searchBar);
        myToolWindowContent.add(button);

        searchBar.setEnabled(false);
        comboBox.setEnabled(false);
        button.setEnabled(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String s = searchBar.getText().trim();
                if (!s.equals("")) {
                    searchBar.setText(s);
                    System.out.println(comboBox.getSelectedItem().toString());

                    if (firstClick) {
                        searchBar.setEnabled(false);
                        comboBox.setEnabled(false);
                        setSearchString(s, getSearchType(comboBox.getSelectedItem().toString()));
                    }
                    else if (currentClient != null) {
                        currentClient.reset();
                    }
                }
            }

        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });

        Content content = contentFactory.createContent(myToolWindowContent, "Test Tool Action", false);
        //System.out.println(myToolWindowContent);
        toolWindow.getContentManager().addContent(content);

    }

    private RmiServerIntf.SEARCH_TYPE getSearchType(String searchType) {
        switch (searchType) {
            case "string":
                return RmiServerIntf.SEARCH_TYPE.STRING;
            case "object":
                return RmiServerIntf.SEARCH_TYPE.OBJECT;
            case "variable":
                return RmiServerIntf.SEARCH_TYPE.VARIABLE;
            case "regex":
                return RmiServerIntf.SEARCH_TYPE.REGEX;
        }
        return RmiServerIntf.SEARCH_TYPE.STRING;
    }

    public static void showInput() {
        searchBar.setEnabled(true);
        comboBox.setEnabled(true);
        button.setEnabled(true);
    }

    public static void endClient() {
        currentClient = null;
        searchBar.setEnabled(false);
        comboBox.setEnabled(false);
        button.setEnabled(false);
        RuntimeSearchProgramRunner.setClientNotRunning();
    }

    private void setSearchString(String searchString, RmiServerIntf.SEARCH_TYPE searchType) {
        boolean searchStringSent = false;
        while (!searchStringSent) {
            try {
                RmiClient.setSearchString(searchString, searchType);
                searchStringSent = true;
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        firstClick = false;
    }

    public static void removeBreakpoint(String packagePath, int lineNumber) {
        Project project = ProjectHolder.getInstance().getProject();
        String file = makeFilePath(project, packagePath);
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

    public static void addBreakpoint(String packagePath, int lineNumber) {
        Project project = ProjectHolder.getInstance().getProject();
        String file = makeFilePath(project, packagePath);
        DebuggerManagerEx.getInstanceEx(project).getBreakpointManager()
                .addLineBreakpoint(FileDocumentManager.getInstance()
                                .getDocument(LocalFileSystem.getInstance()
                                        .findFileByIoFile(new File(file))),
                        lineNumber);
    }

    private static String makeFilePath(Project project, String packagePath){
        String filePath = ModuleRootManager.getInstance(ModuleManager.getInstance(project).getModules()[0]).getContentRoots()[0].toString();
        filePath = filePath.substring(7);
        // TODO src and what is going on here???
        String path = filePath + "/src/" + packagePath;
        return path;
    }

    public static void setFirstClick() {
        firstClick = true;
    }

    public static void createClient() {
        currentClient = new ClientThread();
        currentClient.start();
    }
}