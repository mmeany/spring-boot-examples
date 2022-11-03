# Overview

A collection of standalone projects that demonstrate aspects of Spring Boot technology all in a single repository.

All project dependency versions are defined in the top level [gradle.properties](gradle.properties) file. This should make
updating to newer version of libraries and frameworks simpler and highlight any issues with said upgrades early on.

This is all a Work In Progress, as fleeting thoughts catch me I may add new projects without retro fitting to some that are already there (`common` is an example).
# The Projects

| Project                                                         | Description                                                                                                                      |
|-----------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| [Actuator Security](./rest-api/actuator-security/README.md)     | A WebMcv application with a single controller demonstrating configuration of security for Actuator endpoints                     |
| [Common](./rest-api/common/README.md)                           | Where I intend to start gathering reusable stuff                                                                                 |
| [Cucumber](./rest-api/cucumber-spring-testcontainers/README.md) | Using Cucumber to integration test a Spring Boot app used in parallel with Testcontainers                                        |
| [Example API](./rest-api/example-api/README.md)                 | A WebMcv application with a single controller. Makes us of WebMvc, JPA, Spring Security, Swagger, Actuator, CORS, TestContainers |
| [Multi Microservice](./rest-api/multi-microservice/README.md)   | A collection of microservices, using JWT, intercommunication, docker based integration tests                                     |
| [Exploring RBAC](./rest-api/rbac-keycloak/README.md)            | Using Keycloak to provide RBAC roles, either from realm or client claims (`` or ``)                                              |
| [Docker](./docker/README.md)                                    | Docker compose file(s) for configuring environments                                                                              |

Any requests?

