package com.aquillius.portal.model;

import lombok.Data;

/**
 * TokenWrapper is a model object representing the token received from the front end.
 */
@Data
public class TokenWrapper {

    private String token;
    private String idempotencyKey;

}