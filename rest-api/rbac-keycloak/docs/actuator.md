# Introduction

The application makes use of the Spring [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#actuator) which provides
a few endpoints for getting some insight to the running application.

A gradle plugin included in the global [build.gradle](../../../build.gradle), [gradle-git-properties](https://github.com/n0mer/gradle-git-properties)
gathers some information about the build and makes it available via the `actuator/info` with the following configuration
added to [application.yml](../rbac-rest-api/src/main/resources/application.yml):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  info:
    env:
      enabled: true
    git:
      mode: full
    java:
      enabled: true
```

As a result there are three endpoints exposed:

* [Health](http://127.0.0.1:9081/actuator/health)
* [Info](http://127.0.0.1:9081/actuator/info)
* [Metrics](http://127.0.0.1:9081/actuator/info)

As two of these endpoints expose sensitive information access to them are restricted, see [Security Configuration](./configuration.md#Spring Security Configuration).
