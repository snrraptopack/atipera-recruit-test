package org.task;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;

@Path("/")
@RegisterRestClient(baseUri = "https://api.github.com")
public interface GitRepositoryProxy {

    @GET
    @Path("/users/{username}/repos")
    @Produces(MediaType.APPLICATION_JSON)
    ArrayList<JsonNode> getRepositories(@PathParam("username")String username) throws Exception ;

    @GET
    @Path("/repos/{owner}/{repoName}/branches")
    @Produces(MediaType.APPLICATION_JSON)
    ArrayList<JsonNode> getBranches(@PathParam("owner") String owner, @PathParam("repoName") String repo) ;
}