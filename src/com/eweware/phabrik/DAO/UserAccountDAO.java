package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.UserAccountObj;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class UserAccountDAO {
    private static final Logger log = Logger.getLogger(UserAccountDAO.class.getName());

    public static UserAccountObj CreateFromRS(ResultSet rs)  {
        UserAccountObj newUser = new UserAccountObj();
        try {

            newUser.Id = rs.getLong("id");
            newUser.U =  rs.getString("U");
            newUser.S =  rs.getString("S");
            newUser.D =  rs.getString("D");
            newUser.c =  new DateTime(rs.getTimestamp("c"));//rs.getDate("c");

        }
        catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newUser = null;
        }

        return newUser;
    }

    public static UserAccountObj CreateNewUserAccount(String userName, String pwd)  {
        UserAccountObj newUser = null;
        if (AccountExists(userName)) {
            log.severe("account name already exists");
        } else {
            newUser = new UserAccountObj();
            // create the new user
            newUser.U = userName;
            newUser.c = new DateTime();
            Authenticator.SaltUserAccount(newUser, pwd);

            InsertNewObjIntoDB(newUser);
        }

        return newUser;
    }

    private static void InsertNewObjIntoDB(UserAccountObj newUser) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.useraccounts (U, S, D, c) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setString(1, newUser.U);
            statement.setString(2,newUser.S);
            statement.setString(3, newUser.D);
            statement.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newUser.Id = rs.getLong(1);
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }

    public static UserAccountObj FetchByName(String userName) {
        UserAccountObj userAccount = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.useraccounts WHERE U = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setString(1, userName.toLowerCase());

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    userAccount = UserAccountDAO.CreateFromRS(newRs);
                }
            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return userAccount;
    }

    private static Boolean AccountExists(String userName) {
        Boolean exists = false;
        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.useraccounts WHERE U = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setString(1, userName.toLowerCase());

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    exists = true;
                }
            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return exists;
    }
}
