package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.SolSysDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.SolSysObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dave on 9/22/2016.
 */
public class SolSysREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String xLocStr = request.getParameterValues("xloc")[0];
            String yLocStr = request.getParameterValues("yloc")[0];
            String zLocStr = request.getParameterValues("zloc")[0];

            int xLoc = Integer.parseInt(xLocStr);
            int yLoc = Integer.parseInt(yLocStr);
            int zLoc = Integer.parseInt(zLocStr);

            SolSysObj newSystem = SolSysDAO.FetchByXYZ(xLoc, yLoc, zLoc);

            if (newSystem != null) {
                if (SolSysDAO.UserHasAccess(userId, newSystem)) {
                    // do any init work
                } else
                    newSystem = null;
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();


            RestUtils.get_gson().toJson(newSystem, out);
            out.flush();
            out.close();
        }
    }
}
