<%--
  Created by IntelliJ IDEA.
  User: Dave
  Date: 9/19/2016
  Time: 9:55 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Phabrik - Init Systems</title>
</head>
<body>
Phabrik Server
<form action="/api/v1/initsystem" method="post" >
    <input type="submit" value="Submit"><br/>
    xLoc: <input type="text" title="xloc" name="xloc"><br/>
    yLoc: <input type="text" title="yloc" name="yloc"><br/>
    zLoc: <input type="text" title="zloc" name="zloc"><br/>
    name: <input type="text" title="name" name="name"><br/>
    radius: <input type="text" title="radius" name="radius"><br/>
    require earthlike: <input type="checkbox" title="earth" name="earth"><br/>
</form>
</body>
</html>
