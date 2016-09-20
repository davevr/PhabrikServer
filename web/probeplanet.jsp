<%--
  Created by IntelliJ IDEA.
  User: Dave
  Date: 9/18/2016
  Time: 8:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Phabrik - probe planet</title>
</head>
<body>
Phabrik Server
<form action="/api/v1/probeplanet" method="post" >
    planet id: <input type="text" title="planetid" name="planetid"><br/>
    <input type="submit" value="Submit"><br/>
</form>
</body>
</html>
