package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.LoginDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlayerObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class SignInREST extends HttpServlet {
    private static final Logger log = Logger.getLogger(SignInREST.class.getName());

    public class LoginResult
    {
        public String loggedIn;

        LoginResult() {
            loggedIn = "N";
        }
    }

    public class LoginDataObj
    {
        public String N; // userName
        public String pwd; // password
        public boolean create; // create

    }



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //SignIn  N = userName, pwd = passWord
        // todo - work with JSON
        String theName = request.getParameterValues("N")[0];
        String thePwd = request.getParameterValues("pwd")[0];
        boolean create = false;

        String[] createVal = request.getParameterValues("create");
        if (createVal != null) {
            String createStr = createVal[0];
            create = createStr.compareTo("on") == 0;
        }



        LoginDataObj theData = new LoginDataObj();
        theData.N = theName;
        theData.pwd = thePwd;
        theData.create = create;


        PlayerObj theUser;

        if (theData.create)
            theUser = LoginDAO.CreateUserAndLogin(request.getSession(), theData.N, theData.pwd);
        else
            theUser = LoginDAO.LoginUser(request.getSession(), theData.N, theData.pwd);

        // return the result
        PrintWriter out = response.getWriter();

        if (theUser != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            DBHelper.getGson().toJson(theUser, out);
        } else
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginResult theResult = new LoginResult();


        try {
            HttpSession theSession = request.getSession();

            if (Authenticator.UserIsLoggedIn(theSession))
                theResult.loggedIn = "Y";

        }
        catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }


        // return the result
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        DBHelper.getGson().toJson(theResult, out);
        out.flush();
        out.close();
    }
}
