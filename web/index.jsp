<%--
  Created by IntelliJ IDEA.
  User: Dave
  Date: 9/2/2016
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Phabrik - a space exploration game</title>
  </head>
  <body>
  Phabrik Server
  <form action="/api/v1/signin" method="post" >
  <input type="submit" value="Submit"><br/>
  Username: <input type="text" title="N" name="N"><br/>
  Password: <input type="password" title="pwd" name="pwd"><br/>
      create account: <input type="checkbox" title="create" name="create"><br/>
  </form>
  </body>
</html>
