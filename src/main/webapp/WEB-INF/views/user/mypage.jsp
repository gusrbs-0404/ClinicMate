<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <c:import url="/WEB-INF/views/module/header.jsp" />
    <div class="container">
        
        <main class="main-content">
            <div class="mypage-container">
                <div class="user-info-section">
                    <h2>회원 정보</h2>
                    <div class="user-info-card">
                        <c:choose>
                            <c:when test="${not empty user}">
                                <div class="info-item">
                                    <label>아이디:</label>
                                    <span>${user.username}</span>
                                </div>
                                <div class="info-item">
                                    <label>이름:</label>
                                    <span>${user.name}</span>
                                </div>
                                <div class="info-item">
                                    <label>이메일:</label>
                                    <span>${user.email}</span>
                                </div>
                                <div class="info-item">
                                    <label>전화번호:</label>
                                    <span>${user.phone}</span>
                                </div>
                                <div class="info-item">
                                    <label>가입일:</label>
                                    <span><fmt:formatDate value="${createdAtDate}" pattern="yyyy년 M월 d일 HH:mm:ss" /></span>
                                </div>
                            </c:when>
                            <c:otherwise>
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
                            </c:otherwise>
                        </c:choose>
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
    <c:import url="/WEB-INF/views/module/footer.jsp" />
</body>
</html>


