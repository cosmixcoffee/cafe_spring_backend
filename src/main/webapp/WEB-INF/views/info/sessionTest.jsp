<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

</head>
<body>
    <h2>세션 값 확인</h2>
    
    <p><strong>cf_number:</strong> 
        <%= session.getAttribute("cf_number") != null ? session.getAttribute("cf_number") : "null" %>
    </p>
    
    <p><strong>userid:</strong> 
        <%= session.getAttribute("userid") != null ? session.getAttribute("userid") : "null" %>
    </p>

    <p><strong>user_role:</strong> 
        <%= session.getAttribute("user_role") != null ? session.getAttribute("user_role") : "null" %>
    </p>
    
    <p><strong>user_name:</strong> 
        <%= session.getAttribute("user_name") != null ? session.getAttribute("user_name") : "null" %>
    </p>

    <p><strong>user_au_lv:</strong> 
        <%= session.getAttribute("user_au_lv") != null ? session.getAttribute("user_au_lv") : "null" %>
    </p>
    
    <hr>
    
    <h3>AJAX 요청을 통해 세션 값 가져오기</h3>
    <button id="checkSession">세션 값 확인 (AJAX)</button>
    <pre id="sessionData"></pre>

    <script>
        $(document).ready(function () {
            $("#checkSession").click(function () {
                $.ajax({
                    url: "/checkSession.do",
                    type: "GET",
                    success: function (response) {
                        $("#sessionData").text(JSON.stringify(response, null, 4));
                    },
                    error: function () {
                        alert("세션 데이터를 가져오는 데 실패했습니다.");
                    }
                });
            });
        });
    </script>
</html>