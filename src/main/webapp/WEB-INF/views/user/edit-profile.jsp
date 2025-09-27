<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 회원정보 수정</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/user.css">
</head>
<body>
    <c:import url="/WEB-INF/views/module/header.jsp" />
    <div class="container">
        
        <main class="main-content">
            <div class="edit-profile-container">
                <h2>회원정보 수정</h2>
                
                <form id="editProfileForm" class="edit-form" action="/users/action/edit" method="POST">
                    <input type="hidden" name="_method" value="PUT" />
                    <div class="form-group">
                        <label for="currentPassword">현재 비밀번호</label>
                        <input type="password" id="currentPassword" name="currentPassword" placeholder="현재 비밀번호를 입력하세요" required>
                        <div class="error-message" id="currentPasswordError"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="newPassword">새 비밀번호</label>
                        <input type="password" id="newPassword" name="newPassword" placeholder="새 비밀번호를 입력하세요">
                        <div class="error-message" id="newPasswordError"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmNewPassword">새 비밀번호 확인</label>
                        <input type="password" id="confirmNewPassword" name="confirmNewPassword" placeholder="새 비밀번호를 다시 입력하세요">
                        <div class="error-message" id="confirmNewPasswordError"></div>
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
                    
                    <div class="button-group">
                        <button type="submit" class="btn btn-primary">정보 수정</button>
                        <button type="button" class="btn btn-secondary" onclick="goBack()">취소</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
    <c:import url="/WEB-INF/views/module/footer.jsp" />
</body>
</html>


