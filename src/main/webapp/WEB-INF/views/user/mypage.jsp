<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 마이페이지</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/user.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <div class="logo">
                <h1>ClinicMate</h1>
            </div>
            <nav class="nav">
                <a href="/" class="nav-link">홈</a>
                <a href="/users/me" class="nav-link">마이페이지</a>
                <button class="btn btn-secondary" onclick="logout()">로그아웃</button>
            </nav>
        </header>
        
        <main class="main-content">
            <div class="mypage-container">
                <div class="user-info-section">
                    <h2>회원 정보</h2>
                    <div class="user-info-card">
                        <div class="info-item">
                            <label>아이디:</label>
                            <span id="userUsername">-</span>
                        </div>
                        <div class="info-item">
                            <label>이름:</label>
                            <span id="userName">-</span>
                        </div>
                        <div class="info-item">
                            <label>이메일:</label>
                            <span id="userEmail">-</span>
                        </div>
                        <div class="info-item">
                            <label>전화번호:</label>
                            <span id="userPhone">-</span>
                        </div>
                        <div class="info-item">
                            <label>가입일:</label>
                            <span id="userCreatedAt">-</span>
                        </div>
                        <div class="button-group">
                            <button class="btn btn-primary" onclick="editProfile()">정보 수정</button>
                            <button class="btn btn-danger" onclick="withdrawRequest()">회원 탈퇴</button>
                        </div>
                    </div>
                </div>
                
                <div class="reservation-section">
                    <h2>예약 내역</h2>
                    <div class="reservation-list" id="reservationList">
                        <div class="no-data">
                            <p>예약 내역이 없습니다.</p>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
</body>
</html>


