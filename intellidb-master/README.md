# IntelliDB 🗣️

![Java](https://img.shields.io/badge/Java-17-blue?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-green?logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?logo=thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow)

> A modern web application that allows users to securely connect to their databases and interact with them using natural language.

IntelliDB bridges the gap between complex database queries and human conversation. It empowers team members of all technical levels to explore data intuitively, without needing to write a single line of SQL.

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [📸 Screenshots](#-screenshots)
- [🛠️ Tech Stack](#-tech-stack)
- [🚀 Getting Started](#-getting-started)
- [📞 Contact](#-contact)
- [📄 License](#-license)

---

## ✨ Features

* **🔐 Secure User Authentication:** Full login/registration system with persistent user sessions, powered by Spring Security.
* **💾 Encrypted Connection Management:** Save, edit, and delete multiple database connections. Credentials are fully encrypted at rest using AES.
* **💬 Natural Language to SQL:** Uses a Large Language Model (LLM) to translate plain English questions into executable SQL queries.
* **🧠 Schema-Aware Intelligence:** The AI is provided with the full table schema to generate accurate, context-aware queries.
* **🖥️ Interactive Dashboard:** A sleek, dark-themed dashboard to manage connections and view database schemas.
* **📊 Dynamic Query Interface:** Features a schema viewer, a query history panel, and an interactive results table.
* **🔍 Data Exploration Tools:**
    * **Table Previews:** Click any table to see its structure and a preview of its data.
    * **Sortable Results:** Click on any column header in the results table to sort the data instantly.
    * **Export to CSV:** Download any query result set as a CSV file with one click.
* **🛡️ Built-in Security:** Includes a confirmation step for potentially destructive queries (e.g., `UPDATE`, `DELETE`) to prevent accidental data modification.

---

## 📸 Screenshots

#### Main Connections Dashboard


#### Interactive Query Dashboard


#### Public Landing Page


---

## 🛠️ Tech Stack

| Category   | Technology                                                                              |
|------------|-----------------------------------------------------------------------------------------|
| **Backend** | `Java 17`, `Spring Boot`, `Spring Security`, `Spring Data JPA`                            |
| **Frontend** | `Thymeleaf` with Layout Dialect, `HTML5`, `CSS3`, `JavaScript (Fetch API)`               |
| **Database** | `H2` (for local app data), `MySQL` (for user connections)                               |
| **AI** | `Google Gemini API`                                                                     |
| **Build** | `Apache Maven`                                                                          |

---

## 🚀 Getting Started

Follow these instructions to get a local copy of IntelliDB up and running.

### Prerequisites

* Java Development Kit (JDK) 17 or later
* Apache Maven
* Git

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/sripallavyerram/IntelliDB-NLP-Database.git](https://github.com/sripallavyerram/Intellidb.git)
    cd intellidb
    ```

2.  **Configure the application:**
    Open the `src/main/resources/application.properties` file. You must provide your own secret keys for the application to function.

    ```properties
    # Your API key from Google AI Studio
    gemini.api.key=YOUR_GOOGLE_GEMINI_API_KEY

    # A random 16-character string for encrypting passwords
    encryption.secret.key=ThisIsA16CharKey
    ```

3.  **Run the application:**
    Use Maven to build and run the Spring Boot application.
    ```bash
    mvn spring-boot:run
    ```
    The application will be running at `http://localhost:8080`.

### Usage

1.  Navigate to **http://localhost:8080**.
2.  Click "Sign Up" to create a new account, then log in.
3.  On the dashboard, click "Add Connection" to save the credentials for your MySQL database.
4.  Click "Connect" on a saved connection to enter the query interface.
5.  Ask questions in plain English and explore your data!

---

## 📞 Contact

**Sripallav Yerram**

-   **Email:** Sripallav85@gmail.com
-   **LinkedIn:** [linkedin.com/in/yerram-sripallav-bb976525b](https://www.linkedin.com/in/yerram-sripallav-bb976525b/)
-   **GitHub:** [github.com/sripallavyerram](https://github.com/sripallavyerram)

---

## 📄 License

This project is licensed under the MIT License.
