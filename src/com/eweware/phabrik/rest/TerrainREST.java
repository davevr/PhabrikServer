package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.AccessDAO;
import com.eweware.phabrik.DAO.PlanetDAO;
import com.eweware.phabrik.DAO.SectorDAO;
import com.eweware.phabrik.DAO.TerrainDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.AccessObj;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.TerrainObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dave on 9/23/2016.
 */
public class TerrainREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String sectorMapStr = request.getParameter("sectormap");

             if (sectorMapStr != null) {
                String paintOnlyStr = request.getParameter("paint");
                SectorObj[] sectorList = RestUtils.get_gson().fromJson(sectorMapStr, SectorObj[].class);
                boolean paintOnly = Boolean.parseBoolean(paintOnlyStr);

                SectorDAO.UpdateSectors(sectorList, paintOnly);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();

                RestUtils.get_gson().toJson(true, out);
                out.flush();
                out.close();
            }
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String planetidStr = request.getParameterValues("planetid")[0];

            long planetId = Long.parseLong(planetidStr);

            AccessObj accessObj = AccessDAO.FetchForPlanet(userId, planetId);
            TerrainObj newTerrain = null;

            if (accessObj.known) {
                PlanetObj thePlanet = PlanetDAO.FetchByID(planetId);
                newTerrain = TerrainDAO.FetchOrCreateForPlanet(thePlanet);
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            RestUtils.get_gson().toJson(newTerrain, out);
            out.flush();
            out.close();
        }
    }
}
