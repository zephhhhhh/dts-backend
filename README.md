# HMCTS Dev Test Backend
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Static Badge](https://img.shields.io/badge/pages-Docs-informational?logo=github)](https://zephhhhhh.github.io/dts-backend/)


This is a backend implementation for the [DTS Developer Test](https://github.com/hmcts/dts-developer-challenge)
based on the HMCTS backend reference stack repository [DTS Dev Test Backend](https://github.com/hmcts/hmcts-dev-test-backend).

I have referenced other HMCTS repositories such as [Opal fines service](https://github.com/hmcts/opal-fines-service)
to attempt to match the overall project architecture, layout, conventions and code-style as much as
reasonably possible with some adjustments in order to aid in the ease of use and viewing the project.

The project passes checkstyle tests defined by the reference repository.

## Documentation
API documentation in the form of Swagger docs have been hosted as github pages available to view here: [API Documentation](https://zephhhhhh.github.io/dts-backend/)

### Brief endpoint overview

| Method | Path            | Purpose                |
|--------|-----------------|------------------------|
| GET    | /tasks/all      | List all tasks         |
| GET    | /tasks/{taskId} | Get a task by ID       |
| POST   | /tasks/create   | Create a task          |
| PATCH  | /tasks/{taskId} | Update a task's status |
| DELETE | /tasks/{taskId} | Delete a task          |

## Pre-requisites
This project was originally tested and ran on Java 21,
however the tests run in a Java 17 context, so it should run there too.

## Build instructions
### Compile & run
```bash
./gradlew run
```

### Run tests
```bash
./gradlew test
./gradlew integration
./gradlew functional
./gradlew smoke
```

## Database configuration
This project uses SQLite instead of PostgresQL,
mostly due to configuration issues of PostgresQL not wanting to run
correctly on my machine.

The default database name is `tasks.db`, a temporary database `functional-test.db`
is created for running tests and is dropped after execution.

The SQLite database is accessed in the same way as the PostgresQL database in
other HMCTS repositories.

## CI/CD
All CI/CD pipelines from the reference repository are retained with modifications
to the documentation action to deploy the documentation to GitHub pages.
