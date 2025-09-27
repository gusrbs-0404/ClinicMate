<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 로그인</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/user.css">
</head>
<body>
    <c:import url="/WEB-INF/views/module/header.jsp" />
    <div class="container">
        <div class="signin-container">
            <div class="logo">
                <h1>ClinicMate</h1>
                <p>병원 예약 관리 시스템</p>
            </div>
            
            <form id="signinForm" class="signin-form" action="/users/action/signin" method="POST">
                <h2>로그인</h2>
                
                <div class="form-group">
                    <label for="username">아이디</label>
                    <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required>
                    <div class="error-message" id="usernameError"></div>
                </div>
                
                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
                    <div class="error-message" id="passwordError"></div>
                </div>
                
                
                <button type="submit" class="btn btn-primary" id="signinBtn">로그인</button>
            </form>
            
            <div class="signup-link">
                <p>계정이 없으신가요? <a href="/users/signup">회원가입</a></p>
            </div>
        </div>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
    <c:import url="/WEB-INF/views/module/footer.jsp" />
</body>
</html>

