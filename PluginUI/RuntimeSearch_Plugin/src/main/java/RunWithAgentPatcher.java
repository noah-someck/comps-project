import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.roots.impl.storage.ClassPathStorageUtil;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.rt.coverage.util.classFinder.ClassPathEntry;
import com.intellij.util.PathsList;
import com.intellij.util.lang.ClasspathCache;
import javassist.ClassPath;
import junit.runner.ClassPathTestCollector;
import org.jf.dexlib2.analysis.ClassPathResolver;

import java.io.IOException;
import java.util.jar.JarFile;

public class RunWithAgentPatcher extends JavaProgramPatcher {
    private static final String PATH_TO_AGENT_JAR = "C:\\Users\\T\\aseComps1\\PluginUI\\RuntimeSearch_Plugin\\src\\main\\resources\\agents\\InstrumentationTest-agent.jar";
//"C:\\Users\\T\\aseComps1\\PluginUI\\RuntimeSearch_Plugin\\src\\main\\resources\\agents\\InstrumentationTest-agent.jar";

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        if (!executor.getId().equals("RUNTIME_SEARCH_EXECUTOR")) return;

        ParametersList vmParametersList = javaParameters.getVMParametersList();
        vmParametersList.add( "-javaagent:" + PATH_TO_AGENT_JAR);


    }

}
