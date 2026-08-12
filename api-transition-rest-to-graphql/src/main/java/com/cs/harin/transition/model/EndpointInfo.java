package com.cs.harin.transition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a REST endpoint found in a controller
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointInfo {

    private String httpMethod;
    private String path;
    private String methodName;
    private String returnType;
    private List<ParameterInfo> parameters;
    private String className;
    private boolean hasRequestBody;
    private boolean hasPathVariable;
    private String requestBodyType;

}
