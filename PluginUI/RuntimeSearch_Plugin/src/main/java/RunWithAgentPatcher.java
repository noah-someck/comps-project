import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;

public class RunWithAgentPatcher extends JavaProgramPatcher {
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        if (executor.getId().equals("RUNTIME_SEARCH_EXECUTOR")) {
            vmParametersList.addAt(0, "-javaagent:C:\\Users\\Joshua\\Desktop\\CS\\comps\\InstrumentationTest\\out\\artifacts\\InstrumentationTest_jar\\InstrumentationTest.jar");
        }
        System.out.println(vmParametersList.getParametersString());

    }
}
