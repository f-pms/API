# Prerequisites

- Java 17
- Intellij IDEA or any other IDEs (remember setting up the JDK 17)

# Project Structure

The project utilizes multi-module structure. The modules are:

- `core` - contains the core logic of the application
    - `api` - contains the API services such as REST API
    - `model` - contains the enums and business models which are mapped from entities in the database module
- `integration` - contains the services for integrating external sources such as database or iot devices.
- `support` - contains the common code shared between modules / or any supporting modules such as logging, security
  config, etc.