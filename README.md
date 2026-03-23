# StockFlow - Java Inventory Management System

A networked desktop inventory management system supporting multi-client connections. Features a Java Swing GUI client, Socket server, object serialization, and an embedded SQLite database.

## Prerequisites

- Java Development Kit (JDK) 17 or higher.
- `sqlite-jdbc` JAR file must be downloaded and added to your classpath.

## Compilation

Navigate to the `src` directory and run:

```bash
javac -cp ".;/path/to/sqlite-jdbc.jar" $(find . -name "*.java")
```
*(On Windows, replace `:` with `;` for classpath, and use `dir /s /B *.java` instead of `find`)*

## Running the Server

Start the server process first:

```bash
java -cp ".;/path/to/sqlite-jdbc.jar" com.inventory.server.Server
```
*The server runs on port `5000` locally.*

## Running the Client

Start the GUI client Application:

```bash
java -cp "." com.client.LoginScreen
```

### Connectivity Requirements

- To connect from a different machine, change `localhost` on the Login Screen to the IP address of the machine hosting the Server program.
- Ensure port `5000` is open on the host's firewall.

### Default Credentials

Username: `admin`  
Password: `admin123`
