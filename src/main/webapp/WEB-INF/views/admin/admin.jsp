<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 관리자 페이지</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/admin.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="admin-page">
    <c:import url="/WEB-INF/views/module/header.jsp" />
    
    <!-- 관리자 페이지 전용 스크립트 -->
    <script>
    // 관리자 페이지에서 메인/마이페이지 이동 차단
    document.addEventListener('DOMContentLoaded', function() {
        // 로고 클릭 시 관리자 페이지로 유지
        const logoLink = document.querySelector('.logo-link');
        if (logoLink) {
            logoLink.href = '/admin';
        }
        
        // 마이페이지 링크 제거
        const mypageLink = document.querySelector('a[href="/users/me"]');
        if (mypageLink) {
            mypageLink.style.display = 'none';
        }
        
        // 관리자 정보 표시
        const userMenu = document.getElementById('userMenu');
        if (userMenu) {
            const adminInfo = document.createElement('span');
            adminInfo.textContent = '관리자';
            adminInfo.style.color = '#ed3f27';
            adminInfo.style.fontWeight = 'bold';
            adminInfo.style.marginRight = '1rem';
            userMenu.insertBefore(adminInfo, userMenu.firstChild);
        }
    });
    </script>

    <!-- 메인 네비게이션 -->
    <nav class="admin-nav">
        <div class="nav-content">
            <span class="admin-title">관리자 : admin</span>
            <div class="nav-tabs">
                <button class="nav-tab active" data-tab="users">회원관리</button>
                <button class="nav-tab" data-tab="hospitals">병원/진료과/의사 관리</button>
                <button class="nav-tab" data-tab="reservations">예약관리</button>
                <button class="nav-tab" data-tab="payments">결제관리</button>
                <button class="nav-tab" data-tab="statistics">통계관리</button>
                <button class="nav-tab" data-tab="notifications">알림관리</button>
            </div>
        </div>
    </nav>

    <!-- 메인 콘텐츠 -->
    <main class="admin-main">
        <!-- 회원 관리 탭 -->
        <div id="users-tab" class="tab-content active">
            <div class="content-header">
                <h2>회원 관리</h2>
                <button class="btn btn-primary" onclick="showAddUserModal()">회원 추가</button>
            </div>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>사용자명</th>
                            <th>이름</th>
                            <th>이메일</th>
                            <th>전화번호</th>
                            <th>권한</th>
                            <th>탈퇴상태</th>
                            <th>가입일</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody id="users-table-body">
                        <!-- 동적으로 로드됨 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 병원/진료과/의사 관리 탭 -->
        <div id="hospitals-tab" class="tab-content">
            <div class="content-header">
                <h2>병원/진료과/의사 관리</h2>
                <div class="sub-nav">
                    <button class="sub-nav-btn active" data-sub-tab="hospitals">병원/진료과 관리</button>
                    <button class="sub-nav-btn" data-sub-tab="doctors">의사 관리</button>
                    <button class="sub-nav-btn" data-sub-tab="schedules">스케줄 관리</button>
                </div>
            </div>
            
            <!-- 병원 관리 -->
            <div id="hospitals-sub-tab" class="sub-tab-content active">
                <div class="sub-content-header">
                    <button class="btn btn-primary" onclick="showAddHospitalModal()">병원 추가</button>
                    <p class="info-text">💡 병원 행을 클릭하면 해당 병원의 진료과 목록이 표시됩니다.</p>
                </div>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>병원 ID</th>
                                <th>병원명</th>
                                <th>주소</th>
                                <th>전화번호</th>
                                <th>등록일</th>
                                <th>액션</th>
                            </tr>
                        </thead>
                        <tbody id="hospitals-table-body">
                            <!-- 동적으로 로드됨 -->
                        </tbody>
                    </table>
                </div>
                
                <!-- 선택된 병원의 진료과 목록 -->
                <div id="selected-hospital-departments" style="display: none; margin-top: 2rem;">
                    <div class="sub-content-header">
                        <h4 id="selected-hospital-name">선택된 병원</h4>
                        <button class="btn btn-primary" onclick="showAddDepartmentModal()">진료과 추가</button>
                    </div>
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>진료과 ID</th>
                                    <th>진료과명</th>
                                    <th>액션</th>
                                </tr>
                            </thead>
                            <tbody id="hospital-departments-table-body">
                                <!-- 동적으로 로드됨 -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>


            <!-- 의사 관리 -->
            <div id="doctors-sub-tab" class="sub-tab-content">
                <div class="sub-content-header">
                    <button class="btn btn-primary" onclick="showAddDoctorModal()">의사 추가</button>
                </div>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>의사 ID</th>
                                <th>병원명</th>
                                <th>진료과</th>
                                <th>의사명</th>
                                <th>진료시간</th>
                                <th>액션</th>
                            </tr>
                        </thead>
                        <tbody id="doctors-table-body">
                            <!-- 동적으로 로드됨 -->
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- 스케줄 관리 -->
            <div id="schedules-sub-tab" class="sub-tab-content">
                <div class="sub-content-header">
                    <select id="schedule-doctor-select">
                        <option value="">의사 선택</option>
                    </select>
                    <input type="date" id="schedule-date">
                    <button class="btn btn-primary" onclick="loadDoctorSchedule()">스케줄 조회</button>
                </div>
                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>시간</th>
                                <th>예약자</th>
                                <th>상태</th>
                                <th>액션</th>
                            </tr>
                        </thead>
                        <tbody id="schedules-table-body">
                            <!-- 동적으로 로드됨 -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- 예약 관리 탭 -->
        <div id="reservations-tab" class="tab-content">
            <div class="content-header">
                <h2>예약 관리</h2>
                <select id="reservation-hospital-filter" onchange="filterReservationsByHospital(this.value)">
                    <option value="">전체 병원</option>
                </select>
            </div>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>예약 ID</th>
                            <th>회원명</th>
                            <th>병원명</th>
                            <th>의사명</th>
                            <th>진료과</th>
                            <th>예약일시</th>
                            <th>상태</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody id="reservations-table-body">
                        <!-- 동적으로 로드됨 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 결제 관리 탭 -->
        <div id="payments-tab" class="tab-content">
            <div class="content-header">
                <h2>결제 관리</h2>
                <select id="payment-hospital-filter" onchange="filterPaymentsByHospital(this.value)">
                    <option value="">전체 병원</option>
                </select>
            </div>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>결제 ID</th>
                            <th>회원명</th>
                            <th>병원명</th>
                            <th>의사명</th>
                            <th>진료과</th>
                            <th>결제금액</th>
                            <th>결제방법</th>
                            <th>예약일시</th>
                            <th>결제일시</th>
                            <th>상태</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody id="payments-table-body">
                        <!-- 동적으로 로드됨 -->
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 통계 관리 탭 -->
        <div id="statistics-tab" class="tab-content">
            <div class="content-header">
                <h2>통계 관리</h2>
                <select id="stats-hospital-filter" onchange="filterStatsByHospital(this.value)">
                    <option value="">전체 병원</option>
                </select>
            </div>
            <div class="stats-container">
                <div class="stats-sidebar">
                    <button class="stats-btn active" data-stats="monthly">월별 예약 현황</button>
                    <button class="stats-btn" data-stats="department">진료과별 예약 건수</button>
                    <button class="stats-btn" data-stats="payment">결제 총 금액</button>
                </div>
                <div class="stats-chart">
                    <canvas id="statsChart"></canvas>
                </div>
            </div>
        </div>

        <!-- 알림 관리 탭 -->
        <div id="notifications-tab" class="tab-content">
            <div class="content-header">
                <h2>알림 관리</h2>
                <button class="btn btn-secondary" onclick="loadFailedNotifications()">실패한 알림만 보기</button>
            </div>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>알림 ID</th>
                            <th>예약 ID</th>
                            <th>알림유형</th>
                            <th>수신자이메일</th>
                            <th>발송일시</th>
                            <th>발송상태</th>
                            <th>액션</th>
                        </tr>
                    </thead>
                    <tbody id="notifications-table-body">
                        <!-- 동적으로 로드됨 -->
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <c:import url="/WEB-INF/views/module/footer.jsp" />

    <!-- 모달들 -->
    <div id="modal-overlay" class="modal-overlay">
        <div class="modal">
            <div class="modal-header">
                <h3 id="modal-title">제목</h3>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            <div class="modal-body" id="modal-body">
                <!-- 동적으로 로드됨 -->
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeModal()">취소</button>
                <button class="btn btn-primary" id="modal-save-btn">저장</button>
            </div>
        </div>
    </div>

    <script src="/script/admin.js"></script>
</body>
</html>
