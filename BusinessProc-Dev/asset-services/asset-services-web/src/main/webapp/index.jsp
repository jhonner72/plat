<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%
        String contextPath = pageContext.getServletContext().getContextPath();
    %>

    <script type="text/javascript">
        var XMLHttpFactories = [
            function () {
                return new XMLHttpRequest()
            },
            function () {
                return new ActiveXObject("Msxml2.XMLHTTP")
            },
            function () {
                return new ActiveXObject("Msxml3.XMLHTTP")
            },
            function () {
                return new ActiveXObject("Microsoft.XMLHTTP")
            }
        ];

        function submitRequest() {
            var request = document.getElementsByName("request")[0].value;
            if (!request || request.length == 0) {
                alert("empty request");
                return;
            }
            var req = createXMLHTTPObject();
            if (!req) {
                return;
            }
            req.open("POST", "<%=makeUrl(contextPath, "/acs/servlet/acs")%>", true);
            req.setRequestHeader('User-Agent', 'XMLHTTP/1.0');
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            req.onreadystatechange = function () {
                if (req.readyState != 4) {
                    return;
                }
                if (req.status != 200 && req.status != 304) {
                    alert('HTTP error ' + req.status);
                    handleRequest(req);
                    return;
                }
                handleRequest(req);
            };
            if (req.readyState == 4) {
                return;
            }
            req.send(request);
        }

        function handleRequest(req) {
            var writeroot = document.getElementsByName("response")[0];
            writeroot.value = req.responseText;
        }

        function createXMLHTTPObject() {
            var xmlhttp = false;
            for (var i = 0; i < XMLHttpFactories.length; i++) {
                try {
                    xmlhttp = XMLHttpFactories[i]();
                }
                catch (e) {
                    continue;
                }
                break;
            }
            return xmlhttp;
        }
    </script>
</head>
<body>

<form>
    <table>
        <tr>
            <td>
                <textarea name="request" rows=20 cols=80></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input value="submit" type="button" onclick="submitRequest()"/>
            </td>
        </tr>
        <tr>
            <td>
                <textarea name="response" rows=20 cols=80></textarea>
            </td>
        </tr>
    </table>
</form>

</body>
</html>

<%!

    public String makeUrl(String contextPath, String path) {
        if (path.startsWith("/")) {
            return contextPath + path;
        }
        return path;
    }

%>