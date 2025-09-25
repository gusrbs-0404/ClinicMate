<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 회원가입</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/user.css">
</head>
<body>
    <div class="container">
        <div class="signup-container">
            <div class="logo">
                <h1>ClinicMate</h1>
                <p>병원 예약 관리 시스템</p>
            </div>
            
            <form id="signupForm" class="signup-form" action="/users/action/signup" method="POST">
                <h2>회원가입</h2>
                
                <div class="form-group">
                    <label for="username">아이디</label>
                    <input type="text" id="username" name="username" placeholder="6-20자 영문, 숫자, 특수문자(-, _) 조합" required>
                    <div class="error-message" id="usernameError"></div>
                </div>
                
                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password" id="password" name="password" placeholder="영문, 숫자, 특수문자(!@#$%^&*) 포함" required>
                    <div class="error-message" id="passwordError"></div>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">비밀번호 확인</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="비밀번호를 다시 입력하세요" required>
                    <div class="error-message" id="confirmPasswordError"></div>
                </div>
                
                <div class="form-group">
                    <label for="name">이름</label>
                    <input type="text" id="name" name="name" placeholder="한글 2-5자" required>
                    <div class="error-message" id="nameError"></div>
                </div>
                
                <div class="form-group">
                    <label for="email">이메일</label>
                    <input type="email" id="email" name="email" placeholder="example@domain.com" required>
                    <div class="error-message" id="emailError"></div>
                </div>
                
                <div class="form-group">
                    <label for="phone">전화번호</label>
                    <input type="tel" id="phone" name="phone" placeholder="010-1234-5678" required>
                    <div class="error-message" id="phoneError"></div>
                </div>
                
                <button type="submit" class="btn btn-primary" id="signupBtn">회원가입</button>
            </form>
            
            <div class="login-link">
                <p>이미 계정이 있으신가요? <a href="/users/signin">로그인</a></p>
            </div>
        </div>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
</body>
</html>


