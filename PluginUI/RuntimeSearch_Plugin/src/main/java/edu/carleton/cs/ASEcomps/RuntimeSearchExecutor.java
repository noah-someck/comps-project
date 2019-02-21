package edu.carleton.cs.ASEcomps;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.ui.UIBundle;
import com.intellij.xdebugger.XDebuggerBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RuntimeSearchExecutor extends Executor {

    @NonNls public static final String EXECUTOR_ID = "RUNTIME_SEARCH_EXECUTOR";
//    private final String myStartActionText = XDebuggerBundle.message("debugger.runner.start.action.text");
    private final String myStartActionText = "Debug with Runtime Search";
    private final String myDescription = XDebuggerBundle.message("string.debugger.runner.description");

    @Override
    public String getToolWindowId() {
//        Executor[] list = ExecutorRegistry.getInstance().getRegisteredExecutors();
//        for (int i = 0; i < list.length; i++) {
//            System.out.println(list[i].getId());
//        }
//        return "Test Tool Window";
        return ToolWindowId.DEBUG;
    }

    @Override
    public Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowDebugger;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("images/RSlogo2.png");
    }

    @Override
    public Icon getDisabledIcon() {
        return IconLoader.getDisabledIcon(getIcon());
    }

    @Override
    public String getDescription() {
        return myDescription;
    }

    @NotNull
    @Override
    public String getActionName() {
        return UIBundle.message("tool.window.name.debug");
    }

    @NotNull
    @Override
    public String getId() {
        return EXECUTOR_ID;
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return myStartActionText;
    }

    @Override
    public String getContextActionId() {
        return "DebugClass";
    }

    @Override
    public String getHelpId() {
        return "debugging.DebugWindow";
    }
}