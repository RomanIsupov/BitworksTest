package ru.romanisupov;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements HttpHandler {
    private final Worker worker;
    private int currentId;

    public RequestHandler(Worker worker) {
        this.worker = worker;
        currentId = 1;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> parameters = extractParameters(exchange.getRequestURI().getQuery());
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        if (parameters.size() == 2 && parameters.get("concurrency") != null && parameters.get("sort") != null) {
            Map<String, String> response = addJobToQueue(
                    new Job(currentId,
                            Integer.parseInt(parameters.get("concurrency")),
                            parameters.get("sort"))
            );
            currentId++;
            sendResponse(exchange, response.toString().getBytes(StandardCharsets.UTF_8), 200);
        }
        else if (parameters.size() == 1 && parameters.get("get") != null) {
            Map<String, String> response = getJobState(Integer.parseInt(parameters.get("get")));
            sendResponse(exchange, response.toString().getBytes(StandardCharsets.UTF_8), 200);
        }
        else {
            sendResponse(exchange, "Bad request".getBytes(StandardCharsets.UTF_8), 400);
        }
    }

    private Map<String, String> addJobToQueue(Job job) {
        Map<String, String> response = new HashMap<>();
        worker.add(job);
        if (!worker.isRunning()) {
            worker.run();
        }
        response.put("jobId", String.valueOf(job.getId()));
        return response;
    }

    private Map<String, String> getJobState(int jobId) {
        Map<String, String> response = new HashMap<>();
        JobState state = worker.getJobState(jobId);
        response.put("state", state.name());
        int[] data = state == JobState.READY ? FileWorker.readArrayFromFile(worker.getReadyFilePath(jobId)) : new int[0];
        response.put("data", Arrays.toString(data));
        return response;
    }

    private void sendResponse(HttpExchange exchange, byte[] response, int statusCode) {
        try {
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response);
            responseBody.flush();
            responseBody.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> extractParameters(String query) {
        Map<String, String> queryParameters = new HashMap<>();
        String[] split = query.split("&");
        for (String part : split) {
            String key = part.split("=")[0];
            String value = part.split("=")[1];
            queryParameters.put(key, value);
        }
        return queryParameters;
    }
}
