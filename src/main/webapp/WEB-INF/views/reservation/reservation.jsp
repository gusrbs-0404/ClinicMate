<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 병원 예약</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/reservation.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
</head>
<body>
    <div class="container">
        <jsp:include page="/WEB-INF/views/module/header.jsp" />

        <main class="reservation-content">
            <div class="reservation-container">
                <!-- 병원 정보 섹션 -->
                <div class="hospital-info-header">
                    <h2>병원 예약</h2>
                    <c:if test="${not empty selectedHospital}">
                        <h3 id="hospitalName">${selectedHospital.hospitalName}</h3>
                        <p id="hospitalAddress">${selectedHospital.address}</p>
                        <p id="hospitalPhone">${selectedHospital.phone}</p>
                    </c:if>
                </div>

                <div class="reservation-main">
                    <!-- 좌측 패널: 진료과 및 의사 선택 -->
                    <div class="left-panel">
                        <div class="panel-section">
                            <h4>진료과 선택</h4>
                            <div id="departmentList" class="selection-list">
                                <c:choose>
                                    <c:when test="${not empty selectedHospital}">
                                        <!-- JavaScript로 동적 로드 -->
                                        <p>진료과를 불러오는 중...</p>
                                    </c:when>
                                    <c:otherwise>
                                        <p>병원을 먼저 선택해주세요.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="panel-section">
                            <h4>의사 선택</h4>
                            <div id="doctorList" class="selection-list">
                                <p>진료과를 선택하면 의사 목록이 표시됩니다.</p>
                            </div>
                        </div>
                    </div>

                    <!-- 중앙 패널: 날짜 및 시간 선택 -->
                    <div class="center-panel">
                        <div class="panel-section">
                            <h4>날짜 선택</h4>
                            <input type="text" id="datePicker" class="flatpickr" placeholder="날짜를 선택하세요">
                        </div>

                        <div class="panel-section">
                            <h4>시간 선택 (30분 단위)</h4>
                            <div id="timeSlots" class="time-slots">
                                <p>날짜와 의사를 선택하면 예약 가능한 시간이 표시됩니다.</p>
                            </div>
                        </div>
                    </div>

                    <!-- 우측 패널: 예약 정보 확인 -->
                    <div class="right-panel">
                        <div class="panel-section">
                            <h4>예약자 정보</h4>
                            <div class="summary-item">
                                <strong>이름:</strong> <span id="summaryUserName"></span>
                            </div>
                            <div class="summary-item">
                                <strong>전화번호:</strong> <span id="summaryUserPhone"></span>
                            </div>
                            <div class="summary-item">
                                <strong>이메일:</strong> <span id="summaryUserEmail"></span>
                            </div>
                        </div>
                        
                        <div class="panel-section">
                            <h4>예약 정보 확인</h4>
                            <div class="summary-item">
                                <strong>병원:</strong> <span id="summaryHospitalName"></span>
                            </div>
                            <div class="summary-item">
                                <strong>진료과:</strong> <span id="summaryDepartmentName"></span>
                            </div>
                            <div class="summary-item">
                                <strong>의사:</strong> <span id="summaryDoctorName"></span>
                            </div>
                            <div class="summary-item">
                                <strong>날짜:</strong> <span id="summaryDate"></span>
                            </div>
                            <div class="summary-item">
                                <strong>시간:</strong> <span id="summaryTime"></span>
                            </div>
                            <button id="confirmReservationBtn" class="btn btn-primary" disabled>예약 확정</button>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/WEB-INF/views/module/footer.jsp" />
    </div>

    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="/script/common.js"></script>
    <script src="/script/reservation.js"></script>
</body>
</html>