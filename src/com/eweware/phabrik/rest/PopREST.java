package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.PlayerDAO;
import com.eweware.phabrik.DAO.PointOfPresenceDAO;
import com.eweware.phabrik.DAO.SolSysDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.PointOfPresenceObj;
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
public class PopREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long playerId = Authenticator.CurrentUserId(request.getSession());

        if (playerId > 0) {
            List<PointOfPresenceObj> popList = PointOfPresenceDAO.FetchForUser(playerId);
            PointOfPresenceObj newObj = null;

            if (popList.size() == 0) {
                // create first one
                newObj = PointOfPresenceDAO.CreateForUser(playerId);
            } else {
                // todo - allow multiple POP
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            RestUtils.get_gson().toJson(newObj, out);
            out.flush();
            out.close();
        } else {
            // player is not logged in
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();

            out.print("user not signed in");
            out.flush();
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long playerId = Authenticator.CurrentUserId(request.getSession());

        if (playerId > 0) {
            List<PointOfPresenceObj> popList = PointOfPresenceDAO.FetchForUser(playerId);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            RestUtils.get_gson().toJson(popList, out);
            out.flush();
            out.close();
        } else {
            // player is not logged in
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();

            out.print("user not signed in");
            out.flush();
            out.close();
        }


    }
}
