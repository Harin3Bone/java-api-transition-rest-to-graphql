package com.cs.harin.transition.mojo;

import com.cs.harin.transition.model.rest.EndpointInfo;
import com.cs.harin.transition.model.scan.ScanResult;
import com.cs.harin.transition.service.GraphQLService;
import com.cs.harin.transition.service.ScannerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.cs.harin.transition.constant.ErrorMessage.UNEXPECTED_EXCEPTION;

@Slf4j
@Mojo(name = "transition", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class TransitionMojo extends BaseMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("Starting REST to GraphQL transition for package: {}", basePackage);
        preValidation();

        try {
            // Create output directory if it doesn't exist
            createOutputDirectory();

            // Convert package name to directory path
            Path packagePath = Paths.get(sourceDirectory.getAbsolutePath(),
                    basePackage.replace('.', File.separatorChar));

            if (!packagePath.toFile().exists()) {
                throw new MojoExecutionException("Package directory does not exist: " + packagePath);
            }

            log.info("Scanning package directory: {}", packagePath);

            // Scan the code - focusing on Controllers only
            ScannerService scannerService = new ScannerService();
            ScanResult scanResult = scannerService.scan(packagePath, basePackage);

            log.info("Scan completed. Found {} controllers with {} endpoints",
                    scanResult.getControllers().size(),
                    scanResult.getEndpoints().size());

            // Filter GET endpoints only
            List<EndpointInfo> getEndpoints = scanResult.getEndpoints().stream()
                    .filter(endpoint -> "GET".equalsIgnoreCase(endpoint.getHttpMethod()))
                    .toList();

            log.info("Found {} GET endpoints to convert to GraphQL queries", getEndpoints.size());

            if (getEndpoints.isEmpty()) {
                log.warn("No GET endpoints found in controllers. Skipping GraphQL generation.");
                return;
            }

            // Generate GraphQL Query classes
            GraphQLService generator = new GraphQLService();
            generator.generateQueryClass(getEndpoints, basePackage, outputDirectory);

            log.info("GraphQL transition completed successfully!");
            log.info("Generated files written to: {}", outputDirectory.getAbsolutePath());

        } catch (Exception e) {
            log.error("Error during transitioning", e);
            throw new MojoExecutionException(UNEXPECTED_EXCEPTION, e);
        }
    }

    @Override
    protected void getMojoDirectory() {
        this.outputDirectory = new File(sourceDirectory.getAbsolutePath()
                .concat(File.separator)
                .concat(basePackage.replace('.', File.separatorChar))
                .concat(File.separator)
                .concat("graphql")
        );
    }

}
