package com.cs.harin.transition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a Java class found during scanning
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfo {

    private String packageName;
    private String className;
    private String fullClassName;
    private String filePath;
    private List<String> annotations;
    private List<FieldInfo> fields;
    private List<MethodInfo> methods;
    private String classType; // CONTROLLER, ENTITY, SERVICE, REPOSITORY, DAO, CONFIG, UTIL, OTHER

}
