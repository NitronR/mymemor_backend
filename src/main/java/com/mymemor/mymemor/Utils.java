package com.mymemor.mymemor;

import java.util.Base64;

public abstract class Utils {

    public static String encryptPassword(String password) {
        // TODO : improve encrpt password logic
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    public static String decryptPassword(String encodedPassword) {
        // TODO : improve decrpt password logic
        byte[] actualByte = Base64.getDecoder().decode(encodedPassword);
        return new String(actualByte);
    }

    public static boolean validRegistration() {
        // TODO: verify registration form
        return true;
    }

    public static boolean validateUsername(String username) {
        // TODO : validate username
        return true;
    }

    public static boolean validateName(String query) {
        // TODO : validate name
        return true;
    }
}
