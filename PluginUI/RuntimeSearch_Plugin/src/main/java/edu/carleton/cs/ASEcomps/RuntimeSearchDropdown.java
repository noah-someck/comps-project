package edu.carleton.cs.ASEcomps;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;

public class RuntimeSearchDropdown extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null)
        {
            return;
        }
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow("Test Tool Window");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        window.show(runnable);
    }
}
