package edu.carleton.cs.ASEcomps;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;

public class RunWithAgentPatcher extends JavaProgramPatcher {

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        if (!executor.getId().equals("RUNTIME_SEARCH_EXECUTOR")) return;

        URI uri = null;
        try {
            uri = new URI(getClass().getClassLoader().getResource("agents/InstrumentationTest-agent.jar").getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String PATH_TO_AGENT_JAR = uri.getPath();

        try {
            unzip(PATH_TO_AGENT_JAR.replace("!/agents/InstrumentationTest-agent.jar", ""),
                    PATH_TO_AGENT_JAR.replace("RuntimeSearch_Plugin-1.0-SNAPSHOT.jar!/agents/InstrumentationTest-agent.jar", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PATH_TO_AGENT_JAR = PATH_TO_AGENT_JAR.replace("RuntimeSearch_Plugin-1.0-SNAPSHOT.jar!/", "");
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        vmParametersList.add( "-javaagent:" + PATH_TO_AGENT_JAR);
    }

    private void unzip(String jarFile, String destDir) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = new File(destDir + File.separator + file.getName());
            if (!file.getName().equals("agents/") && !file.getName().equals("agents/InstrumentationTest-agent.jar")) {
                continue;
            }
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(file); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        jar.close();
    }

}
