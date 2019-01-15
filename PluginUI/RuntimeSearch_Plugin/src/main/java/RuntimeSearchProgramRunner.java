import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeSearchProgramRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "RUNTIME_SEARCH_PROGRAM_RUNNER";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (executorId.equals("RUNTIME_SEARCH_EXECUTOR")) {
            return true;
        }
        return false;
    }
}
