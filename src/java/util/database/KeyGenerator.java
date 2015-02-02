package cgwap.util.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cgwap.entities.User;

/**
 * This class is responsible for the generation of random token and IDs for the
 * database
 */
public class KeyGenerator {

    private static final Logger LOGGER = Logger.getLogger(User.class.getName());

    /**
     * The salt is the SHA512 Hash of a random token 
     */
    private static final String SALT   = hashSHA512("CGWAPBachelorLisaWagner", "");

    /**
     * To compute the hash of the password of the user, the password is hashed
     * together with the salt, to store it securely in database.
     * 
     * @param input
     *            is user's password
     * @return the hashedPassword
     */
    public static String hashUserPassword(String input) {
        return hashSHA512(input, SALT);
    }

    /**
     * Generate a long, random and unique token. SHA512 is used to get a wide
     * area of possible signs. SHA256 is used so the ticketID is representable
     * on the ticket.Especially used for ticket- and pool-token.
     * 
     * @return the hashed generated Token
     */
    public static String generateToken() {
        String time = String.valueOf(System.nanoTime());
        String token = String.valueOf((new Random()).nextLong());
        return hashSHA256(time, hashSHA512(token, ""));
    }
    
//    /**
//     * Generates a new random token, especially for verifying addresses or
//     * setting back passwords
//     * 
//     * @param entity
//     *            the Token entity to generate the new random token for
//     * @return the generated random token
//     * @throws ApplicationException
//     *             if hashing failed
//     */
//    public static String generateTokenForTokenTable(Token entity) throws ApplicationException {
//        String salt = hashSHA256(String.valueOf(entity.getUserId()), "");
//        Random rand = new Random();
//        String randomNumber = String.valueOf(rand.nextInt());
//        return hashSHA256(randomNumber, salt);
//    }

    /**
     * Hashes a String with SHA512. Returns the output in hex code.
     * 
     * @param input
     *            the String to hash
     * @return the generated hash
     * @throws IllegalArgumentException
     *             if the input is null or empty
     */
    private static String hashSHA512(String input, String salt) {
        // prepare message digest
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) { // should never happen
            LOGGER.log(Level.SEVERE, "SHA-512 is an unknown algorithm for message digest. ", e);
            return null;
        }

        // hash input
        byte[] hash = md.digest((input + salt).getBytes());

        return convertToHexString(hash);
    }
    
    /**
     * Hashes a given String using SHA256. Returns the output in hexadecimal code. 
     * 
     * @param input the string to hash
     * @param salt the salt to use
     * @return the generated hash
     */
    private static String hashSHA256(String input, String salt) {
        // prepare message digest
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) { // should never happen
            LOGGER.log(Level.SEVERE, "SHA-256 is an unknown algorithm for message digest. ", e);
            return null;
        }

        // hash input
        byte[] hash = md.digest((input + salt).getBytes());

        return convertToHexString(hash);
    }

    /**
     * Converts a given byte array to a hexadecimal string.
     * 
     * @param hash the byte array to convert
     * @return the converted hexadecimal string
     */
    private static String convertToHexString(byte[] hash) {
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                output.append("0");
                output.append(Integer.toHexString((0xFF & hash[i])));
            } else {
                output.append(Integer.toHexString(0xFF & hash[i]));
            }
        }

        return output.toString();
    }
}
