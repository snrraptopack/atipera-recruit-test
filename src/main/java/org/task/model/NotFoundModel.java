package org.task.model;

public class NotFoundModel {
    public int status;
    public String message;

    public NotFoundModel(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
