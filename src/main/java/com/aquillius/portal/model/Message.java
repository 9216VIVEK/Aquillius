package com.aquillius.portal.model;

import lombok.Data;

@Data
public class Message {

    int code;

    String message;

    public Message() {
    }

    public Message(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
