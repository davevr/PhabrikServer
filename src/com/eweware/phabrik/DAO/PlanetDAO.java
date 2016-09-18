package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SunObj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class PlanetDAO {
    private static final Logger log = Logger.getLogger(PlanetDAO.class.getName());

    public static PlanetObj CreateFromRS(ResultSet rs) {
        PlanetObj newObj = new PlanetObj();
        long sunId = 0;
        int moonCount = 0;
        int gasCount = 0;

        try {
            newObj.Id = rs.getLong("Id");
            newObj.planetType = PlanetObj.planet_type.valueOf(rs.getString("planet_type"));
            newObj.systemId = rs.getLong("systemid");
            newObj.discovererId = rs.getLong("discovererid");
            newObj.planet_no = rs.getInt("planet_no");
            newObj.a = rs.getDouble("a");
            newObj.e = rs.getDouble("e");
            newObj.owned = rs.getBoolean("owned");
            newObj.ownerId = rs.getLong("ownerid");
            sunId = rs.getLong("sun");
            newObj.axial_tilt = rs.getDouble("axial_tilt");
            newObj.mass = rs.getDouble("mass");
             newObj.gas_giant = rs.getBoolean("gas_giant");
            newObj.dust_mass = rs.getDouble("dust_mass");
            newObj.gas_mass = rs.getDouble("gas_mass");
            newObj.moon_a = rs.getDouble("moon_a");
            newObj.moon_e = rs.getDouble("moon_e");
            newObj.core_radius = rs.getDouble("core_radius");
            newObj.radius = rs.getDouble("radius");
            newObj.orbit_zone = rs.getInt("orbit_zone");
            newObj.density = rs.getDouble("density");
            newObj.orb_period = rs.getDouble("orb_period");
            newObj.day = rs.getDouble("day");
             newObj.resonant_period = rs.getBoolean("resonant_period");
            newObj.esc_velocity = rs.getDouble("esc_velocity");
            newObj.surf_accel = rs.getDouble("surf_accel");
            newObj.surf_grav = rs.getDouble("surf_grav");
            newObj.rms_velocity = rs.getDouble("rms_velocity");
            newObj.molec_weight = rs.getDouble("molec_weight");
            newObj.volatile_gas_inventory = rs.getDouble("volatile_gas_inventory");
            newObj.surf_pressure = rs.getDouble("surf_pressure");
             newObj.greenhouse_effect = rs.getBoolean("greenhouse_effect");
            newObj.boil_point = rs.getDouble("boil_point");
            newObj.albedo = rs.getDouble("albedo");
            newObj.exospheric_temp = rs.getDouble("exospheric_temp");
            newObj.estimated_temp = rs.getDouble("estimated_temp");
            newObj.estimated_terr_temp = rs.getDouble("estimate_terr_temp");
            newObj.surf_temp = rs.getDouble("surf_temp");
            newObj.greenhs_rise = rs.getDouble("greenhs_rise");
            newObj.high_temp = rs.getDouble("high_temp");
            newObj.low_temp = rs.getDouble("low_temp");
            newObj.max_temp = rs.getDouble("max_temp");
            newObj.min_temp = rs.getDouble("min_temp");
            newObj.hydrosphere = rs.getDouble("hydrosphere");
            newObj.cloud_cover = rs.getDouble("cloud_cover");
            newObj.ice_cover = rs.getDouble("ice_cover");
            newObj.gases = rs.getInt("gases");
             newObj.earthlike = rs.getBoolean("earthlike");
             newObj.habitable = rs.getBoolean("habitable");
             newObj.habitable_jovian = rs.getBoolean("habitable_jovian");;
            moonCount =  rs.getInt("moons");
            newObj.planetName = rs.getString("planetname");

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            if (gasCount > 0) {
                // todo -inflate the gases
            }

            if (moonCount > 0) {
                // todo - inflate the moons

            }
        }

        return newObj;
    }



    public static PlanetObj FetchByID(Long objId) {
        PlanetObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.planets WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = PlanetDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Planet not found");
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

    public static List<PlanetObj> FetchForSun(SunObj theSun) {
        List<PlanetObj> newList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.planets WHERE sun = ? ORDER BY planet_no ASC";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, theSun.Id );

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    PlanetObj newObj = PlanetDAO.CreateFromRS(newRs);
                    if (newObj != null)
                        newObj.sun = theSun;
                        newList.add(newObj);
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





    public static void InsertNewObjIntoDB(PlanetObj newObj) {
        try {
            String queryStr = "INSERT INTO PhabrikObjects.planets " +
                    "(planet_type, systemid, discovererid, planet_no, a, e, owned, ownerid, sun, axial_tilt, mass, gas_giant, " +
                    "dust_mass, gas_mass, moon_a, moon_e, core_radius, radius, orbit_zone, density, orb_period, day, resonant_period, " +
                    "esc_velocity, surf_accel, surf_grav, rms_velocity, molec_weight, volatile_gas_inventory, surf_pressure, " +
                    "greenhouse_effect, boil_point, albedo, exospheric_temp, estimated_temp, estimated_terr_temp, surf_temp, " +
                    "greenhs_rise, high_temp, low_temp, max_temp, min_temp, hydrosphere, cloud_cover, ice_cover, gases, " +
                    "earthlike, habitable, habitable_jovian, moons, planetname)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setString(1, newObj.planetType.toString());
            statement.setLong(2,newObj.systemId);
            statement.setLong(3, newObj.discovererId);
            statement.setDouble(4, newObj.planet_no);
            statement.setDouble(5, newObj.a);
            statement.setDouble(6, newObj.e);
            statement.setBoolean(7, newObj.owned);
            statement.setLong(8, newObj.ownerId);
            statement.setLong(9, newObj.sun.Id);
            statement.setDouble(10, newObj.axial_tilt);
            statement.setDouble(11, newObj.mass);
            statement.setBoolean(12, newObj.gas_giant);
            statement.setDouble(13, newObj.dust_mass);
            statement.setDouble(14, newObj.gas_mass);
            statement.setDouble(15, newObj.moon_a);
            statement.setDouble(16, newObj.moon_e);
            statement.setDouble(17, newObj.core_radius);
            statement.setDouble(18, newObj.radius);
            statement.setDouble(19, newObj.orbit_zone);
            statement.setDouble(20, newObj.density);
            statement.setDouble(21, newObj.orb_period);
            statement.setDouble(22,newObj.day);
            statement.setBoolean(23, newObj.resonant_period);
            statement.setDouble(24, newObj.esc_velocity);
            statement.setDouble(25, newObj.surf_accel);
            statement.setDouble(26, newObj.surf_grav);
            statement.setDouble(27, newObj.rms_velocity);
            statement.setDouble(28, newObj.molec_weight);
            statement.setDouble(29, newObj.volatile_gas_inventory);
            statement.setDouble(30, newObj.surf_pressure);
            statement.setBoolean(31, newObj.greenhouse_effect);
            statement.setDouble(32, newObj.boil_point);
            statement.setDouble(33, newObj.albedo);
            statement.setDouble(34, newObj.exospheric_temp);
            statement.setDouble(35, newObj.estimated_temp);
            statement.setDouble(36, newObj.estimated_terr_temp);
            statement.setDouble(37, newObj.surf_temp);
            statement.setDouble(38, newObj.greenhs_rise);
            statement.setDouble(39, newObj.high_temp);
            statement.setDouble(40, newObj.low_temp);
            statement.setDouble(41, newObj.max_temp);
            statement.setDouble(42,newObj.min_temp);
            statement.setDouble(43, newObj.hydrosphere);
            statement.setDouble(44, newObj.cloud_cover);
            statement.setDouble(45, newObj.ice_cover);
            statement.setInt(46, newObj.gases);
            statement.setBoolean(47, newObj.earthlike);
            statement.setBoolean(48, newObj.habitable);
            statement.setBoolean(49, newObj.habitable_jovian);
            if (newObj.moonList != null)
                statement.setInt(50, newObj.moonList.size());
            else
                statement.setInt(50, 0);
            statement.setString(51, newObj.planetName);


            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newObj.Id = rs.getLong(1);

                // todo - save moons
                // todo - save atmosphere

            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
