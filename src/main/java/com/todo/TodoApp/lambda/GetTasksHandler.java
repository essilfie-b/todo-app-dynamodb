package com.todo.TodoApp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.TodoApp.Task;
import com.todo.TodoApp.TaskRepository;
import org.crac.Core;
import org.crac.Resource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTasksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>, Resource {
    private TaskRepository repository;
    private final ObjectMapper objectMapper;

    public GetTasksHandler() {
        Core.getGlobalContext().register(this);
        initializeResources();
        this.objectMapper = new ObjectMapper();
    }

    private void initializeResources() {
        DynamoDbEnhancedClient dbClient = DynamoDbEnhancedClient.create();
        repository = new TaskRepository(dbClient);
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
            Map<String, String> queryParams = input.getQueryStringParameters();
            String filter = queryParams != null ? queryParams.get("filter") : "all";

            List<Task> tasks;
            if ("active".equalsIgnoreCase(filter)) {
                tasks = repository.findByCompleted(false);
            } else if ("completed".equalsIgnoreCase(filter)) {
                tasks = repository.findByCompleted(true);
            } else {
                tasks = repository.getAllTasks();
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(headers)
                    .withBody(objectMapper.writeValueAsString(tasks));

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(headers)
                    .withBody("Error processing request: " + e.getMessage());
        }
    }

    @Override
    public void beforeCheckpoint(org.crac.Context<? extends Resource> context) throws Exception {

    }

    @Override
    public void afterRestore(org.crac.Context<? extends Resource> context) throws Exception {
        initializeResources();
    }
}
