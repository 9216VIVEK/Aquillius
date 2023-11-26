package com.aquillius.portal.util;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "@#";

    public static String generatePassword(int length) {
        String allCharacters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;

        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one of each type of character
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password with random characters
        for (int i = 4; i < length; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        return password.toString();
    }
}

