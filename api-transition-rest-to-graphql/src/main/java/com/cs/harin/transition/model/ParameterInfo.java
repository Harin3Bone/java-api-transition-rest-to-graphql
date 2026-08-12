package com.cs.harin.transition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a method parameter
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterInfo {

    private String name;
    private String type;
    private String annotation;

}
