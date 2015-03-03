# cgwap A cultural game with a purpose

## Installation instructions:

Installing CGWAP requires an instance of the Apache Tomcat Web Server with a Java Runtime Environment version 7 or higher. The WAR file of the game must be placed in the webapps directory.
In addition, a PostgreSQL database is required (version 9.0). CGWAP automatically initializes its tables, it must, however, after the first deployment credentials in the unzipped area of ​​application are defined once in CGWAPs config.properties file. It also can specify the admin user and the login.
In addition to these steps means it is necessary to make sure that the application using the deployment descriptor 'CGWAP' on the server must be running for the paths can be correctly resolved to game resources. However, this should be the case usually automatic, but can be used in more complex situations relevant web server (Apache/nginx servers to upstream about proxy forwarding).
