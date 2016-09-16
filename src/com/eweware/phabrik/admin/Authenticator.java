package com.eweware.phabrik.admin;

import com.eweware.phabrik.DAO.PlayerDAO;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.UserAccountObj;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class Authenticator {
    private static final Logger log = Logger.getLogger(Authenticator.class.getName());
    private static Authenticator instance = null;
    public static final String USERID = "userid";
    public static final String ISADMIN = "isadmin";
    private final static int ITERATIONS = 1000;
    private static final String TWO_WAY_CRYPT_METHOD = "PBEWithMD5AndDES";
    private static final char[] MASTER_PASSWORD = "23&-*/F43v02!s_83jJ@=a".toCharArray();
    private static final byte[] MASTER_SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    private static final String FBAppId = "439651239569547";
    private static final String FBSecret = "2671cc74281c6e342fef53467a7f7205";



    protected Authenticator() {
        // Exists only to defeat instantiation.
    }
    public static Authenticator getInstance() {
        if(instance == null) {
            instance = new Authenticator();
        }
        return instance;
    }



    public static String[] createSaltedPassword(final String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final byte[] bSalt = new byte[8];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(bSalt);

        final byte[] bDigest = getHash(ITERATIONS, password, bSalt);

        return new String[]{new String(Base64.encodeBase64(bDigest)), new String(Base64.encodeBase64(bSalt))};
    }

    public static boolean authenticate(String digest, String salt, final String password) throws Exception {
        final byte[] proposedDigest = getHash(ITERATIONS, password, Base64.decodeBase64(salt));

        return Arrays.equals(proposedDigest, Base64.decodeBase64(digest));
    }



    public static PlayerObj AuthenticateUser(HttpSession session, String userName, String userPwd, UserAccountObj accountInfo)
    {
        PlayerObj newUser = null;

        try {

            if (authenticate(accountInfo.D, accountInfo.S, userPwd)) {
                // we are in
                newUser = PlayerDAO.FetchByName(userName);
                LoginAuthenticatedUser(session, newUser);
            }
        }
        catch (Exception exp)
        {
            // authenticate failed
            return null;
        }

        return newUser;
    }

    public static PlayerObj LoginAuthenticatedUser(HttpSession session, PlayerObj newUser)
    {
        if (UserIsLoggedIn(session)) {
            Logout(session);
        }

        if (newUser != null) {
            session.setAttribute(USERID, newUser.Id);

            if (newUser.isAdmin) {
                session.setAttribute(ISADMIN, true);
            }
            log.log(Level.WARNING, "Session stored");
        }

        return newUser;
    }



    private static String hmacSHA256(String accessToken, String appSecret) throws Exception {
        try {
            byte[] key = appSecret.getBytes(Charset.forName("UTF-8"));
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] raw = mac.doFinal(accessToken.getBytes());
            byte[] hex = encodeHex(raw);
            return new String(hex, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException("Creation of appsecret_proof has failed", e);
        }
    }

    public static byte[] encodeHex(final byte[] data) {
        if (data == null)
            throw new NullPointerException("Parameter 'data' cannot be null.");

        final char[] toDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }

        return new String(out).getBytes(Charset.forName("UTF-8"));
    }

    public static void SaltUserAccount(UserAccountObj newAccount, String pwd) {
        try {
            String[] saltAndHash = createSaltedPassword(pwd);

            newAccount.D = saltAndHash[0];
            newAccount.S = saltAndHash[1];

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }



    public static long CurrentUserId(HttpSession session) {
        Object theId = session.getAttribute(USERID);
        if (theId != null)
            return (long)theId;
        else {
            log.log(Level.WARNING, "Fetching null session!");
            return 0;
        }
    }

    public static boolean UserIsAdmin(HttpSession session) {
        Object isAdmin = session.getAttribute(ISADMIN);
        if (isAdmin != null)
            return (boolean)isAdmin;
        else {
            log.log(Level.WARNING, "Fetching null session!");
            return false;
        }
    }



    public static Boolean UserIsLoggedIn(HttpSession session) {
        return session.getAttribute(USERID) != null;
    }

    public static Boolean Logout(HttpSession session)
    {
        if (session.getAttribute(USERID) != null) {
            // TODO:  update user's online status
            long userId = CurrentUserId(session);
            session.removeAttribute(USERID);
            session.removeAttribute(ISADMIN);
            return true;
        }
        else
            return false;

    }

    public static byte[] getHash(final int iterationNb, final String password, final byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }
}
