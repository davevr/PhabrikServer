package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.PlayerObj;
import com.eweware.phabrik.obj.SolSysObj;
import com.eweware.phabrik.obj.SunObj;
import com.eweware.phabrik.sim.StarGen;
import com.sun.media.sound.SoftMixingSourceDataLine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/13/16.
 */
public class SolSysDAO {
    private static final Logger log = Logger.getLogger(SolSysDAO.class.getName());

    public static SolSysObj CreateFromRS(ResultSet rs) {
        SolSysObj newObj = new SolSysObj();
        int sunCount = 0;

        try {
            newObj.Id = rs.getLong("Id");
            newObj.xLoc = rs.getInt("xloc");
            newObj.yLoc = rs.getInt("yloc");
            newObj.zLoc = rs.getInt("zloc");
            newObj.discovererId = rs.getLong("discovererid");
            newObj.systemName = rs.getString("systemname");
            newObj.underProtection = rs.getBoolean("underprotection");
            sunCount = rs.getInt("suns");

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            // inflate secondary structures
            newObj.suns = SunDAO.FetchForSystem(newObj);

        }

        return newObj;
    }



    public static SolSysObj FetchByID(Long objId) {
        SolSysObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.solarsystems WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = SolSysDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "System not found");
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

    public static SolSysObj FindNewSystem(String sysName, int xLoc, int yLoc, int zLoc, int radius, int maxTries) {
        SolSysObj newSys = null;
        StarGen generator = new StarGen();

        while (maxTries-- > 0) {
            int curX = ThreadLocalRandom.current().nextInt(xLoc - radius, xLoc + radius);
            int curY = ThreadLocalRandom.current().nextInt(yLoc - radius, yLoc + radius);
            int curZ = ThreadLocalRandom.current().nextInt(zLoc - radius, zLoc + radius);

            if (FetchIdForLoc(curX, curY, curZ) == 0) {
                // free space!  Generate a planetary System
                newSys = new SolSysObj();
                newSys.xLoc = curX;
                newSys.yLoc = curY;
                newSys.zLoc = curZ;
                newSys.systemName = sysName;

                double minMass = 0.3;
                double maxMass = 3;
                double mass = ThreadLocalRandom.current().nextDouble(minMass, maxMass);

                SunObj newSun = generator.GenerateSystem(sysName, mass);

                newSys.suns = new ArrayList<SunObj>();
                newSys.suns.add(newSun);
                InsertNewObjIntoDB(newSys);
                break;
                }

        }

        return newSys;
    }

    public static List<SolSysObj> InitializeSystems(String sysName, int xLoc, int yLoc, int zLoc, int radius, boolean forceEarth) {
        List<SolSysObj> newSys = new ArrayList<>();
        StarGen generator = new StarGen();

        radius--;
        if (radius < 0)
            radius = 0;

        for (int x = xLoc - radius; x <= xLoc + radius; x++) {
            for (int y =yLoc - radius; y <= yLoc + radius; y++) {
                for (int z = zLoc - radius; z <= zLoc + radius; z++) {
                    SolSysObj theSys = FetchByXYZ(x,y,z);

                    if (theSys == null) {
                        theSys = new SolSysObj();
                        theSys.xLoc = x;
                        theSys.yLoc = y;
                        theSys.zLoc = z;
                        theSys.systemName = String.format("%s [%d, %d, %d]", sysName, x,y,z);
                        double minMass = 0.3;
                        double maxMass = 3;

                        SunObj newSun = null;

                        if ((x == xLoc) && (y == yLoc) && (z == zLoc) && forceEarth) {
                            while (true) {
                                double mass = ThreadLocalRandom.current().nextDouble(minMass, maxMass);

                                newSun = generator.GenerateSystem(theSys.systemName, mass);
                                if (newSun.earthlike > 0)
                                    break;
                            }
                        } else {
                            double mass = ThreadLocalRandom.current().nextDouble(minMass, maxMass);

                             newSun = generator.GenerateSystem(theSys.systemName, mass);
                        }

                        theSys.suns = new ArrayList<SunObj>();
                        theSys.suns.add(newSun);
                        InsertNewObjIntoDB(theSys);
                    }

                    newSys.add(theSys);
                }
            }
        }

        return newSys;
    }



    public static long FetchIdForLoc(int x, int y, int z) {
        long newId = 0;
        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT Id FROM PhabrikObjects.solarsystems WHERE xloc = ? and yloc = ? and zloc = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setInt(1, x);
                statement.setInt(2, y);
                statement.setInt(3, z);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newId = newRs.getLong("Id");
                } else {
                    // nothing there
                    newId = 0;
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newId;
    }

    public static SolSysObj FetchByXYZ(int x, int y, int z) {
        SolSysObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.solarsystems WHERE xloc = ? and yloc = ? and zloc = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setInt(1, x);
                statement.setInt(2, y);
                statement.setInt(3, z);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = SolSysDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "System not found");
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

    public static boolean UserHasAccess(long userId, SolSysObj newSystem) {
        boolean hasAccess = false;

        PlayerObj theObj = PlayerDAO.FetchByID(userId);
        if (theObj != null) {
            if (theObj.isAdmin)
                hasAccess = true;
            else {
                // todo - see if the user has discovered this system
                hasAccess = true;
            }
        }
        return hasAccess;
    }



    private static void InsertNewObjIntoDB(SolSysObj newObj) {
        try {
            String queryStr = "INSERT INTO PhabrikObjects.solarsystems (xloc, yloc, zloc, discovererid, systemname, underprotection, suns)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setInt(1, newObj.xLoc);
            statement.setInt(2, newObj.yLoc);
            statement.setInt(3, newObj.zLoc);
            statement.setLong(4, newObj.discovererId);
            statement.setString(5, newObj.systemName);
            statement.setBoolean(6, newObj.underProtection);
            statement.setInt(7, newObj.suns.size());

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newObj.Id = rs.getLong(1);
                for (SunObj curSun : newObj.suns) {
                    curSun.solarSystemId = newObj.Id;
                    SunDAO.InsertNewObjIntoDB(curSun);
                }
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
