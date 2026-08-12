# Java Spring Boot API Transition REST to GraphQL

## Description

This project implements plugin for Maven to transition a Java Spring Boot API from using RESTful endpoints to GraphQL.

## Features

- Automatically generates GraphQL API based on existing RESTful endpoints
- Automatically generates GraphQL schema and resolvers based on the existing RESTful request body.

## Usage
Add this plugin into the Spring Boot project at `pom.xml`

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.cs.harin</groupId>
            <artifactId>api-transition-rest-to-graphql</artifactId>
            <version>develop-SNAPSHOT</version>
            <configuration>
                <!-- Base package to scan - CHANGE THIS to match your package -->
                <basePackage>com.cs.harin.rest</basePackage>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Data Structure

### REST Annotation Driven

This directory contains the source code for the RESTful endpoints of the Java Spring Boot API
by using `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` annotations e.g,

```java
@GetMapping("/users")
public List<User> getUsers() {
    // ...
}

@PostMapping("/users")
public User createUser(@RequestBody User user) {
    // ...
}
```

### REST Classic Mapper

This directory contains the source code for the RESTful endpoints of the Java Spring Boot API
by using classic mapper approach like `@RequestMapping` e.g,

```java
@RequestMapping("/users", method = RequestMethod.GET)
public List<User> getUsers() {
    // ...
}

@RequestMapping(value = "/users", method = RequestMethod.POST)
public User createUser(@RequestBody User user) {
    // ...
}
```

## Contribution

![Harin Thananam](https://img.shields.io/badge/Harin3Bone-231F20?&style=flat&logo=github&logoColor=ffffff)