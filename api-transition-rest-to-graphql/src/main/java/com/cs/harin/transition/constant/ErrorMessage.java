package com.cs.harin.transition.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String BASE_PACKAGE_NOT_SPECIFIED = "The \"basePackage\" must be specified in pom file or command line";
    public static final String UNEXPECTED_EXCEPTION = "Failed to scan code";

}
