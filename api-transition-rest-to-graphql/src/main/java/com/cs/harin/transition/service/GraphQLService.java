package com.cs.harin.transition.service;

import com.cs.harin.transition.model.rest.EndpointInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates GraphQL Query classes from REST GET endpoints
 */
@Slf4j
public class GraphQLService {

    private static final String QUERY_MAPPING_ANNOTATION = "@QueryMapping";
    private static final String ARGUMENT_ANNOTATION = "@Argument";

    public void generateQueryClass(List<EndpointInfo> getEndpoints, String packageName, File outputDirectory) throws IOException {
        if (getEndpoints.isEmpty()) {
            log.warn("No GET endpoints found to generate queries");
            return;
        }

        String className = extractControllerName(getEndpoints.get(0).getClassName()) + "Query";
        String filePath = outputDirectory.getAbsolutePath() + File.separator + className + ".java";

        log.info("Generating GraphQL Query class: {} at {}", className, filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(generateQueryClassContent(className, packageName, getEndpoints));
        }

        log.info("Successfully generated GraphQL Query class: {}", className);
    }

    private String generateQueryClassContent(String className, String packageName, List<EndpointInfo> endpoints) {
        StringBuilder sb = new StringBuilder();

        // Package declaration
        sb.append("package ").append(packageName).append(".graphql;\n\n");

        // Imports
        sb.append("import org.springframework.graphql.data.method.annotation.Argument;\n");
        sb.append("import org.springframework.graphql.data.method.annotation.QueryMapping;\n");
        sb.append("import org.springframework.stereotype.Controller;\n");
        sb.append("import lombok.RequiredArgsConstructor;\n\n");

        // Assign Return Type
        endpoints.stream()
                .map(EndpointInfo::getReturnType)
                .distinct()
                .forEach(returnType -> {
                    if (!isPrimitiveOrJavaLang(returnType)) {
                        sb.append("// TODO: Add proper import for ").append(returnType).append("\n");
                    }
                });
        sb.append("\n");

        // Class declaration
        sb.append("/**\n");
        sb.append(" * GraphQL Query Controller generated from REST endpoints\n");
        sb.append(" */\n");
        sb.append("@Controller\n");
        sb.append("@RequiredArgsConstructor\n");
        sb.append("public class ").append(className).append(" {\n\n");

        // Add service field injection
        String serviceName = extractServiceName(endpoints.get(0).getClassName());
        sb.append("    // TODO: Inject your service here\n");
        sb.append("    // private final ").append(serviceName).append(" ").append(toFieldName(serviceName)).append(";\n\n");

        // Generate query methods
        for (EndpointInfo endpoint : endpoints) {
            sb.append(generateQueryMethod(endpoint));
            sb.append("\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    private String generateQueryMethod(EndpointInfo endpoint) {
        StringBuilder sb = new StringBuilder();

        // @QueryMapping annotation
        String queryName = convertToGraphQLQueryName(endpoint.getMethodName(), endpoint.getPath());
        sb.append("    ").append(QUERY_MAPPING_ANNOTATION).append("(name = \"").append(queryName).append("\")\n");

        // Method signature
        sb.append("    public ").append(endpoint.getReturnType()).append(" ")
                .append(endpoint.getMethodName()).append("(");

        // Parameters with @Argument
        if (endpoint.getParameters() != null && !endpoint.getParameters().isEmpty()) {
            String params = endpoint.getParameters().stream()
                    .filter(param -> param.getAnnotation() != null && param.getAnnotation().contains("PathVariable"))
                    .map(param -> ARGUMENT_ANNOTATION + " " + param.getType() + " " + param.getName())
                    .collect(Collectors.joining(", "));
            sb.append(params);
        }

        sb.append(") {\n");

        // Method body
        sb.append("        // TODO: Implement query logic\n");
        sb.append("        // Original REST method: ").append(endpoint.getMethodName()).append("\n");
        
        if (endpoint.getReturnType().equals("void")) {
            sb.append("        return;\n");
        } else {
            sb.append("        return null; // TODO: Return actual data\n");
        }
        
        sb.append("    }\n");

        return sb.toString();
    }

    private String convertToGraphQLQueryName(String methodName, String directoryPath) {
        // Remove 'get' prefix if exists and convert to camelCase
        String name = methodName;
        if (name.startsWith("get")) {
            name = name.substring(3);
            if (!name.isEmpty()) {
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
        }
        return name;
    }

    private String extractControllerName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        String className = lastDot >= 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
        // Remove 'Controller' suffix if exists
        if (className.endsWith("Controller")) {
            return className.substring(0, className.length() - "Controller".length());
        }
        return className;
    }

    private String extractServiceName(String controllerClassName) {
        String baseName = extractControllerName(controllerClassName);
        return baseName + "Service";
    }

    private String toFieldName(String className) {
        if (className.isEmpty()) {
            return className;
        }
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    private boolean isPrimitiveOrJavaLang(String type) {
        return type.equals("void") || type.equals("int") || type.equals("long") ||
               type.equals("double") || type.equals("float") || type.equals("boolean") ||
               type.equals("byte") || type.equals("short") || type.equals("char") ||
               type.startsWith("String") || type.startsWith("Integer") || 
               type.startsWith("Long") || type.startsWith("Double") ||
               type.startsWith("List") || type.startsWith("Map") || type.startsWith("Set");
    }
}
