# Invoice Generator CLI

Generated invoice based on selected package and usage.

#### Tech Stack
- Java 17
- Spring Shell
- Jackson Databind
- Lombok

### Basic usage

> By being an Spring Boot application, it can be started just by running the class `InvoiceApplication.java`.

Upon running the application it will default to "interactive mode", which opens a shell for command execution.

Typing `help` will show additional available commands

For invoice generation, the command use the command `generate` which accepts the following parameters:
- Package Type (S, M, L): `--package` or `-p`
- Used minutes: `--minutes` or `-m`
- Used SMS: `--sms` or `-s`
> This information can also be viewed with the command `generate --help`


Example:
```shell
invoice:> generate --package L --minutes 150 -sms 100
```

This command will output a table with detailed invoice information.

_The log output for the application is located at `out/invoice.log`_

### Build and execution
```shell
./gradlew build -x test
java -jar build/libs/invoice-0.0.1-SNAPSHOT.jar
```

Executing the application using the jar file enables the possibility to use "non-interactive mode",
which is enabled by adding the command as parameter for the jar execution.

Example:
```shell
java -jar build/libs/invoice-0.0.1-SNAPSHOT.jar generate -p S -m 100 -s 20
```
Also, via jar is also possible to execute another version of the application that takes user input in a slightly different way.

Running the alternate version of the generate command (`gen`), prompts for each information separately.

_The output is presented the same way for both methods._

> Executing the `gen` command when starting the application running the main class, won't have any effect, since there will be no prompts for information.

> So far I could not find a way to use this alternate method on dev environment (running the main class)

With that, there a few things that is left to be done:
### TODO's
- Personalize some parsing error messages
- Migrate completely to the second method of execution ("gen") which prompts for information one by one.
- Add unit tests for the secondary usage method ("gen" command)

### Limitations
- `gen` command does not work when starting the application by running the main class (dev mode)
