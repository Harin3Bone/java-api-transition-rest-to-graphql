package com.cs.harin.transition.writer;

import com.cs.harin.transition.model.*;
import com.cs.harin.transition.scanner.ScanResult;
import com.cs.harin.transition.util.DateFormatUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Writes scan results to various output formats in the /target/scan directory
 */
public class ScanResultWriter {

    private static final String SEPARATOR_LINE = "=".repeat(80) + "\n";
    private static final String DASH_LINE = "-".repeat(80) + "\n";
    private static final String NEWLINE = "\n";
    private static final String INDENT = "  - ";
    private static final String SPACE = " ";
    private static final String COMMA_SEPARATOR = ", ";
    private static final String BRACKET_OPEN = " [";
    private static final String BRACKET_CLOSE = "]";
    
    private static final String LABEL_CLASS = "Class: ";
    private static final String LABEL_TYPE = "Type: ";
    private static final String LABEL_FILE = "File: ";
    private static final String LABEL_ANNOTATIONS = "Annotations: ";
    private static final String LABEL_METHODS = "Methods: ";
    
    private static final String WRITTEN_REPORT_TO = "Written %s report to: %s";

    private final Log log;
    private final ObjectMapper objectMapper;
    
    public ScanResultWriter(Log log) {
        this.log = log;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    public void writeResults(ScanResult scanResult, File outputDirectory) throws IOException {
        // Write JSON summary
        writeJsonSummary(scanResult, outputDirectory);
        
        // Write detailed reports
        writeClassesReport(scanResult, outputDirectory);
        writeControllersReport(scanResult, outputDirectory);
        writeEndpointsReport(scanResult, outputDirectory);
        writeEntitiesReport(scanResult, outputDirectory);
        writeServicesReport(scanResult, outputDirectory);
        writeRepositoriesReport(scanResult, outputDirectory);
        
        log.info("All scan results written successfully");
    }
    
    private void writeJsonSummary(ScanResult scanResult, File outputDirectory) throws IOException {
        File jsonFile = new File(outputDirectory, "scan-result.json");
        objectMapper.writeValue(jsonFile, scanResult);
        log.info("Written JSON summary to: " + jsonFile.getName());
    }
    
    private void writeClassesReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "classes-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("CLASSES SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Package: " + scanResult.getPackageName() + NEWLINE);
            writer.write("Scan Path: " + scanResult.getScanPath() + NEWLINE);
            writer.write("Scan Time: " + DateFormatUtil.formatTimestamp(scanResult.getScanTimestamp()) + NEWLINE);
            writer.write("Total Classes: " + scanResult.getClasses().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (ClassInfo classInfo : scanResult.getClasses()) {
                writer.write(DASH_LINE);
                writer.write(LABEL_CLASS + classInfo.getFullClassName() + NEWLINE);
                writer.write(LABEL_TYPE + classInfo.getClassType() + NEWLINE);
                writer.write(LABEL_FILE + classInfo.getFilePath() + NEWLINE);
                writer.write(LABEL_ANNOTATIONS + String.join(COMMA_SEPARATOR, classInfo.getAnnotations()) + NEWLINE);
                writer.write("\nFields (" + classInfo.getFields().size() + "):\n");
                for (FieldInfo field : classInfo.getFields()) {
                    writer.write(INDENT + field.getAccessModifier() + SPACE + field.getType() + SPACE + field.getName());
                    if (!field.getAnnotations().isEmpty()) {
                        writer.write(BRACKET_OPEN + String.join(COMMA_SEPARATOR, field.getAnnotations()) + BRACKET_CLOSE);
                    }
                    writer.write(NEWLINE);
                }
                writer.write("\nMethods (" + classInfo.getMethods().size() + "):\n");
                for (MethodInfo method : classInfo.getMethods()) {
                    writer.write(INDENT + method.getAccessModifier() + SPACE + method.getReturnType() + SPACE + method.getName() + "(");
                    writer.write(method.getParameters().stream()
                        .map(p -> p.getType() + SPACE + p.getName())
                        .reduce((a, b) -> a + COMMA_SEPARATOR + b)
                        .orElse(""));
                    writer.write(")");
                    if (!method.getAnnotations().isEmpty()) {
                        writer.write(BRACKET_OPEN + String.join(COMMA_SEPARATOR, method.getAnnotations()) + BRACKET_CLOSE);
                    }
                    writer.write(NEWLINE);
                }
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "classes", reportFile.getName()));
    }
    
    private void writeControllersReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "controllers-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("CONTROLLERS SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Total Controllers: " + scanResult.getControllers().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (ClassInfo controller : scanResult.getControllers()) {
                writer.write(DASH_LINE);
                writer.write("Controller: " + controller.getFullClassName() + NEWLINE);
                writer.write(LABEL_FILE + controller.getFilePath() + NEWLINE);
                writer.write(LABEL_ANNOTATIONS + String.join(COMMA_SEPARATOR, controller.getAnnotations()) + NEWLINE);
                writer.write(LABEL_METHODS + controller.getMethods().size() + NEWLINE);
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "controllers", reportFile.getName()));
    }
    
    private void writeEndpointsReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "endpoints-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("REST ENDPOINTS SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Total Endpoints: " + scanResult.getEndpoints().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (EndpointInfo endpoint : scanResult.getEndpoints()) {
                writer.write(DASH_LINE);
                writer.write("HTTP Method: " + endpoint.getHttpMethod() + NEWLINE);
                writer.write("Path: " + endpoint.getPath() + NEWLINE);
                writer.write("Handler Method: " + endpoint.getMethodName() + NEWLINE);
                writer.write("Return Type: " + endpoint.getReturnType() + NEWLINE);
                writer.write("Controller: " + endpoint.getClassName() + NEWLINE);
                writer.write("Has Request Body: " + endpoint.isHasRequestBody() + NEWLINE);
                if (endpoint.isHasRequestBody()) {
                    writer.write("Request Body Type: " + endpoint.getRequestBodyType() + NEWLINE);
                }
                writer.write("Has Path Variable: " + endpoint.isHasPathVariable() + NEWLINE);
                writer.write("\nParameters (" + endpoint.getParameters().size() + "):\n");
                for (ParameterInfo param : endpoint.getParameters()) {
                    writer.write(INDENT + param.getType() + SPACE + param.getName());
                    if (param.getAnnotation() != null) {
                        writer.write(" [@" + param.getAnnotation() + "]");
                    }
                    writer.write(NEWLINE);
                }
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "endpoints", reportFile.getName()));
    }
    
    private void writeEntitiesReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "entities-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("ENTITIES SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Total Entities: " + scanResult.getEntities().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (ClassInfo entity : scanResult.getEntities()) {
                writer.write(DASH_LINE);
                writer.write("Entity: " + entity.getFullClassName() + NEWLINE);
                writer.write(LABEL_FILE + entity.getFilePath() + NEWLINE);
                writer.write(LABEL_ANNOTATIONS + String.join(COMMA_SEPARATOR, entity.getAnnotations()) + NEWLINE);
                writer.write("\nFields (" + entity.getFields().size() + "):\n");
                for (FieldInfo field : entity.getFields()) {
                    writer.write(INDENT + field.getType() + SPACE + field.getName());
                    if (!field.getAnnotations().isEmpty()) {
                        writer.write(BRACKET_OPEN + String.join(COMMA_SEPARATOR, field.getAnnotations()) + BRACKET_CLOSE);
                    }
                    writer.write(NEWLINE);
                }
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "entities", reportFile.getName()));
    }
    
    private void writeServicesReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "services-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("SERVICES SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Total Services: " + scanResult.getServices().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (ClassInfo service : scanResult.getServices()) {
                writer.write(DASH_LINE);
                writer.write("Service: " + service.getFullClassName() + NEWLINE);
                writer.write(LABEL_FILE + service.getFilePath() + NEWLINE);
                writer.write(LABEL_ANNOTATIONS + String.join(COMMA_SEPARATOR, service.getAnnotations()) + NEWLINE);
                writer.write(LABEL_METHODS + service.getMethods().size() + NEWLINE);
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "services", reportFile.getName()));
    }
    
    private void writeRepositoriesReport(ScanResult scanResult, File outputDirectory) throws IOException {
        File reportFile = new File(outputDirectory, "repositories-report.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(SEPARATOR_LINE);
            writer.write("REPOSITORIES SCAN REPORT\n");
            writer.write(SEPARATOR_LINE);
            writer.write("Total Repositories: " + scanResult.getRepositories().size() + NEWLINE);
            writer.write(SEPARATOR_LINE);
            writer.write(NEWLINE);
            
            for (ClassInfo repository : scanResult.getRepositories()) {
                writer.write(DASH_LINE);
                writer.write("Repository: " + repository.getFullClassName() + NEWLINE);
                writer.write(LABEL_FILE + repository.getFilePath() + NEWLINE);
                writer.write(LABEL_ANNOTATIONS + String.join(COMMA_SEPARATOR, repository.getAnnotations()) + NEWLINE);
                writer.write(LABEL_METHODS + repository.getMethods().size() + NEWLINE);
                writer.write(NEWLINE);
            }
        }
        log.info(String.format(WRITTEN_REPORT_TO, "repositories", reportFile.getName()));
    }
}
