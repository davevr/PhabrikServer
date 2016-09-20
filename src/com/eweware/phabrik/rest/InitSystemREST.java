package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.SolSysDAO;
import com.eweware.phabrik.obj.SolSysObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Dave on 9/19/2016.
 */
public class InitSystemREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xLocStr = request.getParameterValues("xloc")[0];
        String yLocStr = request.getParameterValues("yloc")[0];
        String zLocStr = request.getParameterValues("zloc")[0];
        String nameStr = request.getParameterValues("name")[0];
        String radiusStr = request.getParameterValues("radius")[0];

        int xLoc = Integer.parseInt(xLocStr);
        int yLoc = Integer.parseInt(yLocStr);
        int zLoc = Integer.parseInt(zLocStr);
        int radius = Integer.parseInt(radiusStr);
        boolean earthlike = false;

        String[] earthVal = request.getParameterValues("earth");
        if (earthVal != null) {
            String createStr = earthVal[0];
            earthlike = createStr.compareTo("on") == 0;
        }

        List<SolSysObj> newSystem = SolSysDAO.InitializeSystems(nameStr, xLoc, yLoc, zLoc, radius, earthlike);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();


        RestUtils.get_gson().toJson(newSystem, out);
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
