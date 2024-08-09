# GitHub Repository Service Documentation

## Overview

This project provides a RESTful API to fetch GitHub repository and branch information for a given user. It uses Quarkus, a Kubernetes-native Java stack tailored for OpenJDK HotSpot and GraalVM, to build and run the application.

## Project Structure

### `GitRepositoryProxy`

This interface defines the REST client for interacting with the GitHub API.

```java
package org.task;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@Path("/")
@RegisterRestClient(baseUri = "https://api.github.com")
public interface GitRepositoryProxy {

    @GET
    @Path("/users/{username}/repos")
    @Produces(MediaType.APPLICATION_JSON)
    List<RepositoryModel> getRepositories(@PathParam("username") String username);

    @GET
    @Path("/repos/{owner}/{repoName}/branches")
    @Produces(MediaType.APPLICATION_JSON)
    List<BranchModel> getBranches(@PathParam("owner") String owner, @PathParam("repoName") String repo);
}
```

### `GitRepositoryService`

This service class handles the business logic for fetching repository and branch data from GitHub.

```java
package org.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.task.model.BranchesModel;
import org.task.model.LastCommit;
import org.task.model.ResponseModel;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GitRepositoryService {

    @Inject
    @RestClient
    GitRepositoryProxy gitRepositoryProxy;

    ArrayList<ResponseModel> getData(String username) throws Exception {
        ArrayList<BranchesModel> branches = new ArrayList<>();
        ArrayList<ResponseModel> responseModels = new ArrayList<>();

        ArrayList<JsonNode> responses = gitRepositoryProxy.getRepositories(username);

        for (JsonNode response : responses) {
            String repoName = response.get("name").asText();
            boolean isFolked = response.get("fork").asBoolean();
            ArrayList<JsonNode> branchesResponse = gitRepositoryProxy.getBranches(username, repoName);

            if (!isFolked) {
                for (JsonNode branch : branchesResponse) {
                    branches.add(new BranchesModel(branch.get("name").asText(), new LastCommit(branch.get("commit").get("sha").asText())));
                }
                responseModels.add(new ResponseModel(username, repoName, branches));
            }
        }
        return responseModels;
    }
}
```

### `GitRepositoryResource`

This resource class exposes the RESTful endpoints to the clients.

```java
package org.task;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.task.model.NotFoundModel;
import org.task.model.ResponseModel;

import java.util.ArrayList;

@Path("/github")
@Produces(MediaType.APPLICATION_JSON)
public class GitRepositoryResource {

    @Inject
    GitRepositoryService gitRepositoryService;

    @GET
    @Path("/{username}")
    public Response getRepositories(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new NotFoundModel(Response.Status.BAD_REQUEST.getStatusCode(), "Username is required")).build();
        }
        try {
            ArrayList<ResponseModel> responseModels = gitRepositoryService.getData(username);
            return Response.ok(responseModels).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new NotFoundModel(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage())).build();
        }
    }
}
```

### `NotFoundModel`

This model class represents the error response when a resource is not found.

```java
package org.task.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotFoundModel {
    @JsonProperty
    private int status;

    @JsonProperty
    private String message;

    public NotFoundModel(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
```

### `application.properties`

Configuration file for the Quarkus application.

```ini
quarkus.http.port=8085
```

## Running the Application

1. **Build the project**: Use Maven to build the project.
   ```sh
   mvn clean install
   ```

2. **Run the application**: Start the Quarkus application.
   ```sh
   mvn quarkus:dev
   ```

3. **Access the API**: The application will be running on port `8085`. You can access the API at `http://localhost:8085/github/{username}`.

## API Endpoints

### Get Repositories

- **URL**: `/github/{username}`
- **Method**: `GET`
- **Path Parameters**:
  - `username` (string): GitHub username.
- **Responses**:
  - `200 OK`: Returns a list of repositories and their branches.
  - `400 Bad Request`: If the username is not provided.
  - `404 Not Found`: If the user or repositories are not found.

## Error Handling

- **400 Bad Request**: Returned when the username is not provided.
- **404 Not Found**: Returned when the user or repositories are not found.

## Dependencies

- **Quarkus**: Framework for building Java applications.
- **RESTEasy Reactive**: For building RESTful web services.
- **Jackson**: For JSON processing.
- **MicroProfile Rest Client**: For making RESTful web service calls.

## Conclusion

This project demonstrates how to build a RESTful API using Quarkus to interact with the GitHub API. It includes error handling and proper response formatting to ensure a robust and user-friendly API.
