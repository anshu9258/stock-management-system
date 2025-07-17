📦 Stock Management System
A full-featured Stock Management Web Application built using Spring Boot, JDBC, Thymeleaf, and MySQL, allowing users and admins to manage inventory, view stock analytics, and perform CRUD operations on products.

🚀 Features
🔐 Login System with Admin/User roles

📦 Add, Edit, Delete, and View Products

🏷️ Dynamic Category Management

🖼️ Product Image Upload & Display

📊 Stock Analytics Dashboard (Chart.js)

📉 Low-stock alerts

📩 Send PDF reports via Email

🔐 Admin Password Change

🎨 Responsive UI with Bootstrap & Font Awesome

🛠️ Technologies Used
Backend: Java, Spring Boot, Spring MVC, JDBC

Frontend: HTML5, Thymeleaf, Bootstrap 5, Chart.js

Database: MySQL

PDF & Email: iTextPDF, JavaMailSender

⚙️ How to Run Locally
Clone the repo

bash
Copy
Edit
git clone https://github.com/yourusername/stock-management-system.git
cd stock-management-system
Configure MySQL database

Create a new MySQL database:

sql
Copy
Edit
CREATE DATABASE stockdb;
Update your DB credentials in:

css
Copy
Edit
src/main/resources/application.properties
Add tables

sql
Copy
Edit
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255),
  password VARCHAR(255),
  role VARCHAR(50)
);

CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  quantity INT,
  price DOUBLE,
  category VARCHAR(100),
  image VARCHAR(255)
);
Insert Admin User

sql
Copy
Edit
INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$4q7dZlAksSbO3EOftT3ZFeA0uhWLEvGgYI/mPtQ7u0eHoTP2B7YtK', 'admin');
Password: admin123 (BCrypt encoded)

Run the app

bash
Copy
Edit
mvn spring-boot:run
Access the app

Go to: http://localhost:8080

Login with:

makefile
Copy
Edit
Username: admin
Password: admin123
📁 Project Structure
css
Copy
Edit
src/
 └── main/
      ├── java/
      │    └── com.niet.stockmanagement/
      │         ├── controller/
      │         ├── dao/
      │         ├── model/
      │         ├── service/
      │         └── StockManagementSystemApplication.java
      ├── resources/
      │    ├── static/
      │    ├── templates/
      │    └── application.properties
🧑‍💻 Author
Anshu Srivastava

Student, NIET College
