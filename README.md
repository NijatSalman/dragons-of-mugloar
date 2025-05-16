# Dragons Of Mugloar Application

This Spring Boot application automates gameplay for the Dragons of Mugloar game. It handles turn-based task solving,
health recovery, item upgrades, game state tracking, and retry logic. The backend processes game turns recursively until 
the dragon dies or the target score (1000+) is reached.

## Table of Contents
* [General Info](#general-info)
* [Technologies](#technologies)
* [Running the Application](#running-the-application)
    * [Run from Docker](#run-from-docker)
    * [Run Locally](#run-locally)
* [URLs](#urls)

<h2 id="general-info">General Info</h2>
The application includes the following features:
- **API Documentation:** Swagger is integrated for better API documentation and usability.
- **Error Handling:** A global exception handler is implemented to manage errors gracefully.
- **Tracing:** Micrometer Tracing is utilized for logs in application.
- **Testing:** Comprehensive unit tests ensure that the functionality and reliability of the application are maintained. Due to time constraints.
- **Game Automation:** Automates the entire game lifecycle from start to end.
- **Turn Processing:** Evaluates task priorities, performs healing, purchases upgrades, and solves quests.
- **Task Scoring:** Calculates task scores based on predefined rules and prioritizes them.


<h2 id="technologies">Technologies</h2>
The project is created using:
* **JDK 21**
* **Gradle**
* **Spring Boot 3.4.5**
* **Springdoc OpenAPI**
* **Docker**
* **Lombok**

<h2 id="running-the-application">Running the Application</h2>

<h3 id="run-from-docker">Run from Docker</h3>
1. Ensure that Docker is installed on your machine.
2. Navigate to the project root directory.
3. Build and start the Docker containers using the following commands:

   ```bash
    ./gradlew clean build
    docker build -t dragons .
    docker run -it -p 8080:8080 dragons
### Run Locally

**Prerequisites:**
- Ensure that **JDK 21** is installed on your machine.
- Install **Gradle** if not already installed.

**Using IntelliJ IDEA:**
1. Open the `DragonsOfMugloarApplication.java` in IntelliJ.
2. Perform a clean build by selecting **Build > Rebuild Project**.
3. Click the **Run** button to start the application.

**Using Command Line:**
1. Navigate to the project root directory.
2. Run the backend application using Gradle:
    ```bash
    ./gradlew bootRun
    ```

### URLs
- **Backend API Documentation:** `http://localhost:8080/swagger-ui/index.html`
- **Health Check Endpoint:** `http://localhost:8080/actuator/health`