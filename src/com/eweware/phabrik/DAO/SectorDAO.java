package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.*;

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
public class SectorDAO {
    private static final Logger log = Logger.getLogger(SectorDAO.class.getName());

    public static SectorObj CreateFromRS(ResultSet rs) {
        SectorObj newObj = new SectorObj();
        try {
            newObj.Id = rs.getLong("Id");
            newObj.lowTemp = rs.getDouble("lowtemp");
            newObj.highTemp = rs.getDouble("hightemp");
            newObj.surfaceType = SectorObj.SurfaceType.valueOf(rs.getString("surfacetype"));
            newObj.ownerId = rs.getLong("ownerid");
            newObj.claimed = rs.getBoolean("claimed");
            newObj.xLoc = rs.getInt("xloc");
            newObj.yLoc = rs.getInt("yloc");
            newObj.terrainId = rs.getLong("terrainid");

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }


        return newObj;
    }



    public static SectorObj FetchByXY(Long terrainId, int xLoc, int yLoc) {
        SectorObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.sectors WHERE terrainid = ? and xLoc = ? and yloc = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, newObj.terrainId);
                statement.setLong(2, newObj.xLoc);
                statement.setLong(3, newObj.yLoc);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = SectorDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Sector not found");
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

    public static SectorObj FetchForTerrain(TerrainObj terrain) {
        SectorObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.sectors WHERE terrainid = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, terrain.Id);

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    newObj = SectorDAO.CreateFromRS(newRs);
                    terrain._sectorArray[newObj.xLoc][newObj.yLoc] = newObj;
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

    public static SectorObj CreateMasterSectorFromPlanet(PlanetObj thePlanet) {
        SectorObj newSector = new SectorObj();

        newSector.highTemp = thePlanet.high_temp;
        newSector.lowTemp = thePlanet.low_temp;
        newSector.surfaceType = GetSurfaceType(thePlanet, 0,0);

        return newSector;
    }

    public static SectorObj.SurfaceType GetSurfaceType(PlanetObj thePlanet, int xLoc, int yLoc) {
        SectorObj.SurfaceType newType = SectorObj.SurfaceType.Unknown;

        switch (thePlanet.planetType) {

            case tRock:
            case tVenusian:
            case tAsteroids:
                newType = SectorObj.SurfaceType.Rock;
                break;
            case tUnknown:
                newType = SectorObj.SurfaceType.Unknown;
                break;
            case t1Face:
            case tTerrestrial:
            case tMartian:
                newType = SectorObj.SurfaceType.Dirt;
                break;
            case tWater:
                newType = SectorObj.SurfaceType.Water;
                break;
            case tIce:
                newType = SectorObj.SurfaceType.Ice;
                break;
            case tGasGiant:
            case tSubGasGiant:
            case tSubSubGasGiant:
                newType = SectorObj.SurfaceType.Gas;
                break;
        }

        return newType;
    }







    public static void InsertNewObjIntoDB(SectorObj newSector) {
        try {
            String queryStr = "INSERT INTO PhabrikObjects.sectors (lowtemp, hightemp, surfacetype, ownerid, claimed, xloc, yloc, terrainid )" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setDouble(1, newSector.lowTemp);
            statement.setDouble(2,newSector.highTemp);
            statement.setString(3, newSector.surfaceType.toString());
            statement.setLong(4, newSector.ownerId);
            statement.setBoolean(5, newSector.claimed);
            statement.setInt(6, newSector.xLoc);
            statement.setInt(7, newSector.yLoc);
            statement.setLong(8, newSector.terrainId);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newSector.Id = rs.getLong(1);
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
