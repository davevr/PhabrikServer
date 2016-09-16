package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.StructureObj;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/13/16.
 */
public class PointOfPresenceDAO {
    private static final Logger log = Logger.getLogger(PointOfPresenceDAO.class.getName());

    public static PointOfPresenceObj CreateFromRS(ResultSet rs) {
        PointOfPresenceObj newObj = new PointOfPresenceObj();
        try {

            newObj.Id = rs.getLong("Id");
            newObj.playerId = rs.getLong("playerid");
            newObj.targetId = rs.getLong("structureid");
            newObj.created = new DateTime(rs.getTimestamp("created"));
            newObj.lastactive = new DateTime(rs.getTimestamp("lastactive"));

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            // inflate secondary structures
            newObj.structure = StructureDAO.FetchByID(newObj.targetId);

        }

        return newObj;
    }

    public static PointOfPresenceObj FetchByID(Long objId) {
        PointOfPresenceObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.PointOfPresence WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = PointOfPresenceDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Point of Presence not found");
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newObj;
    }

    public static List<PointOfPresenceObj> FetchForUser(long userId) {
        List<PointOfPresenceObj> popList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.PointOfPresence WHERE playerid = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, userId);

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    PointOfPresenceObj newObj = PointOfPresenceDAO.CreateFromRS(newRs);
                    if (newObj != null)
                        popList.add(newObj);
                }

                newRs.close();

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return popList;

    }
}
