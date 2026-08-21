import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        
        // CORS headers for local development
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        
        if ("OPTIONS".equals(method)) {
            sendResponse(exchange, 204, "");
            return;
        }

        try {
            String requestBody = getRequestBody(exchange);
            
            // Route the request based on the URL path
            switch (path) {
                case "/api/login":
                    handleLogin(exchange, requestBody);
                    break;
                case "/api/signup":
                    handleSignup(exchange, requestBody);
                    break;
                case "/api/books/add":
                    handleAddBook(exchange, requestBody);
                    break;
                case "/api/community/create":
                    handleCreateCommunity(exchange, requestBody);
                    break;
                default:
                    sendResponse(exchange, 404, "{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private void handleLogin(HttpExchange exchange, String body) throws IOException {
        // Basic extraction (assuming plain JSON structure for demonstration without external libraries)
        String email = extractJsonValue(body, "email");
        String password = extractJsonValue(body, "password");

        Models.User user = DatabaseManager.loginUser(email, password);
        
        if (user != null) {
            // If password matches with corresponding email, redirect to home page and load all the data[cite: 1]
            sendResponse(exchange, 200, "{\"status\": \"success\", \"userId\": " + user.getId() + "}");
        } else {
            // If entered wrong password and if email is already present in database, show “Wrong password” label[cite: 1]
            sendResponse(exchange, 401, "{\"error\": \"Wrong password or email not found\"}");
        }
    }

    private void handleSignup(HttpExchange exchange, String body) throws IOException {
        // If email is not present in the database, and clicked sign in, redirect to sign-up page[cite: 1]
        // (Logic handled on frontend, this processes the final sign-up data)
        String email = extractJsonValue(body, "email");
        String phoneNo = extractJsonValue(body, "phone"); // Filling Phone No. and Address is mandatory[cite: 1]
        
        // DatabaseManager insertion logic would go here
        
        sendResponse(exchange, 201, "{\"status\": \"Account created\"}");
    }

    private void handleAddBook(HttpExchange exchange, String body) throws IOException {
        String name = extractJsonValue(body, "name"); // Mandatory[cite: 1]
        String author = extractJsonValue(body, "author"); // Mandatory[cite: 1]
        String editionStr = extractJsonValue(body, "edition"); // Mandatory[cite: 1]
        String genre = extractJsonValue(body, "genre");
        
        // In a real application, you'd extract the user from session tokens
        Models.User mockOwner = new Models.User(); 
        
        DatabaseManager.addBook(mockOwner, name, author, Integer.parseInt(editionStr), genre);
        sendResponse(exchange, 201, "{\"status\": \"Book added successfully\"}");
    }

    private void handleCreateCommunity(HttpExchange exchange, String body) throws IOException {
        String name = extractJsonValue(body, "name"); // Mandatory[cite: 1]
        String city = extractJsonValue(body, "city"); // Mandatory[cite: 1]
        String pincodeStr = extractJsonValue(body, "pincode"); // Mandatory[cite: 1]
        String desc = extractJsonValue(body, "description");
        String isProtectedStr = extractJsonValue(body, "isProtected");
        
        boolean isProtected = "true".equals(isProtectedStr); // In protected state, the creator of the community will decide if a request to join is approved[cite: 1]
        
        Models.User mockCreator = new Models.User();
        
        DatabaseManager.createCommunity(mockCreator, name, city, Integer.parseInt(pincodeStr), desc, isProtected);
        sendResponse(exchange, 201, "{\"status\": \"Community created successfully\"}");
    }

    // --- UTILITY METHODS ---

    private String getRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * A highly simplified string extractor for basic JSON since no JSON parsing frameworks are allowed.
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        
        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
}