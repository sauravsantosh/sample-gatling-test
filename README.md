# Sample Gatling Project

### Prerequisites

- Java
- Gradle
- Scala

#### [Running Gatling Test]
One can easily override config variables while running test via command line.
  
```bash    
    ./gradlew clean gatlingRun -Denvironment=<environment-name>
```
Example:

```bash    
    ./gradlew clean gatlingRun -Denvironment=dev
```

To overwrite the default value set in config, please run below command

```bash    
    ./gradlew clean gatlingRun -Denvironment=<environment-name> -Dusers=<no-of-users> -Dramp=<run-time-in seconds>
```

Example:

```bash    
    ./gradlew clean gatlingRun -Denvironment=dev -Dusers=50 -Dramp=10
```

#### [To view report]

To view the report go to `/build/reports/gatling/` and open `index.html` file

