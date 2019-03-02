package edu.carleton.cs.ASEcomps;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeSearchProgramRunner extends GenericDebuggerRunner {

    private static boolean clientRunning = false;

    @NotNull
    @Override
    public String getRunnerId() {
        return "RUNTIME_SEARCH_PROGRAM_RUNNER";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals("RUNTIME_SEARCH_EXECUTOR") && super.canRun(DefaultDebugExecutor.EXECUTOR_ID, profile) && !clientRunning;
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback) throws ExecutionException {
        callback = new Callback() {
            @Override
            public void processStarted(RunContentDescriptor descriptor) {
                clientRunning = true;
                RuntimeSearchWindow.createClient();
            }
        };
        super.execute(environment, callback);
        RuntimeSearchWindow.setFirstClick();
        ToolWindowManager manager = ToolWindowManager.getInstance(environment.getProject());
        ToolWindow window = manager.getToolWindow("Test Tool Window");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        window.show(runnable);
    }

    public static void setClientNotRunning() {
        clientRunning = false;
    }
}
