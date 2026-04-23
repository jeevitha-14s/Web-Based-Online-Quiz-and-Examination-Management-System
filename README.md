# Quiz Web Application

A full-stack quiz application built using Spring Boot that allows users to attempt quizzes, submit answers, and view their scores instantly.

---

## Tech Stack

* **Backend:** Spring Boot
* **Database:** H2 (In-Memory Database)
* **Persistence:** Spring Data JPA (Hibernate)
* **Frontend:** Thymeleaf (Server-side rendering)
* **Build Tool:** Gradle

---

## Features

* 📝 Attempt quiz with multiple-choice questions
* ⚡ Real-time answer evaluation
* 📊 Score calculation and result display
* 🗄️ In-memory database (no setup required)
* 🌐 Simple and interactive UI

---

## Project Structure

* `controller/` → Handles HTTP requests
* `service/` → Business logic
* `repository/` → Database operations
* `model/` → Entity classes
* `templates/` → Frontend (Thymeleaf HTML files)
* `resources/` → Configuration files

---

## How to Run Locally

1. Clone the repository:
   git clone https://github.com/YOUR_USERNAME/quiz-web-app.git

2. Navigate to the project folder:
   cd quiz-web-app

3. Run the application:
   ./gradlew bootRun

4. Open in browser:
   http://localhost:8081

---

## H2 Database Console

You can view the database in your browser:

URL: http://localhost:8081/h2-console

Credentials:

* JDBC URL: jdbc:h2:mem:quiz-db
* Username: root
* Password: root

---

## Notes

* Uses H2 in-memory database → data resets on restart
* No external database setup required
* Designed for learning and demonstration purposes

---

## Authors

Jeevitha S
K L Sonika 
Hrithik S P
Kadirisani Neha

---

## Future Improvements

* Add user authentication
* Store quiz history
* Add timer functionality
* Improve UI/UX design

---
