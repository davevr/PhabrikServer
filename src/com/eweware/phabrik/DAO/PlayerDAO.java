package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.UserAccountObj;
import org.joda.time.DateTime;

import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class PlayerDAO {
    private static final Logger log = Logger.getLogger(PlayerDAO.class.getName());

    public static PlayerObj CreateFromRS(ResultSet rs) {
        PlayerObj newUser = new PlayerObj();
        try {

            newUser.Id = rs.getLong("Id");
            newUser.playerName = rs.getString("Name");
            newUser.isAdmin = rs.getBoolean("isAdmin");
            newUser.lastlogin = new DateTime(rs.getTimestamp("lastlogin"));
        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newUser = null;
        }

        if (newUser != null) {
            // inflate secondary structures
            newUser.popList = PointOfPresenceDAO.FetchForUser(newUser.Id);
            
        }

        return newUser;
    }

    public static PlayerObj GetCurrentUser(HttpSession theSession) {
        PlayerObj theUser = null;

        Long userId = Authenticator.CurrentUserId(theSession);

        if (userId != null)
            theUser = FetchByID(userId);

        return theUser;
    }

    public static PlayerObj FetchByID(Long userId) {
        PlayerObj newUser = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.players WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, userId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newUser = PlayerDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "User not found");
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newUser;
    }

    public static PlayerObj FetchByName(String userName) {
        PlayerObj newUser = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.players WHERE Name = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setString(1, userName.toLowerCase());

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newUser = PlayerDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "User not found");
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newUser;
    }

    public static PlayerObj CreateNewUser(String userName, String pwd)  {
        UserAccountObj newUserAccount = UserAccountDAO.CreateNewUserAccount(userName, pwd);
        PlayerObj newUser = null;

        if (newUserAccount != null) {
            newUser = new PlayerObj();
            newUser.isAdmin = false;
            newUser.playerName = userName;

            InsertNewObjIntoDB(newUser);
        }


        return newUser;
    }

    private static void InsertNewObjIntoDB(PlayerObj newUser) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.players (Name, isAdmin, lastlogin) VALUES (?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setString(1, newUser.playerName);
            statement.setBoolean(2,newUser.isAdmin);
            statement.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));

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

}
