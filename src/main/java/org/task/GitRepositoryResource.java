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
public class GitRepositoryResource  {

    @Inject
    GitRepositoryService gitRepositoryService;

    @GET
    @Path("/{username}")
    public Response getRepositories(@PathParam("username") String username){
       if(username == null || username.isEmpty()){
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
