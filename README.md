# Prerequisites

- Java 17
- Intellij IDEA or any other IDEs (remember setting up the JDK 17)

# Project Structure

The project utilizes multi-module structure. The modules are:

- `core` - contains the core logic of the application
  - `api` - contains the API services such as REST API
  - `model` - contains the enums and business models which are mapped from entities in the database module
- `integration` - contains the services for integrating external sources such as database or IoT devices.
- `support` - contains the common code shared between modules / or any supporting modules such as logging, security config, etc.

# Development setup

- Install [google-java-format](https://plugins.jetbrains.com/plugin/8527) plugin, [reference docs](https://github.com/google/google-java-format?tab=readme-ov-file#intellij-android-studio-and-other-jetbrains-ides)
- Install [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin
