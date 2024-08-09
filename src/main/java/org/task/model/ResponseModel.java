package org.task.model;

import java.util.List;

public class ResponseModel {
    public String repositoryName;
    public String owner_login;
    public List<BranchesModel> branches;

    public ResponseModel(String repositoryName, String owner_login, List<BranchesModel> branches){
        this.repositoryName = repositoryName;
        this.owner_login = owner_login;
        this.branches = branches;
    }

}
