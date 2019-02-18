package edu.carleton.cs.ASEcomps;

import com.intellij.openapi.project.Project;

public class ProjectHolder {

    private static ProjectHolder projectHolder;
    private Project project;

    private ProjectHolder(Project project) {
        this.project = project;
    }

    public static ProjectHolder getInstance() {
        return projectHolder;
    }

    public static void createProjectHolder(Project project) {
        if (projectHolder == null) {
            projectHolder = new ProjectHolder(project);
        }
        projectHolder.setProject(project);
    }

    public Project getProject() {
        return project;
    }

    private void setProject(Project project) {
        this.project = project;
    }

}
