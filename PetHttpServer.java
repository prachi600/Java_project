public import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

public class PetHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/pets", new PetApiHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        System.out.println("Backend Server running on http://localhost:8080/api/pets");
        server.start();
    }

    static class PetApiHandler implements HttpHandler {
        private final PetDAO petDAO = new PetDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    List<Pet> pets = petDAO.findAll();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < pets.size(); i++) {
                        Pet p = pets.get(i);
                        json.append(String.format(
                            "{\"id\":%d, \"name\":\"%s\", \"species\":\"%s\", \"breed\":\"%s\", \"age\":\"%s\", \"status\":\"%s\"}",
                            p.getId(), p.getName(), p.getSpecies(), p.getBreed(), p.getAge(), p.getStatus()
                        ));
                        if (i < pets.size() - 1) json.append(",");
                    }
                    json.append("]");

                    byte[] response = json.toString().getBytes();
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                }
            }
        }
    }
} {
    
}
