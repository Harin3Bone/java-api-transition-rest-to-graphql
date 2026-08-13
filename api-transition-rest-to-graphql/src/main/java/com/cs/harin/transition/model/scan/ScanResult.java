package com.cs.harin.transition.model.scan;

import com.cs.harin.transition.model.ClassInfo;
import com.cs.harin.transition.model.rest.EndpointInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for all scan results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {

    private String packageName;
    private String scanPath;
    private long scanTimestamp;

    @Builder.Default
    private List<ClassInfo> classes = new ArrayList<>();

    @Builder.Default
    private List<ClassInfo> controllers = new ArrayList<>();

    @Builder.Default
    private List<ClassInfo> entities = new ArrayList<>();

    @Builder.Default
    private List<ClassInfo> services = new ArrayList<>();

    @Builder.Default
    private List<ClassInfo> repositories = new ArrayList<>();

    @Builder.Default
    private List<EndpointInfo> endpoints = new ArrayList<>();

}
