package com.cs.harin.transition;

import com.cs.harin.transition.scanner.CodeScanner;
import com.cs.harin.transition.scanner.ScanResult;
import com.cs.harin.transition.writer.ScanResultWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Maven plugin goal to scan Java code in a package and write analysis to /target/scan
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ScanCodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Base package name to scan (e.g., "com.cs.harin.rest")
     * Configure in pom.xml <configuration><basePackage>...</basePackage></configuration>
     * Or override via command line with -DbasePackage=...
     */
    @Parameter(property = "basePackage", required = false)
    private String basePackage;

    /**
     * Source directory to scan
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true)
    private File sourceDirectory;

    /**
     * Output directory for scan results
     */
    @Parameter(defaultValue = "${project.build.directory}/scan", readonly = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting code scan for package: " + basePackage);
        getLog().info("Source directory: " + sourceDirectory.getAbsolutePath());
        getLog().info("Output directory: " + outputDirectory.getAbsolutePath());

        if (!sourceDirectory.exists()) {
            throw new MojoExecutionException("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
        }

        if (basePackage == null || basePackage.trim().isEmpty()) {
            throw new MojoExecutionException(
                "Base package name must be specified in pom.xml <configuration><basePackage>...</basePackage></configuration> " +
                "or via command line with -DbasePackage=<package.name>"
            );
        }

        try {
            // Create output directory if it doesn't exist
            if (!outputDirectory.exists()) {
                boolean created = outputDirectory.mkdirs();
                if (created) {
                    getLog().info("Created output directory: " + outputDirectory.getAbsolutePath());
                }
            }

            // Convert package name to directory path
            Path packagePath = Paths.get(sourceDirectory.getAbsolutePath(),
                    basePackage.replace('.', File.separatorChar));

            if (!packagePath.toFile().exists()) {
                throw new MojoExecutionException("Package directory does not exist: " + packagePath);
            }

            getLog().info("Scanning package directory: " + packagePath);

            // Scan the code
            CodeScanner scanner = new CodeScanner(getLog());
            ScanResult scanResult = scanner.scan(packagePath, basePackage);

            getLog().info("Scan completed. Found:");
            getLog().info("  - " + scanResult.getClasses().size() + " classes");
            getLog().info("  - " + scanResult.getControllers().size() + " controllers");
            getLog().info("  - " + scanResult.getEndpoints().size() + " endpoints");
            getLog().info("  - " + scanResult.getEntities().size() + " entities");
            getLog().info("  - " + scanResult.getServices().size() + " services");
            getLog().info("  - " + scanResult.getRepositories().size() + " repositories");

            // Write results to output directory
            ScanResultWriter writer = new ScanResultWriter(getLog());
            writer.writeResults(scanResult, outputDirectory);

            getLog().info("Scan results written to: " + outputDirectory.getAbsolutePath());

        } catch (Exception e) {
            getLog().error("Error during code scanning", e);
            throw new MojoExecutionException("Failed to scan code", e);
        }
    }
}
