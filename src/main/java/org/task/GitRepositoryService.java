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

           if(!isFolked){
               for (JsonNode branch : branchesResponse) {
                   branches.add(new BranchesModel(branch.get("name").asText(), new LastCommit(branch.get("commit").get("sha").asText())));
               }
               responseModels.add(new ResponseModel(username, repoName, branches));
               branches = new ArrayList<>();
           }
        }
        return responseModels;
    }

}
