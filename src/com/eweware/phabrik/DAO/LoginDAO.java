package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.UserAccountObj;
import org.joda.time.DateTime;

import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class LoginDAO {
    private static final Logger log = Logger.getLogger(LoginDAO.class.getName());

    public static PlayerObj LoginUser(HttpSession session, String userName, String pwd) {
        PlayerObj theObj = null;

        UserAccountObj userAccount = UserAccountDAO.FetchByName(userName);

        if (userAccount != null) {
            theObj = Authenticator.AuthenticateUser(session, userName, pwd, userAccount);
        }

        return theObj;
    }

    public static PlayerObj CreateUserAndLogin(HttpSession session, String userName, String pwd) {
        PlayerObj theObj = null;
        UserAccountObj userAccount = UserAccountDAO.FetchByName(userName);

        if (userAccount != null) {
            log.severe("User account already exists!");
            theObj = null;
        } else {
            theObj = PlayerDAO.CreateNewUser(userName, pwd);

            if (theObj != null) {
                theObj = Authenticator.LoginAuthenticatedUser(session, theObj);
            }

        }

        return theObj;
    }
}
