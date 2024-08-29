# CRM Application

## Overview

This CRM (Customer Relationship Management) application is designed to efficiently manage customer data and interactions across multiple departments. Built using **Spring Boot** and **Angular**, it features a robust **REST API**, a powerful statistics dashboard, and secure authentication using **JWT**. The application is designed with **microservices architecture** to ensure scalability, flexibility, and easy maintenance. The design and development process was planned using **UML diagrams** to ensure clear and effective conceptualization.

## Key Features

- **Microservices Architecture**: Designed with a microservices approach to handle different aspects of customer relationship management efficiently across multiple departments.
- **Spring Boot Backend**: The backend is powered by Spring Boot, providing a robust and scalable REST API.
- **Angular Frontend**: A dynamic and responsive frontend built with Angular, ensuring a seamless user experience.
- **Internationalization (i18n)**: Supports multiple languages to cater to a global audience.
- **Secure Authentication**: Uses **JWT (JSON Web Tokens)** for secure user authentication and authorization.
- **Statistics Dashboard**: A comprehensive dashboard that highlights:
  - Best leads.
  - Agent of the month.
  - Various charts and statistics for different functionalities.
- **UML-Based Design**: The app's design was planned using UML diagrams for clarity and effective conception of the system architecture and flow.

## Technologies Used

- **Backend**: Spring Boot, Spring Security, Spring Data JPA, Hibernate, REST API, JWT
- **Frontend**: Angular, Angular Material, ngx-translate (for i18n), NgRx (for state management)
- **Database**: MySQL / PostgreSQL (configurable)
- **Other Tools**: Maven, Gradle, Node.js, npm, Docker, Kubernetes (for containerization and orchestration)

## Architecture Overview

The CRM application follows a **microservices architecture**, where each service handles a specific domain of customer relationship management. This modular design allows independent deployment and scaling of each service as needed. Key components include:

1. **User Management Service**: Manages user registration, authentication, and role-based access control (RBAC) using JWT.
2. **Customer Service**: Handles customer data management, including CRUD operations and relationship management.
3. **Lead Management Service**: Manages lead data and tracks the performance of sales agents.
4. **Dashboard Service**: Aggregates data from various services to provide insightful statistics and charts.
5. **Notification Service**: Manages notifications for customer interactions and updates.
