package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.PlanetDAO;
import com.eweware.phabrik.DAO.SolSysDAO;
import com.eweware.phabrik.DAO.TerrainDAO;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SolSysObj;
import com.eweware.phabrik.obj.TerrainObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by davidvronay on 9/5/16.
 */
public class ProbePlanetREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String planetIdStr = request.getParameterValues("planetid")[0];

        long planetId = Long.parseLong(planetIdStr);

        PlanetObj thePlanet = PlanetDAO.FetchByID(planetId);

        if (thePlanet != null) {
            TerrainObj terrainObj = TerrainDAO.FetchOrCreateForPlanet(thePlanet);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            RestUtils.get_gson().toJson(terrainObj, out);

            out.flush();
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
