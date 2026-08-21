import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException {
        // Initialize the server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve static frontend files (HTML, CSS, JS) from the public directory
        server.createContext("/", new StaticFileHandler());

        // Route all backend data requests to the single API handler
        // (ApiHandler.java will be created next)
        server.createContext("/api/", new ApiHandler());

        // Use a thread pool to handle concurrent incoming requests
        server.setExecutor(Executors.newCachedThreadPool());
        
        System.out.println("Starting server on http://localhost:8080...");
        server.start();

        // Initialize the background task scheduler for extreme conditions[cite: 1]
        startConditionScheduler();
    }

    /**
     * Schedules the background checks required for transactions.
     */
    private static void startConditionScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // Run this check every hour to enforce deadlines[cite: 1]
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Running scheduled condition checks...");
                
                // Logic to be implemented in DatabaseManager:
                // 1. Check 48 hr window for exchange[cite: 1].
                // 2. Send notification every 6 hours if any party has not acknowledged after exchange[cite: 1].
                // 3. Send notification to lender every 12 hours if lender acknowledged but borrower has not[cite: 1].
                // 4. Send notification to borrower every 12 hours if borrower acknowledged but lender has not[cite: 1].
                // 5. Make book available if borrower goes inactive for 5 days straight[cite: 1].
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    /**
     * A plain HTTP handler to serve static files (HTML, CSS, JS) to the browser.
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html if root is requested
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Secure the path to only read from the 'public' directory
            File file = new File("public" + path);
            
            if (file.exists() && !file.isDirectory()) {
                // Determine Content-Type
                String contentType = "text/plain";
                if (path.endsWith(".html")) contentType = "text/html";
                else if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                
                try (OutputStream os = exchange.getResponseBody()) {
                    Files.copy(file.toPath(), os);
                }
            } else {
                // Return 404 if file is not found
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
}