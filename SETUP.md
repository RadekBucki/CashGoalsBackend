# Application setup

## To start application there are two options:

### Docker + Local Spring backend

To start database on docker use command:
```bash
docker-compose -f compose.yaml up 
```

Alternatively, for JetBrains Intellij users they can use green button on the left in opened docker-compose file.

After this you can build Spring backend locally in your environment:
```bash
  ./gradlew bootRun
```

Alternatively, for JetBrains Intellij users click green button in opened
[CashGoalsBackendApplication.java](src/main/java/pl/cashgoals/CashGoalsBackendApplication.java) file

### Docker and Spring backend
Before dockerizing you have to create .jar file with command:
```bash
  ./gradlew bootJar
```

To start application within docker containers you can use created docker-compose file
```bash
  docker-compose -f compose.yaml up
```

Docker-compose will automatically create database, populate it with test data and start spring backend afterwards.
No further action is required, Dockerfile is only used to build spring application as the part of the docker-compose.

Alternatively, for JetBrains Intellij users they can use green button on the left in opened docker-compose file.

## Local usefull links

[GraphiQL UI](http://localhost:8080/graphiql?path=/graphql)

## Recommended Intellij configurations

1. Run with dev profile
   - Spring configuration with active profiles: dev
2. Run with prod profile
   - Spring configuration with active profiles: prod
3. Sonar
   - Graddle configuration with run arguemnts: test jacocoTestReport sonar

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.2/maven-plugin/reference/html/#build-image)

