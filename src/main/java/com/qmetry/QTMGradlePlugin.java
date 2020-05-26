package com.qmetry;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class QTMGradlePlugin implements Plugin<Project> 
{
    @Override
    public void apply(Project project) 
	{
        project.getExtensions().create("qmetryConfig", QTMGradleExtension.class);
        project.getTasks().create("publishResults", PublishResults.class);
    }
}