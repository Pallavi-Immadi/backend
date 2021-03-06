package controllers;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

public class Utils {

    static public String generateSalt() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 9) { // length of the random string.

            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));

        }

        String saltStr = salt.toString();
        play.Logger.debug(saltStr);
        return saltStr;

    }

    public static String generateHashedPassword(String password, String salt, int iteration) throws NoSuchAlgorithmException {

        String saltPwd = salt.concat(password);

        MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        final byte[] hash = mDigest.digest(saltPwd.getBytes(StandardCharsets.UTF_8));

        StringBuffer hexString = new StringBuffer();

        for(int i=0; i< iteration; i++){

            String hex = Integer.toHexString(0xff & hash[i] );
            hexString.append(hex);
        }
        return hexString.toString();
    }


    static public String generateToken() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 18) { // length of the random string.

            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));

        }

        String saltStr = salt.toString();
        play.Logger.debug("token is"+saltStr);
        return saltStr;

    }

    static public Long generateThreshold() {

        long generatedTime = Calendar.getInstance().getTimeInMillis();
        long threshold=generatedTime + 1500000;       //setting 25 minutes as the threshold time

        return threshold;
    }


}
