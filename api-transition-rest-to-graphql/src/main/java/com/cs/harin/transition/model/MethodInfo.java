package com.cs.harin.transition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a method in a class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    private String name;
    private String returnType;
    private List<ParameterInfo> parameters;
    private List<String> annotations;
    private String accessModifier;

}
