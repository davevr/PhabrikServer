package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.StructureObj;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class StructureDAO {
    private static final Logger log = Logger.getLogger(StructureTypeDAO.class.getName());

    public static StructureObj CreateFromRS(ResultSet rs) {
        StructureObj newObj = new StructureObj();
        try {

            newObj.Id = rs.getLong("Id");
            newObj.sectorId = rs.getLong("sectorid");
            newObj.xLoc = rs.getInt("xloc");
            newObj.yLoc = rs.getInt("yloc");
            newObj.xSize = rs.getInt("xsize");
            newObj.ySize = rs.getInt("ysize");
            newObj.curPop = rs.getInt("curpop");
            newObj.maxPop = rs.getInt("maxpop");
            newObj.curHP = rs.getInt("curhp");
            newObj.maxHP = rs.getInt("maxhp");
            newObj.minPowerNeed = rs.getInt("minpowerneed");
            newObj.minPopNeed = rs.getInt("minpopneed");
            newObj.solidStorageSpace = rs.getInt("solidstoragespace");
            newObj.gasStorageSpace = rs.getInt("gasstoragespace");
            newObj.foodStorageSpace = rs.getInt("foodstoragespace");
            newObj.liquidStorageSpace = rs.getInt("liquidstoragespace");
            newObj.energyStorageSpace = rs.getInt("energystoragespace");
            newObj.strangeStorageSpace = rs.getInt("strangestoragespace");
            newObj.maxSolidStorageSpace = rs.getInt("maxsolidstoragespace");
            newObj.maxGasStorageSpace = rs.getInt("maxgasstoragespace");
            newObj.maxFoodStorageSpace = rs.getInt("maxfoodstoragespace");
            newObj.maxLiquidStorageSpace = rs.getInt("maxliquidstoragespace");
            newObj.maxEnergyStorageSpace = rs.getInt("maxenergystoragespace");
            newObj.maxStrangeStorageSpace = rs.getInt("maxstrangestoragespace");
            newObj.creationDate = new DateTime(rs.getTimestamp("creationdate"));
            newObj.lastTick = new DateTime(rs.getTimestamp("lasttick"));
            newObj.ownerId = rs.getLong("ownerid");
            newObj.physicalDefense = rs.getDouble("physicaldefense");
            newObj.energyDefense = rs.getDouble("energydefense");
            newObj.isPublic = rs.getBoolean("ispublic");
            newObj.isVacuumSafe = rs.getBoolean("isvacuumsafe");
            newObj.isRadiationSafe = rs.getBoolean("isradiationsafe");
            newObj.structureTypeId = rs.getLong("structuretypeid");
            newObj.nickname = rs.getString("nickname");


        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }



        return newObj;
    }

    public static List<StructureObj> FetchBySectorID(Long sectorId) {
        List<StructureObj> structureList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.structures WHERE sectorid = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, sectorId);

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    StructureObj newObj = StructureDAO.CreateFromRS(newRs);
                    if (newObj != null)
                        structureList.add(newObj);
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return structureList;
    }

    public static StructureObj FetchByID(Long objId) {
        StructureObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.structures WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = StructureDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Structure not found");
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


    public static void InsertNewObjIntoDB(StructureObj newStruct) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.structures (sectorid, xloc, yloc, xsize, ysize, curpop, maxpop, curhp, maxhp, " +
                    "minpowerneed, minpopneed, solidstoragespace, gasstoragespace, foodstoragespace, liquidstoragespace, energystoragespace, " +
                    "strangestoragespace, maxsolidstoragespace, maxgasstoragespace, maxfoodstoragespace, maxliquidstoragespace, maxenergystoragespace, " +
                    "maxstrangestoragespace, creationdate, lasttick, ownerid, physicaldefense, energydefense, ispublic, " +
                    "isvacuumsafe, isradiationsafe, structuretypeid, nickname )" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setLong(1, newStruct.sectorId);
            statement.setInt(2,newStruct.xLoc);
            statement.setInt(3, newStruct.yLoc);
            statement.setInt(4, newStruct.xSize);
            statement.setInt(5, newStruct.ySize);
            statement.setInt(6, newStruct.curPop);
            statement.setInt(7, newStruct.maxPop);
            statement.setInt(8, newStruct.curHP);
            statement.setInt(9, newStruct.maxHP);
            statement.setInt(10, newStruct.minPowerNeed);
            statement.setInt(11, newStruct.minPopNeed);
            statement.setInt(12,newStruct.solidStorageSpace);
            statement.setInt(13, newStruct.gasStorageSpace);
            statement.setInt(14, newStruct.foodStorageSpace);
            statement.setInt(15, newStruct.liquidStorageSpace);
            statement.setInt(16, newStruct.energyStorageSpace);
            statement.setInt(17, newStruct.strangeStorageSpace);
            statement.setInt(18, newStruct.maxSolidStorageSpace);
            statement.setInt(19, newStruct.maxGasStorageSpace);
            statement.setInt(20, newStruct.maxFoodStorageSpace);
            statement.setInt(21, newStruct.maxLiquidStorageSpace);
            statement.setInt(22,newStruct.maxEnergyStorageSpace);
            statement.setInt(23, newStruct.maxStrangeStorageSpace);
            statement.setTimestamp(24, new Timestamp(newStruct.creationDate.getMillis()));
            statement.setTimestamp(25, new Timestamp(newStruct.lastTick.getMillis()));
            statement.setLong(26, newStruct.ownerId);
            statement.setDouble(27, newStruct.physicalDefense);
            statement.setDouble(28, newStruct.energyDefense);
            statement.setBoolean(29, newStruct.isPublic);
            statement.setBoolean(30, newStruct.isVacuumSafe);
            statement.setBoolean(31, newStruct.isRadiationSafe);
            statement.setLong(32, newStruct.structureTypeId);
            statement.setString(33, newStruct.nickname);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newStruct.Id = rs.getLong(1);
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
