# Serverless Todo Application

This is a serverless implementation of the Todo application using AWS Lambda and DynamoDB.

## Architecture

The application uses:
- AWS Lambda for serverless compute
- Amazon DynamoDB for data storage
- Amazon API Gateway for REST API endpoints
- AWS SAM for infrastructure as code

## API Endpoints

The application exposes the following REST endpoints:

- `GET /tasks` - Get all tasks (optional query param: filter=all|active|completed)
- `POST /tasks` - Create a new task
- `PUT /tasks/{id}` - Update an existing task
- `DELETE /tasks/{id}` - Delete a task
- `DELETE /tasks/completed` - Delete all completed tasks

## Prerequisites

- AWS CLI installed and configured
- AWS SAM CLI installed
- Java 11 or later
- Maven

## Building the Application

```bash
mvn clean package
```

## Deploying the Application

1. First build the application:
```bash
mvn clean package
```

2. Deploy using SAM:
```bash
sam deploy --guided
```

Follow the prompts to complete the deployment.

## Testing

The application includes unit tests that can be run with:
```bash
mvn test
```

## DynamoDB Schema

The application uses a single DynamoDB table with the following schema:

- Table Name: `Todos`
- Partition Key: `id` (String)
- Attributes:
  - `text` (String)
  - `completed` (Boolean)

## Lambda Functions

The application is composed of the following Lambda functions:

1. `GetTasksFunction` - Handles retrieving tasks with optional filtering
2. `CreateTaskFunction` - Handles task creation
3. `UpdateTaskFunction` - Handles task updates
4. `DeleteTaskFunction` - Handles task deletion
5. `DeleteCompletedTasksFunction` - Handles deletion of all completed tasks

Each function is implemented as a separate handler class in the `com.todo.TodoApp.lambda` package.