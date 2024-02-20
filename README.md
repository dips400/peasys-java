# Peasys Java client library

[![Maven Central]()]()
[![JavaDoc]()]()
[![Build Status]()]()
[![Coverage Status]()]()

The official Peasys Java client library.

## Installation

### Requirements

- Java 11.0.16 or later

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.dips:peasys-java:1.0.0"
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.dips</groupId>
  <artifactId>peasys-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Others

You'll need to manually install the following JARs:

- [The Stripe JAR]()
- [Jackson.core] from <>.
- [Jackson.databind] from <>.

## Documentation

Please see the [Java API docs](https://dips400.com/docs) for the most
up-to-date documentation.

## Usage

### License key
Peasys is a tool used along a license that should be found on the [dips400](https://dips400.com) website. This license key is required for the use of the service Peasys.

### Connexion to the server
Peasys authenticates API requests using your license key, which you can find in the [dashboard](https://dips400.com/account).

```java
PeaClient conn = new PeaClient("PARITION_NAME", PORT, "USERNAME", "PASSWORD", "FUTUR_LICENSE_KEY", true);
System.out.println(conn.connectionStatus);
System.out.println(conn.connexionMessage);            
```

### Query the DB2
For example, use the `executeCreate` method of the `PeaClient` class in order to create a new table in the database.

```java
PeaCreateResponse createResponse = conn.executeCreate("CREATE TABLE schema_table/table_name (nom CHAR(10), age INT)");
System.out.println("Requete correctement exécutée : " + createResponse.hasSucceeded);
System.out.println("Message SQL de la requete : " + createResponse.returnedSQLMessage);
System.out.println("Status SQL de la requete : " + createResponse.returnedSQLState + "\n");
```

### Deconnexion
It is important to always disconnect from the server after you used the connexion.
```java
conn.disconnect();
```
## Support

New features and bug fixes are released on the latest major version of the Peasys Java client library. If you are on an older major version, we recommend that you upgrade to the latest in order to use the new features and 
bug fixes including those for security vulnerabilities. Older major versions of the package will continue to be available for use, but will not be receiving any updates.

## Development

JDK 11 is required to build the Peasys Java library.

To run all checks (tests and code formatting):

```sh
./gradlew check
```

To run the tests:

```sh
./gradlew test
```

[api-docs]: https://dips400.com/docs
[dips]: https://dips400.com
