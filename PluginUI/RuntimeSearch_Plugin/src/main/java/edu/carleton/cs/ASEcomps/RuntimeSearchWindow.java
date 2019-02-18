package edu.carleton.cs.ASEcomps;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModuleRootManager;
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

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;
    private static boolean INITIALIZED = false;
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
        JLabel label = new JLabel("Search for a string:");
        JTextField searchBar = new JTextField(20);
        myToolWindowContent.add(label);
        myToolWindowContent.add(searchBar);

        JButton myButton = new JButton("Find Next");
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String s = searchBar.getText().trim();
                if (!s.equals("")) {
                    searchBar.setText(s);
                    passInputString(s, project);

                    if (firstClick) {
                        setSearchString(s);
                        currentClient = new ClientThread();
                        currentClient.start();
                    }
                    else if (currentClient != null) {
                        currentClient.reset();
                    }
                }
            }

        });
        myToolWindowContent.add(myButton);

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    myButton.doClick();
                }
            }
        });

        Content content = contentFactory.createContent(myToolWindowContent, "Test Tool Action", false);
        //System.out.println(myToolWindowContent);
        toolWindow.getContentManager().addContent(content);

    }

    public static void endClient() {
        currentClient = null;
    }

    private void setSearchString(String s) {
        boolean searchStringSent = false;
        while (!searchStringSent) {
            try {
                RmiClient.setSearchString(s);
                searchStringSent = true;
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        firstClick = false;
    }

    private void passInputString(String searchQuery, Project project) {
        if (INITIALIZED == false){
            //initialize backup startup thingy
            INITIALIZED = true;
        }
        //System.out.println(ModuleRootManager.getInstance(ModuleManager.getInstance(project).getModules()[0]).getContentRoots()[0]);
//        removeBreakpoint(project);
        //TODO: call their singleton to update edu.carleton.cs.ASEcomps.BreakpointDataHolder with package path??
//        addBreakpoint(project);
    }

    public static void removeBreakpoint(String packagePath, int lineNumber) {
        Project project = ProjectHolder.getInstance().getProject();
//        int lineNumber = BreakpointDataHolder.getInstance().getLineNumber();
        //String file = edu.carleton.cs.ASEcomps.BreakpointDataHolder.getInstance().getFile();
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
//        int lineNumber = BreakpointDataHolder.getInstance().getLineNumber();
        //String file = edu.carleton.cs.ASEcomps.BreakpointDataHolder.getInstance().getFile();
        String file = makeFilePath(project, packagePath);
        DebuggerManagerEx.getInstanceEx(project).getBreakpointManager()
                .addLineBreakpoint(FileDocumentManager.getInstance()
                                .getDocument(LocalFileSystem.getInstance()
                                        .findFileByIoFile(new File(file))),
                        lineNumber);
    }

    private static String makeFilePath(Project project, String packagePath){
        String filePath = ModuleRootManager.getInstance(ModuleManager.getInstance(project).getModules()[0]).getContentRoots()[0].toString();
        String os = getOS();
        if (os.equals("windows")){
            filePath = filePath.substring(7);
        }
        if (os.equals("mac")){
            filePath = filePath.substring(7);
        }
        if (os.equals("linux")){
            filePath = filePath.substring(7);
        }
        // TODO src and what is going on here???
        String path = filePath + "/src/" + packagePath;
        return path;
    }

    private static String getOS(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")){
            os = "windows";
        }
        if (os.startsWith("mac") || os.startsWith("darwin")){
            os = "mac";
        }
        if (os.contains("nux")){
            os = "linux";
        }
        return os;
    }

    public static void setFirstClick() {
        firstClick = true;
    }
}