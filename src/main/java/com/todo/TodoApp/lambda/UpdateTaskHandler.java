package com.todo.TodoApp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.TodoApp.Task;
import com.todo.TodoApp.TaskRepository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.HashMap;
import java.util.Map;

public class UpdateTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final TaskRepository repository;
    private final ObjectMapper objectMapper;

    public UpdateTaskHandler() {
        DynamoDbEnhancedClient dbClient = DynamoDbEnhancedClient.create();
        this.repository = new TaskRepository(dbClient);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Create headers map with CORS headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");

        // Handle OPTIONS request for CORS preflight
        if ("OPTIONS".equals(input.getHttpMethod())) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(headers);
        }

        try {
            String taskId = input.getPathParameters().get("id");
            Task updatedTask = objectMapper.readValue(input.getBody(), Task.class);

            return repository.findById(taskId)
                    .map(existingTask -> {
                        existingTask.setText(updatedTask.getText());
                        existingTask.setCompleted(updatedTask.isCompleted());
                        Task saved = repository.save(existingTask);
                        try {
                            return new APIGatewayProxyResponseEvent()
                                    .withStatusCode(200)
                                    .withHeaders(headers)
                                    .withBody(objectMapper.writeValueAsString(saved));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(new APIGatewayProxyResponseEvent()
                            .withStatusCode(404)
                            .withHeaders(headers)
                            .withBody("Task not found"));

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(headers)
                    .withBody("Error processing request: " + e.getMessage());
        }
    }
}
