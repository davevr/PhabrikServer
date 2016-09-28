package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.AccessObj;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.SolSysObj;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Dave on 9/27/2016.
 */
public class AccessDAO {

        private static final Logger log = Logger.getLogger(AccessDAO.class.getName());

        public static AccessObj CreateFromRS(ResultSet rs) {
            AccessObj newObj = new AccessObj();
            try {
                newObj.playerid = rs.getLong("playerid");
                newObj.itemid = rs.getLong("itemid");
                newObj.known = rs.getBoolean("known");
            } catch (SQLException sqlexp) {
                log.log(Level.SEVERE, sqlexp.getMessage());
                newObj = null;
            }


            return newObj;
        }





        public static List<AccessObj> FetchForSolSys(long playerId, long solSysId) {
            List<AccessObj> accessList = new ArrayList<>();

            try {

                Connection conn = DBHelper.GetConnection();
                if (conn != null) {

                    CallableStatement statement = conn.prepareCall("{call getaccessforsolsysidforplayer(?, ?)}");
                    statement.setLong(1, playerId   );
                    statement.setLong(1, solSysId);
                    ResultSet rs  = statement.executeQuery();
                    while (rs.next()) {
                        AccessObj newObj = AccessDAO.CreateFromRS(rs);
                        if (newObj != null)
                            accessList.add(newObj);
                    }

                } else {
                    log.log(Level.SEVERE, "No connection");
                }

            } catch (SQLException sqlexp) {
                log.log(Level.SEVERE, sqlexp.getMessage());

            } finally {
                DBHelper.ReleaseConnection();
            }

            return accessList;
        }

    public static AccessObj FetchForPlanet(long playerId, long planetId) {
        AccessObj accessObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String theStr = "SELECT * from phabrikobjects.planetaccess WHERE itemid = ? and (playerid = ? or playerid = -1)";
                PreparedStatement statement = conn.prepareStatement(theStr);
                statement.setLong(1, planetId);
                statement.setLong(2, playerId   );

                ResultSet rs  = statement.executeQuery();
                if (rs.next()) {
                    accessObj = AccessDAO.CreateFromRS(rs);
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return accessObj;
    }

    public static List<AccessObj> FetchForSolSysByLoc(long playerId, int xloc, int yloc, int zloc) {
        List<AccessObj> accessList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                CallableStatement statement = conn.prepareCall("{call getaccessforsolsysforplayer(?, ?, ?, ?)}");
                statement.setLong(1, playerId   );
                statement.setInt(2, xloc);
                statement.setInt(3, yloc);
                statement.setInt(4, zloc);
                ResultSet rs  = statement.executeQuery();
                while (rs.next()) {
                    AccessObj newObj = AccessDAO.CreateFromRS(rs);
                    if (newObj != null)
                        accessList.add(newObj);
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return accessList;
    }

        public static List<AccessObj> FetchForGalaxy(long playerId) {
            List<AccessObj> accessList = new ArrayList<>();

            try {

                Connection conn = DBHelper.GetConnection();
                if (conn != null) {

                    CallableStatement statement = conn.prepareCall("{call phabrikobjects.getaccesssforgalaxyforplayer(?)}");
                    statement.setLong(1, playerId   );
                    ResultSet rs  = statement.executeQuery();
                    while (rs.next()) {
                        AccessObj newObj = AccessDAO.CreateFromRS(rs);
                        if (newObj != null)
                            accessList.add(newObj);
                    }

                } else {
                    log.log(Level.SEVERE, "No connection");
                }

            } catch (SQLException sqlexp) {
                log.log(Level.SEVERE, sqlexp.getMessage());

            } finally {
                DBHelper.ReleaseConnection();
            }

            return accessList;
        }

    public static List<SolSysObj> FetchKnownSystems(long playerId) {
        List<SolSysObj> accessList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                CallableStatement statement = conn.prepareCall("{call phabrikobjects.getknownsystems(?)}");
                statement.setLong(1, playerId   );
                ResultSet rs  = statement.executeQuery();
                while (rs.next()) {
                    SolSysObj newObj = SolSysDAO.CreateFromRS(rs, false);
                    if (newObj != null)
                        accessList.add(newObj);
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return accessList;
    }



    public static void SetUserPlanetAccess(long playerId, long planetId, boolean isknown) {
            try {
                String queryStr = "INSERT INTO phabrikobjects.planetaccess (playerid, planetid, known) " +
                        "VALUES (?,?,?) " +
                        "on duplicate key update known=?";

                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

                statement.setLong(1, playerId);
                statement.setLong(2, planetId);
                statement.setBoolean(3,isknown);
                statement.setBoolean(4, isknown);

                statement.executeUpdate();
                int updatedRows = statement.getUpdateCount();
                statement.close();

            } catch (Exception exp) {
                log.log(Level.SEVERE, exp.getMessage());
            }
        }

    public static void SetUserSolSysAccess(long playerId, long solSysId, boolean isknown) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.solsysaccess (playerid, solsysid, known) " +
                    "VALUES (?,?,?) " +
                    "on duplicate key update known=?";

            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setLong(1, playerId);
            statement.setLong(2, solSysId);
            statement.setBoolean(3,isknown);
            statement.setBoolean(4,isknown);

            statement.executeUpdate();
            int updatedRows = statement.getUpdateCount();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }


    }
