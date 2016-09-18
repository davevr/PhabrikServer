package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.*;
import org.joda.time.DateTime;

import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class SunDAO {
    private static final Logger log = Logger.getLogger(SunDAO.class.getName());

    public static SunObj CreateFromRS(ResultSet rs) {
        SunObj newObj = new SunObj();
        try {
            newObj.Id = rs.getLong("Id");
            newObj.solarSystemId = rs.getInt("solarsystem");
            newObj.name = rs.getString("name");
            newObj.luminosity = rs.getDouble("luminosity");
            newObj.mass = rs.getDouble("mass");
            newObj.life = rs.getDouble("life");
            newObj.age = rs.getDouble("age");
            newObj.r_ecosphere = rs.getDouble("r_ecosphere");
            newObj.m2 = rs.getDouble("m2");
            newObj.a = rs.getDouble("a");
            newObj.e = rs.getDouble("3");
            newObj.earthlike = rs.getInt("earthlike");
            newObj.habitable = rs.getInt("habitable");
            newObj.habitable_jovians = rs.getInt("habitable_jovians");
            newObj.discovererId = rs.getLong("discoverer");

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            // inflate secondary structures
            newObj.planets = PlanetDAO.FetchForSun(newObj);

        }

        return newObj;
    }



    public static SunObj FetchByID(Long objId) {
        SunObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.suns WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = SunDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Sun not found");
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

    public static List<SunObj> FetchForSystem(SolSysObj solSys) {
        List<SunObj> newList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.suns WHERE solarsystemid = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, solSys.Id);

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    SunObj newObj = SunDAO.CreateFromRS(newRs);
                    if (newObj != null) {
                        newList.add(newObj);
                    }
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newList;
    }




    public static void InsertNewObjIntoDB(SunObj newSun) {
        try {
            String queryStr = "INSERT INTO PhabrikObjects.suns (solarsystemid, planets, name, luminosity, mass, life, age, r_ecosphere, m2, a, e, earthlike, habitable, habitable_jovians, discoverer)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setLong(1, newSun.solarSystemId);
            statement.setInt(2,newSun.planets.size());
            statement.setString(3, newSun.name);
            statement.setDouble(4, newSun.luminosity);
            statement.setDouble(5, newSun.mass);
            statement.setDouble(6, newSun.life);
            statement.setDouble(7, newSun.age);
            statement.setDouble(8, newSun.r_ecosphere);
            statement.setDouble(9, newSun.m2);
            statement.setDouble(10, newSun.a);
            statement.setDouble(11, newSun.e);
            statement.setInt(12, newSun.earthlike);
            statement.setInt(13, newSun.habitable);
            statement.setInt(14, newSun.habitable_jovians);
            statement.setLong(15, newSun.discovererId);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newSun.Id = rs.getLong(1);
                for (PlanetObj curPlanet : newSun.planets) {
                    PlanetDAO.InsertNewObjIntoDB(curPlanet);
                }
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
