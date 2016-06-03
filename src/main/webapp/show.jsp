<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Wikipath</title>
    </head>
    <body>
        <h3>The result shortest path:</h3>
        <ul>
            <c:forEach var="article_datum" items="${requestScope.solution}">
                <li>
                    <a href="${article_datum.url}">${article_datum.title}</a>
                </li>
            </c:forEach>
        </ul>
        <a href="/wikipath">Go back.</a>
    </body>
</html>
