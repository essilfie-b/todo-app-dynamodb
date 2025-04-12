package com.todo.TodoApp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.todo.TodoApp.Task;
import com.todo.TodoApp.TaskRepository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteCompletedTasksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final TaskRepository repository;

    public DeleteCompletedTasksHandler() {
        DynamoDbEnhancedClient dbClient = DynamoDbEnhancedClient.create();
        this.repository = new TaskRepository(dbClient);
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
            List<Task> completedTasks = repository.findByCompleted(true);
            repository.deleteAll(completedTasks);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(204)
                    .withHeaders(headers);

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(headers)
                    .withBody("Error processing request: " + e.getMessage());
        }
    }
}
