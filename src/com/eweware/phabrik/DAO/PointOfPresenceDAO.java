package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.StructureObj;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/13/16.
 */
public class PointOfPresenceDAO {
    private static final Logger log = Logger.getLogger(PointOfPresenceDAO.class.getName());
    private static final long kDefaultStructureId = 1;

    public static PointOfPresenceObj CreateFromRS(ResultSet rs) {
        PointOfPresenceObj newObj = new PointOfPresenceObj();
        try {

            newObj.Id = rs.getLong("Id");
            newObj.playerId = rs.getLong("playerid");
            newObj.structureId = rs.getLong("structureid");
            newObj.created = new DateTime(rs.getTimestamp("created"));
            newObj.lastactive = new DateTime(rs.getTimestamp("lastactive"));

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            // inflate secondary structures
            newObj.structure = StructureDAO.FetchByID(newObj.structureId);

        }

        return newObj;
    }

    public static PointOfPresenceObj FetchByID(Long objId) {
        PointOfPresenceObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.pointofpresence WHERE Id = ?";
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

    public static PointOfPresenceObj CreateForUser(long playerId) {
        PointOfPresenceObj newPop = new PointOfPresenceObj();

        newPop.playerId = playerId;
        newPop.created = DateTime.now();
        newPop.lastactive = DateTime.now();
        newPop.structureId = PointOfPresenceDAO.kDefaultStructureId;
        InsertNewObjIntoDB(newPop);

        return newPop;
    }

    public static List<PointOfPresenceObj> FetchForUser(long userId) {
        List<PointOfPresenceObj> popList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.pointofpresence WHERE playerid = ?";
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

    public static void InsertNewObjIntoDB(PointOfPresenceObj newPop) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.pointofpresence (playerid, structureid, created, lastactive )" +
                    " VALUES (?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            long structureId = newPop.structureId;
            if (newPop.structure != null)
                structureId = newPop.structure.Id;

            Timestamp ts = new Timestamp(new java.util.Date().getTime());

            statement.setDouble(1, newPop.playerId);
            statement.setDouble(2,structureId);
            statement.setTimestamp(3,  ts);
            statement.setTimestamp(4,ts);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newPop.Id = rs.getLong(1);
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
