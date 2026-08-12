package com.cs.harin.transition.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a field in a class
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldInfo {

    private String name;
    private String type;
    private List<String> annotations;
    private String accessModifier;

}
