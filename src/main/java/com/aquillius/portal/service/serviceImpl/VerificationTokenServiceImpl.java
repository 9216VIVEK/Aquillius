package com.aquillius.portal.service.serviceImpl;

import com.aquillius.portal.entity.User;
import com.aquillius.portal.entity.VerificationToken;
import com.aquillius.portal.repository.VerificationTokenRepository;
import com.aquillius.portal.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationToken createVerificationToken(User user) {
        log.info("=============inside createVerificationToken VerificationTokenServiceImpl=============");
        VerificationToken verificationToken = new VerificationToken();
        String token = generateToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        System.out.println("1111111111111111111111111111111");
        tokenRepository.save(verificationToken);
        System.out.println("222222222222222222");
        return verificationToken;
    }

    private String generateToken() {
        String digits = "0123456789";

        Random random = new SecureRandom();
        StringBuilder token = new StringBuilder();

        token.append(digits.charAt(random.nextInt(digits.length())));

        for (int i = 1; i < 6; i++) {
            token.append(digits.charAt(random.nextInt(digits.length())));
        }
        return token.toString();
    }

    public VerificationToken findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void delete(VerificationToken token) {
        tokenRepository.delete(token);
    }


    public VerificationToken findTokenByUser(User user) {
        return tokenRepository.findByUser(user);
    }
}
