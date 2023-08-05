# Code style and quality

We use [SonarQube](https://www.sonarqube.org/) to check code quality and style.
It's defined in `compose.yml` as container and can be started with command: 
> docker-compose up sonarqube

Used rules are defined in `sonar/java.xml` file.

## Setup SonarQube

1. Start SonarQube server container on Docker
2. Login to SonarQube server on http://localhost:9000 with default credentials (admin/admin)
   (if you will be required to change password, do it but update your build.gradle file with new password
   or change it back to admin/admin)
3. Create custom rule profile
4. Import `sonar/java.xml` file (Quality Profiles -> Restore)
5. Create new project
  - Project key: `CashGoalsBackend`
  - Project name: `CashGoalsBackend`
  - Use existing key from `build.gradle` file
6. Run `./gradlew sonar` or gradle task `verification/sonar` from IDE
7. Go to Project Settings -> Quality Profiles and set profile you imported in step 4 as default

### SonarQube usage

```bash
./gradlew test jacocoTestReport sonar
```

## Setup SonarLint

1. Install SonarLint plugin in your IDE
2. Add new SonarQube server