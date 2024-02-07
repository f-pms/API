# PMS

## Prerequisites

- Java 17
- Intellij IDEA or any other IDEs (remember setting up the JDK 17)

## Project Structure

The project utilizes multi-module structure. The modules are:

- `core` - contains the core logic of the application
    - `api` - contains the API services such as REST API
    - `model` - contains the enums and business models which are mapped from entities in the
      database module
- `integration` - contains the services for integrating external sources such as database or IoT
  devices.
- `support` - contains the common code shared between modules / or any supporting modules such as
  logging, security
  config, etc.

## IntelliJ setup

- Install [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) plugin
- Add additional configuration by going to `Help→Edit Custom VM Options...`, add these lines

```text
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

- Import the following code style scheme in
  IDE `(Setting -> Editor -> Code Style -> Import scheme)`: [java_code_style.xml](https://github.com/f-pms/Devops/blob/master/resources/java_code_style.xml)
- Enable the google-java-format plugin `(Setting -> google-java-format plugin -> Enable)`
- Install [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin
