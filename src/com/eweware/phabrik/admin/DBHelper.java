package com.eweware.phabrik.admin;

import com.eweware.phabrik.api.gsonUTCJodaDateTimeAdapter;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class DBHelper {
    private static final Logger log = Logger.getLogger(DBHelper.class.getName());
    public static Connection _currentConnection;
    private static int _connectionCount = 0;
    private static Boolean useLocalDB = false;//true;//false;
    private static Gson _gson = null;


    public static Gson getGson() {
        if (_gson == null) {
            _gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new gsonUTCJodaDateTimeAdapter()).create();
        }

        return _gson;
    }


    public static Connection GetConnection() {
        try {
            if ((_currentConnection != null) && _currentConnection.isValid(2)) {
                _connectionCount++;
                return _currentConnection;
            } else {
                String url = null;
                log.info("creating new connection");

                if (SystemProperty.environment.value() ==
                        SystemProperty.Environment.Value.Production) {
                    log.log(Level.INFO, "connecting from app engine");
                    Class.forName("com.mysql.jdbc.GoogleDriver");
                    url = "jdbc:google:mysql://phabrik-server-01:us-central1:phrabrik-sql-server";
                    _currentConnection = DriverManager.getConnection(url, "root", "All4Sheeple");

                } else {
                    log.info("connecting locally");
                    // running locally
                    if (useLocalDB) {
                        // use the DB on theis same machine
                        Class.forName("com.mysql.jdbc.Driver");
                        url = "jdbc:mysql://address=(protocol=tcp)(host=127.0.0.1)(port=3306)";

                        _currentConnection = DriverManager.getConnection(url, "root", "All4Sheeple");
                    } else {
                        Class.forName("com.mysql.jdbc.Driver");
                        url = "jdbc:mysql://address=(protocol=tcp)(host=104.198.67.6)(port=3306)";

                        _currentConnection = DriverManager.getConnection(url, "root", "All4Sheeple");
                    }
                }
            }
        } catch (ClassNotFoundException exp) {
            log.log(Level.SEVERE, exp.getMessage());
        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        }

        _connectionCount++;
        return _currentConnection;
    }

    public static void EnsureConnection() {
        GetConnection();
    }

    public static void ReleaseConnection() {
        _connectionCount--;
        if (_connectionCount == 0)
            log.log(Level.INFO, "Connection to zero");
        else if (_connectionCount < 0) {
            log.log(Level.SEVERE, "ERROR:  Connection over released!");
        }
        // TO DO - decide if we want to release it or not...
        /*
        if (_currentConnection != null) {
            try {
                _currentConnection.close();
                _currentConnection = null;
            }
            catch (Exception exp) {
                System.out.println(exp.getMessage());
            }
        }
        */
    }

    public static PreparedStatement PrepareStatement(String theStatement, Boolean returnKeys) {
        PreparedStatement statement = null;

        try {
            if (returnKeys)
                statement = GetConnection().prepareStatement(theStatement, Statement.RETURN_GENERATED_KEYS);
            else
                statement = GetConnection().prepareStatement(theStatement);

        } catch (Exception exp) {
            System.out.println(exp.getMessage());
        }

        return statement;
    }

    public static ResultSet ExecuteQuery(String theQuery) {
        ResultSet theResult = null;
        try {
            theResult = GetConnection().createStatement().executeQuery(theQuery);
        }
        catch (Exception exp) {
            System.out.println(exp.getMessage());
        }

        return theResult;
    }

    public static ResultSet ExecuteQuery(PreparedStatement theStatement) {
        ResultSet theResult = null;
        try {
            theResult = theStatement.executeQuery();
        }
        catch (Exception exp) {
            System.out.println(exp.getMessage());
        }

        return theResult;
    }

    public static int ExecuteUpdate(PreparedStatement theStatement) {
        int resultCount = 0;
        try {
            resultCount = theStatement.executeUpdate();
        }
        catch (Exception exp) {
            System.out.println(exp.getMessage());
        }

        return resultCount;
    }

    public static void InsertAssociation(String tableName, long sourceId, long itemId) {
        try {
            String statementStr = "INSERT INTO HeardObjects." + tableName + " (sourceid, itemid) values (?, ?) ";
            PreparedStatement statement = PrepareStatement(statementStr, false);
            statement.setLong(1, sourceId);
            statement.setLong(2, itemId);
            statement.execute();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }

    public static boolean AssociationExists(String tableName, long sourceId, long itemId) {
        boolean exists = false;
        try {
            String statementStr = "SELECT * FROM HeardObjects." + tableName + " WHERE sourceid = ? AND itemid = ?";
            PreparedStatement statement = PrepareStatement(statementStr, false);
            statement.setLong(1, sourceId);
            statement.setLong(2, itemId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }

        return exists;
    }

    public static List<Long> FetchAssociations(String tableName, long sourceId) {
        List<Long> resultList = new ArrayList<>();
        try {
            String statementStr = "SELECT itemid FROM HeardObjects." + tableName + " WHERE sourceid = ?";
            PreparedStatement statement = PrepareStatement(statementStr, false);
            statement.setLong(1, sourceId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getLong(1));
            }
            rs.close();
            statement.close();

            if (resultList.size() == 0)
                resultList = null;

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
            resultList = null;
        }

        return resultList;
    }


    public static List<String> FetchStringsMatchId(String tableName, long searchId) {
        List<String> resultList = new ArrayList<String>();

        Connection conn = DBHelper.GetConnection();

        if (conn != null) {
            try {
                String queryString = "SELECT idStr FROM HeardObjects." + tableName + " WHERE sourceId = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryString, false);

                statement.setLong(1, searchId);

                ResultSet rs = DBHelper.ExecuteQuery(statement);
                while (rs.next()) {
                    String theIdStr = rs.getString(1);
                    resultList.add(theIdStr);
                }

                rs.close();
                statement.close();

            } catch (Exception exp) {
                log.log(Level.SEVERE, exp.getMessage());
            } finally {
                DBHelper.ReleaseConnection();
            }
        }

        return resultList;
    }

    public static Boolean RemoveAssociation(String tableName, long sourceId, long itemId) {
        Boolean didIt = false;

        try {
            //todo:  write correct delete
            String statementStr = "DELETE FROM HeardObjects." + tableName + " (sourceid, itemid) values (?, ?) ";
            PreparedStatement statement = PrepareStatement(statementStr, false);
            statement.setLong(1, sourceId);
            statement.setLong(2, itemId);
            statement.execute();
            statement.close();
            didIt = true;

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }

        return didIt;
    }
}
