# Book Library Application

## Overview
The Book Library Application is a Spring Boot-based service for managing a library of books. The application allows users to:

* Add new books to the library
* Borrow available books
* View a list of available books
* Track book loans and receive notifications for overdue books

The application is dockerized and uses JDK 21. It supports basic authentication and integrates with an H2 database for in-memory storage.

Features
* Book Management: Add new books, view available books.
* Book Borrowing: Borrow books and check availability.
* User Management: Only registered users can add or borrow books (if security is enabled).
* Overdue Notification: Logs notifications for overdue books.
*Docker Support: Easily deployable using Docker.
## Prerequisites
* Docker and Docker Compose
* Java JDK 21
* Maven 3.8.6 or higher (if building without Docker)

> [!IMPORTANT]
> Please note there are various configuration that can be done to alter the application behaviour
> * Available endpoints can be examined through:<br/>
> `http://<server_ip>:<server_port>/swagger-ui/index.html`<br/>
> Note that if security is enabled (see details below) to use the swagger request mechanic you will need to use its authentication (lock symbol).
> * Basic Authentication is enabled by default in the application, using only the username for now. <br/>
> Security can be turned on or off in properties file via: <br/>
> `library.security.enabled` <br/>
> The first (default) username is set through: <br/>
> `library.security.default.user`
> * The maximum time allowed for book loan is 3 days, this can be changed via:
> `library.loan.max-duration-days` <br/>
> The application will check for overdue books every day at 12:00 AM, this can be configured via: <br/>
> `library.loan.scheduler.cron`
