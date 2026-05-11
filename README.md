<h2>Small chatbot application</h2>

Built with Java and Spring Boot

**How to run the application:**
1. Clone the repository to your Intellij IDEA. 

2. **Configure the Model**: Open `src/main/resources/application.properties` and set the desired AI model after `ai.model.name=...` (e.g., `ai.model.name=openrouter/free`).

3. **Add your API Key**: * In your IDE (e.g., IntelliJ), go to **Edit Configurations**.

    - Add an **Environment Variable**: `OPENROUTER_KEY={your_api_key_here}`.

4. **Launch**: Start the application.

**How to use:**

- **Web Interface**: Go to `http://localhost:8080/` to start chatting in the UI.

- **API Documentation**: Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.
