# Serverless Todo Application

This is a serverless implementation of the Todo application using AWS Lambda and DynamoDB.

## Architecture

The application uses:
- AWS Lambda for serverless compute
- AWS Lambda Layers for sharing common dependencies
- Amazon DynamoDB for data storage
- Amazon API Gateway for REST API endpoints
- AWS SAM for infrastructure as code

The application uses a Lambda Layer to share common dependencies across all functions, including:
- AWS SDK for DynamoDB
- DynamoDB Enhanced Client
- Jackson for JSON processing

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

The application consists of two components:
1. Lambda Layer containing common dependencies
2. Main application code

To build both components, use the provided build script:

```bash
chmod +x build.sh
./build.sh
```

Or build them separately:

```bash
# Build Lambda Layer
cd layer
mvn clean package
cd ..

# Build main application
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

