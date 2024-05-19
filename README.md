
# Database Utils Library

![Database Utils Library](https://via.placeholder.com/150)

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
  - [Creating and Deleting Databases](#creating-and-deleting-databases)
  - [Creating and Deleting Tables](#creating-and-deleting-tables)
  - [Inserting Data into Tables](#inserting-data-into-tables)
  - [Retrieving Data from Tables](#retrieving-data-from-tables)
  - [Managing Database Users](#managing-database-users)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The **Database Utils Library** is a comprehensive Java library designed to simplify database operations using JDBC. This library abstracts common JDBC tasks into easy-to-use functions, allowing you to create databases, manage tables, handle credentials, and more with minimal effort.

![Database Utils Illustration](https://via.placeholder.com/800x300)

## Features

- **Create and Delete Databases**
- **Create and Delete Tables**
- **Insert and Retrieve Data**
- **Show Table and Database Metadata**
- **Manage Database Users**

## Installation

To use the Database Utils Library in your project, follow these steps:

1. Clone the repository:


   sh
   ```sh
   git clone https://github.com/ddcsoftdev/jdbc-utils-lib.git
   ```

3. Add the library to your project's dependencies. If you're using Maven, add the following to your \`pom.xml\`:


  xml
```
   <dependency>
       <groupId>com.database</groupId>
       <artifactId>utils</artifactId>
       <version>1.0.0</version>
   </dependency>
  ```

3. Alternatively, if you're using Gradle, add the following to your \`build.gradle\`:


   groovy
   ```
   implementation 'com.database:utils:1.0.0'
   ```

## Usage

### Creating and Deleting Databases

To create a new database:

java
```
Connection conn = // obtain your JDBC connection
SQLStatements.createDatabase(conn, "newDatabaseName");
```

To delete a database:

java
```
SQLStatements.deleteDatabase(conn, "databaseNameToDelete");
```

![Database Creation Illustration](https://via.placeholder.com/800x300)

### Creating and Deleting Tables

To create a new table:

java
```
SQLStatements.Column[] columns = {
    new SQLStatements.Column("id", "INT", false),
    new SQLStatements.Column("name", "VARCHAR(100)", true)
};

SQLStatements.createTable(conn, "newTableName", columns);
```

To delete a table:

java
```
SQLStatements.deleteTable(conn, "tableNameToDelete");
```

![Table Creation Illustration](https://via.placeholder.com/800x300)

### Inserting Data into Tables

To insert data into a table:

java
```
Object[] data = {1, "John Doe"};
SQLStatements.insertRegisterToTable(conn, "tableName", data);
```

### Retrieving Data from Tables

To show all data from a table:

java
```
SQLStatements.showAllDataFromTable(conn, "tableName");
```

To get all data from a table as a list:

java
```
ArrayList<Object> results = SQLStatements.getAllDataFromTable(conn, "tableName");
```

![Data Retrieval Illustration](https://via.placeholder.com/800x300)

### Managing Database Users

To create a server login:

java
```
SQLStatements.createServerLogin(conn, "newLoginName", "password123");
```

To grant database access to a user:

java
```
SQLStatements.grantDatabaseAccess(conn, "databaseName", "userName");
```

To remove a user from a database:

java
```
SQLStatements.removeUserFromDatabase(conn, "databaseName", "userName");
```

![User Management Illustration](https://via.placeholder.com/800x300)

## Contributing

We welcome contributions to the Database Utils Library! To contribute, follow these steps:

1. Fork the repository.
2. Create a new branch (\`git checkout -b feature/your-feature\`).
3. Make your changes.
4. Commit your changes (\`git commit -m 'Add some feature'\`).
5. Push to the branch (\`git push origin feature/your-feature\`).
6. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
