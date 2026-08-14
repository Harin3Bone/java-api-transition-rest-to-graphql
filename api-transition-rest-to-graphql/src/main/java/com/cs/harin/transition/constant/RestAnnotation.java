package com.cs.harin.transition.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestAnnotation {

    public static final String GET_MAPPING = "GetMapping";
    public static final String POST_MAPPING = "PostMapping";
    public static final String PUT_MAPPING = "PutMapping";
    public static final String DELETE_MAPPING = "DeleteMapping";
    public static final String PATCH_MAPPING = "PatchMapping";
    public static final String REQUEST_MAPPING = "RequestMapping";

    public static final String PATH_VARIABLE = "PathVariable";
    public static final String REQUEST_BODY = "RequestBody";

}
