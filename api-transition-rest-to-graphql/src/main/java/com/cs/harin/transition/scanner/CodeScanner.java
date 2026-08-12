package com.cs.harin.transition.scanner;

import com.cs.harin.transition.model.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scans Java source code and extracts information about classes, methods, and REST endpoints
 */
public class CodeScanner {
    
    private final Log log;
    private final JavaParser javaParser;
    
    public CodeScanner(Log log) {
        this.log = log;
        this.javaParser = new JavaParser();
    }
    
    public ScanResult scan(Path packagePath, String packageName) throws IOException {
        ScanResult result = ScanResult.builder()
            .packageName(packageName)
            .scanPath(packagePath.toString())
            .scanTimestamp(System.currentTimeMillis())
            .build();
        
        List<File> javaFiles = findJavaFiles(packagePath);
        log.info("Found " + javaFiles.size() + " Java files to scan");
        
        for (File javaFile : javaFiles) {
            try {
                scanFile(javaFile, result);
            } catch (Exception e) {
                log.warn("Error scanning file " + javaFile.getName() + ": " + e.getMessage());
            }
        }
        
        return result;
    }
    
    private List<File> findJavaFiles(Path directory) throws IOException {
        return Files.walk(directory)
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".java"))
            .map(Path::toFile)
            .collect(Collectors.toList());
    }
    
    private void scanFile(File javaFile, ScanResult result) throws IOException {
        CompilationUnit cu = javaParser.parse(javaFile).getResult().orElse(null);
        if (cu == null) {
            log.warn("Could not parse file: " + javaFile.getName());
            return;
        }
        
        cu.getTypes().forEach(type -> {
            if (type instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) type;
                if (!classDecl.isInterface()) {
                    ClassInfo classInfo = extractClassInfo(classDecl, javaFile, cu);
                    result.getClasses().add(classInfo);
                    
                    // Categorize by type
                    String classType = classInfo.getClassType();
                    switch (classType) {
                        case "CONTROLLER":
                            result.getControllers().add(classInfo);
                            extractEndpoints(classDecl, classInfo, result);
                            break;
                        case "ENTITY":
                            result.getEntities().add(classInfo);
                            break;
                        case "SERVICE":
                            result.getServices().add(classInfo);
                            break;
                        case "REPOSITORY":
                            result.getRepositories().add(classInfo);
                            break;
                    }
                }
            }
        });
    }
    
    private ClassInfo extractClassInfo(ClassOrInterfaceDeclaration classDecl, File javaFile, CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
            .map(pd -> pd.getNameAsString())
            .orElse("");
        
        List<String> annotations = classDecl.getAnnotations().stream()
            .map(AnnotationExpr::getNameAsString)
            .collect(Collectors.toList());
        
        List<FieldInfo> fields = classDecl.getFields().stream()
            .flatMap(field -> field.getVariables().stream()
                .map(var -> FieldInfo.builder()
                    .name(var.getNameAsString())
                    .type(var.getTypeAsString())
                    .annotations(field.getAnnotations().stream()
                        .map(AnnotationExpr::getNameAsString)
                        .collect(Collectors.toList()))
                    .accessModifier(field.getAccessSpecifier().asString())
                    .build()))
            .collect(Collectors.toList());
        
        List<MethodInfo> methods = classDecl.getMethods().stream()
            .map(method -> MethodInfo.builder()
                .name(method.getNameAsString())
                .returnType(method.getTypeAsString())
                .parameters(method.getParameters().stream()
                    .map(param -> ParameterInfo.builder()
                        .name(param.getNameAsString())
                        .type(param.getTypeAsString())
                        .annotation(param.getAnnotations().isEmpty() ? null : 
                            param.getAnnotations().get(0).getNameAsString())
                        .build())
                    .collect(Collectors.toList()))
                .annotations(method.getAnnotations().stream()
                    .map(AnnotationExpr::getNameAsString)
                    .collect(Collectors.toList()))
                .accessModifier(method.getAccessSpecifier().asString())
                .build())
            .collect(Collectors.toList());
        
        String classType = determineClassType(classDecl.getNameAsString(), annotations);
        
        return ClassInfo.builder()
            .packageName(packageName)
            .className(classDecl.getNameAsString())
            .fullClassName(packageName + "." + classDecl.getNameAsString())
            .filePath(javaFile.getAbsolutePath())
            .annotations(annotations)
            .fields(fields)
            .methods(methods)
            .classType(classType)
            .build();
    }
    
    private String determineClassType(String className, List<String> annotations) {
        if (annotations.contains("RestController") || annotations.contains("Controller")) {
            return "CONTROLLER";
        } else if (annotations.contains("Entity") || annotations.contains("Document") || annotations.contains("Table")) {
            return "ENTITY";
        } else if (annotations.contains("Service")) {
            return "SERVICE";
        } else if (annotations.contains("Repository")) {
            return "REPOSITORY";
        } else if (annotations.contains("Configuration")) {
            return "CONFIG";
        } else if (className.endsWith("DAO") || className.endsWith("Dao")) {
            return "DAO";
        } else if (className.endsWith("Util") || className.endsWith("Utils") || className.endsWith("Helper")) {
            return "UTIL";
        }
        return "OTHER";
    }
    
    private void extractEndpoints(ClassOrInterfaceDeclaration classDecl, ClassInfo classInfo, ScanResult result) {
        String baseMapping = getBaseMappingPath(classDecl);
        
        classDecl.getMethods().forEach(method -> {
            List<AnnotationExpr> mappingAnnotations = method.getAnnotations().stream()
                .filter(ann -> isMappingAnnotation(ann.getNameAsString()))
                .collect(Collectors.toList());
            
            for (AnnotationExpr mappingAnnotation : mappingAnnotations) {
                EndpointInfo endpoint = extractEndpointInfo(method, mappingAnnotation, baseMapping, classInfo);
                result.getEndpoints().add(endpoint);
            }
        });
    }
    
    private String getBaseMappingPath(ClassOrInterfaceDeclaration classDecl) {
        return classDecl.getAnnotations().stream()
            .filter(ann -> ann.getNameAsString().equals("RequestMapping"))
            .findFirst()
            .map(ann -> extractPathFromAnnotation(ann))
            .orElse("");
    }
    
    private boolean isMappingAnnotation(String annotationName) {
        return annotationName.equals("GetMapping") ||
               annotationName.equals("PostMapping") ||
               annotationName.equals("PutMapping") ||
               annotationName.equals("DeleteMapping") ||
               annotationName.equals("PatchMapping") ||
               annotationName.equals("RequestMapping");
    }
    
    private EndpointInfo extractEndpointInfo(MethodDeclaration method, AnnotationExpr mappingAnnotation, 
                                             String baseMapping, ClassInfo classInfo) {
        String httpMethod = determineHttpMethod(method, mappingAnnotation.getNameAsString());
        String path = baseMapping + extractPathFromAnnotation(mappingAnnotation);
        
        boolean hasRequestBody = method.getParameters().stream()
            .anyMatch(param -> param.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals("RequestBody")));
        
        boolean hasPathVariable = method.getParameters().stream()
            .anyMatch(param -> param.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals("PathVariable")));
        
        String requestBodyType = method.getParameters().stream()
            .filter(param -> param.getAnnotations().stream()
                .anyMatch(ann -> ann.getNameAsString().equals("RequestBody")))
            .findFirst()
            .map(param -> param.getTypeAsString())
            .orElse(null);
        
        List<ParameterInfo> parameters = method.getParameters().stream()
            .map(param -> ParameterInfo.builder()
                .name(param.getNameAsString())
                .type(param.getTypeAsString())
                .annotation(param.getAnnotations().isEmpty() ? null : 
                    param.getAnnotations().get(0).getNameAsString())
                .build())
            .collect(Collectors.toList());
        
        return EndpointInfo.builder()
            .httpMethod(httpMethod)
            .path(path)
            .methodName(method.getNameAsString())
            .returnType(method.getTypeAsString())
            .parameters(parameters)
            .className(classInfo.getFullClassName())
            .hasRequestBody(hasRequestBody)
            .hasPathVariable(hasPathVariable)
            .requestBodyType(requestBodyType)
            .build();
    }
    
    private String determineHttpMethod(MethodDeclaration method, String annotationName) {
        switch (annotationName) {
            case "GetMapping":
                return "GET";
            case "PostMapping":
                return "POST";
            case "PutMapping":
                return "PUT";
            case "DeleteMapping":
                return "DELETE";
            case "PatchMapping":
                return "PATCH";
            case "RequestMapping":
                // Try to extract method from annotation
                return method.getAnnotations().stream()
                    .filter(ann -> ann.getNameAsString().equals("RequestMapping"))
                    .findFirst()
                    .map(ann -> extractMethodFromRequestMapping(ann))
                    .orElse("GET");
            default:
                return "UNKNOWN";
        }
    }
    
    private String extractMethodFromRequestMapping(AnnotationExpr annotation) {
        String annotationStr = annotation.toString();
        if (annotationStr.contains("RequestMethod.GET")) return "GET";
        if (annotationStr.contains("RequestMethod.POST")) return "POST";
        if (annotationStr.contains("RequestMethod.PUT")) return "PUT";
        if (annotationStr.contains("RequestMethod.DELETE")) return "DELETE";
        if (annotationStr.contains("RequestMethod.PATCH")) return "PATCH";
        return "GET";
    }
    
    private String extractPathFromAnnotation(AnnotationExpr annotation) {
        String annotationStr = annotation.toString();
        
        // Try to extract value from annotation
        if (annotationStr.contains("value")) {
            int valueIndex = annotationStr.indexOf("value");
            int startQuote = annotationStr.indexOf("\"", valueIndex);
            int endQuote = annotationStr.indexOf("\"", startQuote + 1);
            if (startQuote != -1 && endQuote != -1) {
                return annotationStr.substring(startQuote + 1, endQuote);
            }
        }
        
        // Try simple format: @GetMapping("/path")
        int firstQuote = annotationStr.indexOf("\"");
        int lastQuote = annotationStr.lastIndexOf("\"");
        if (firstQuote != -1 && lastQuote != -1 && firstQuote != lastQuote) {
            return annotationStr.substring(firstQuote + 1, lastQuote);
        }
        
        return "";
    }
}
