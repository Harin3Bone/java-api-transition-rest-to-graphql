package com.cs.harin.transition.mojo;

import com.cs.harin.transition.model.scan.ScanResult;
import com.cs.harin.transition.service.ScannerService;
import com.cs.harin.transition.service.WriterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Maven plugin goal to scan Java code in a package and write reports
 */
@Slf4j
@Mojo(name = "scan", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ScanCodeMojo extends BaseMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("Starting code scan for package: {}", basePackage);
        preValidation();

        try {
            // Create output directory if it doesn't exist
            createOutputDirectory();

            // Convert package name to directory path
            Path packagePath = Paths.get(
                    sourceDirectory.getAbsolutePath(),
                    basePackage.replace('.', File.separatorChar)
            );

            if (!packagePath.toFile().exists()) {
                throw new MojoExecutionException("Package directory does not exist: " + packagePath);
            }

            log.info("Scanning package directory: {} ", packagePath);

            // Scan the code
            ScannerService scannerService = new ScannerService();
            ScanResult scanResult = scannerService.scan(packagePath, basePackage);

            log.info("Scan completed. Found:");
            log.info("  - {} classes", scanResult.getClasses().size());
            log.info("  - {} controllers", scanResult.getControllers().size());
            log.info("  - {} endpoints", scanResult.getEndpoints().size());
            log.info("  - {} entities", scanResult.getEntities().size());
            log.info("  - {} services", scanResult.getServices().size());
            log.info("  - {} repositories", scanResult.getRepositories().size());

            // Write results to output directory
            WriterService writer = new WriterService();
            writer.writeResults(scanResult, outputDirectory);

            log.info("Scan results written to: {}", outputDirectory.getAbsolutePath());

        } catch (Exception e) {
            log.error("Error during code scanning", e);
            throw new MojoExecutionException("Failed to scan code", e);
        }
    }

    @Override
    protected void getMojoDirectory() {
        this.outputDirectory = new File(outputDirectory.getAbsolutePath().concat("/scan"));
    }
}
