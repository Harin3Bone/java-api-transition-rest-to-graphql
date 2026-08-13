package com.cs.harin.transition.mojo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static com.cs.harin.transition.constant.ErrorMessage.BASE_PACKAGE_NOT_SPECIFIED;

@Slf4j
public abstract class BaseMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "basePackage", required = true)
    protected String basePackage;

    /**
     * Source directory to scan
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
    protected File sourceDirectory;

    /**
     * Output directory for results
     */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    protected File outputDirectory;

    protected void preValidation() throws MojoExecutionException {
        log.info("Source directory: {}", sourceDirectory.getAbsolutePath());
        log.info("Output directory: {}", outputDirectory.getAbsolutePath());

        if (!sourceDirectory.exists()) {
            throw new MojoExecutionException("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
        }

        if (StringUtils.isBlank(basePackage)) {
            throw new MojoExecutionException(BASE_PACKAGE_NOT_SPECIFIED);
        }
    }

    protected void createOutputDirectory() {
        getMojoDirectory();
        log.info("Mojo directory: {}", outputDirectory.getAbsolutePath());
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (created) {
                log.info("Created output directory: {}", outputDirectory.getAbsolutePath());
            }
        }
    }

    protected abstract void getMojoDirectory();
}
