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
                                <c:if test="${user.withdrawalStatus == 'WITHDRAWAL_REQUESTED'}">
                                    <div class="info-item withdrawal-status">
                                        <label>탈퇴 상태:</label>
                                        <span class="status-badge status-warning">탈퇴 요청 대기중</span>
                                    </div>
                                </c:if>
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
                            <c:choose>
                                <c:when test="${user.withdrawalStatus == 'WITHDRAWAL_REQUESTED'}">
                                    <button class="btn btn-warning" onclick="cancelWithdrawRequest()">탈퇴 요청 취소</button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-danger" onclick="withdrawRequest()">회원 탈퇴</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                
                <div class="reservation-section">
                    <h2>예약 내역</h2>
                    <div class="reservation-list" id="reservationList">
                        <c:choose>
                            <c:when test="${not empty reservations}">
                                <c:forEach var="reservation" items="${reservations}">
                                    <div class="reservation-item">
                                        <div class="reservation-info">
                                            <h4>${reservation.hospital.hospitalName}</h4>
                                            <p><strong>진료과:</strong> ${reservation.department.deptName}</p>
                                            <p><strong>의사:</strong> ${reservation.doctor.name}</p>
                                            <p><strong>예약일시:</strong> 
                                                ${reservation.resDate}
                                            </p>
                                            <p><strong>상태:</strong> 
                                                <span class="status status-${reservation.status.name()}">${reservation.status.name()}</span>
                                            </p>
                                        </div>
                                        <div class="reservation-actions">
                                            <c:if test="${reservation.status.name() == '예약중'}">
                                                <button class="btn btn-primary btn-sm" onclick="goToPayment(${reservation.resId})">결제하기</button>
                                                <button class="btn btn-danger btn-sm" onclick="cancelReservation(${reservation.resId})">예약취소</button>
                                            </c:if>
                                            <c:if test="${reservation.status.name() == '완료'}">
                                                <span class="completed-text">예약 완료</span>
                                                <button class="btn btn-danger btn-sm" onclick="cancelReservation(${reservation.resId})">예약취소</button>
                                            </c:if>
                                            <c:if test="${reservation.status.name() == '취소'}">
                                                <span class="cancelled-text">예약 취소됨</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <p>예약 내역이 없습니다.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </main>
    </div>
    
    <!-- 결제 모달 -->
    <div id="paymentModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>결제하기</h3>
                <span class="close" onclick="closePaymentModal()">&times;</span>
            </div>
            <div class="modal-body">
                <div class="payment-info">
                    <h4>예약 정보</h4>
                    <div id="paymentReservationInfo">
                        <!-- 예약 정보가 여기에 표시됩니다 -->
                    </div>
                </div>
                
                <div class="payment-form">
                    <h4>결제 정보</h4>
                    <div class="form-group">
                        <label for="paymentAmount">결제 금액 (원)</label>
                        <input type="number" id="paymentAmount" name="amount" min="1000" max="1000000" value="50000" required>
                    </div>
                    
                    <div class="form-group">
                        <label>결제 방법</label>
                        <div class="payment-methods">
                            <label class="payment-method">
                                <input type="radio" name="paymentMethod" value="카드" checked>
                                <span class="method-label">💳 카드</span>
                            </label>
                            <label class="payment-method">
                                <input type="radio" name="paymentMethod" value="현금">
                                <span class="method-label">💰 현금</span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closePaymentModal()">취소</button>
                <button type="button" class="btn btn-primary" onclick="processPayment()">결제하기</button>
            </div>
        </div>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
    <c:import url="/WEB-INF/views/module/footer.jsp" />
</body>
</html>


