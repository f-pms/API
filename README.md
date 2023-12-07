# Prerequisites
- Java 17
- Intellij IDEA or any other IDEs (remember setting up the JDK 17)

# Project Structure
The project utilizes multi-module structure. The modules are:
- `core` - contains the core logic of the application
  - `api` - contains the API services such as REST API
  - `model` - contains the business models, which are mapped from entities in the database module
  - `enum` - contains the enums used in the application
- `storage` - contains the storage services such as database
- `support` - contains the common code shared between modules / or any supporting modules such as logging, security config, etc.