package com.aquillius.portal.service;

import com.aquillius.portal.entity.User;
import com.aquillius.portal.entity.VerificationToken;

public interface VerificationTokenService {


    VerificationToken createVerificationToken(User user);

    VerificationToken findByToken(String token);

    void delete(VerificationToken token);

    VerificationToken findTokenByUser(User user);
}
