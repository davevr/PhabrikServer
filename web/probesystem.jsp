<%--
  Created by IntelliJ IDEA.
  User: davidvronay
  Date: 9/17/16
  Time: 2:02 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Phabrik - New Planet</title>
</head>
<body>
Phabrik Server
<form action="/api/v1/probesystem" method="post" >
    <input type="submit" value="Submit"><br/>
    xLoc: <input type="text" title="xloc" name="xloc"><br/>
    yLoc: <input type="text" title="yloc" name="yloc"><br/>
    zLoc: <input type="text" title="zloc" name="zloc"><br/>
    name: <input type="text" title="name" name="name"><br/>
    radius: <input type="text" title="radius" name="radius"><br/>
</form>
</body>
</html>
