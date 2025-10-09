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
    <script src="/script/common.js"></script>
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
            <div id="users-pagination" class="pagination-container"></div>
        </div>

        <!-- 병원/진료과/의사 관리 탭 -->
        <div id="hospitals-tab" class="tab-content">
            <div class="content-header">
                <h2>병원/진료과/의사 관리</h2>
                <div class="sub-nav">
                    <button class="sub-nav-btn active" data-sub-tab="hospitals">병원/진료과 관리</button>
                    <button class="sub-nav-btn" data-sub-tab="doctors">의사/스케줄 관리</button>
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
                <div id="hospitals-pagination" class="pagination-container"></div>
                
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
                    <div id="departments-pagination" class="pagination-container"></div>
                </div>
            </div>


            <!-- 의사/스케줄 관리 -->
            <div id="doctors-sub-tab" class="sub-tab-content">
                <div class="sub-content-header">
                    <button class="btn btn-primary" onclick="showAddDoctorModal()">의사 추가</button>
                    <p class="info-text">💡 의사 행을 클릭하면 해당 의사의 스케줄 관리가 표시됩니다.</p>
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
                <div id="doctors-pagination" class="pagination-container"></div>
                
                <!-- 선택된 의사의 스케줄 관리 -->
                <div id="selected-doctor-schedule" style="display: none; margin-top: 2rem;">
                    <div class="sub-content-header">
                        <h4 id="selected-doctor-name">선택된 의사</h4>
                        <div class="schedule-controls">
                            <div class="form-group">
                                <label for="schedule-date-select">날짜 선택:</label>
                                <input type="date" id="schedule-date-select" onchange="loadSelectedDoctorSchedule()">
                            </div>
                        </div>
                    </div>
                    
                    <!-- 선택된 의사 정보 -->
                    <div id="selected-doctor-info" style="margin: 1rem 0; padding: 1rem; background: #f8f9fa; border-radius: 8px;">
                        <h4 id="selected-doctor-details-name"></h4>
                        <p id="selected-doctor-details"></p>
                    </div>
                    
                    <!-- 스케줄 표시 영역 -->
                    <div id="schedule-display">
                        <h4>예약 스케줄</h4>
                        <div id="schedule-grid" class="schedule-grid">
                            <!-- 동적으로 생성됨 -->
                        </div>
                    </div>
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
            <div id="reservations-pagination" class="pagination-container"></div>
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
            <div id="payments-pagination" class="pagination-container"></div>
        </div>

        <!-- 통계 관리 탭 -->
        <div id="statistics-tab" class="tab-content">
            <div class="content-header">
                <h2>통계 관리</h2>
                <div class="filter-controls">
                    <select id="statistics-hospital-filter" onchange="filterStatisticsByHospital(this.value)">
                        <option value="">전체 병원</option>
                    </select>
                </div>
            </div>
            <div class="statistics-container">
                <!-- 월별 예약 현황 -->
                <div class="statistics-section">
                    <h3>월별 예약 현황</h3>
                    <div id="monthly-chart-container"></div>
                </div>
                
                <!-- 일별 예약 현황 -->
                <div class="statistics-section">
                    <h3>일별 예약 현황</h3>
                    <div class="date-selector">
                        <select id="daily-year-select" onchange="loadDailyReservations(this.value, document.getElementById('daily-month-select').value)">
                            <option value="2025">2025년</option>
                            <option value="2024">2024년</option>
                        </select>
                        <select id="daily-month-select" onchange="loadDailyReservations(document.getElementById('daily-year-select').value, this.value)">
                            <option value="01">1월</option>
                            <option value="02">2월</option>
                            <option value="03">3월</option>
                            <option value="04">4월</option>
                            <option value="05">5월</option>
                            <option value="06">6월</option>
                            <option value="07">7월</option>
                            <option value="08">8월</option>
                            <option value="09">9월</option>
                            <option value="10">10월</option>
                            <option value="11">11월</option>
                            <option value="12">12월</option>
                        </select>
                    </div>
                    <div id="daily-chart-container"></div>
                </div>
                
                <!-- 진료과별 예약 건수 -->
                <div class="statistics-section">
                    <h3>진료과별 예약 건수</h3>
                    <div id="department-chart-container"></div>
                </div>
                
                <!-- 결제 통계 -->
                <div class="statistics-section">
                    <h3>결제 통계</h3>
                    <div id="payment-stats-container"></div>
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

    <!-- 병원 수정 모달 -->
    <div id="editHospitalModal" class="modal-overlay" style="display: none;">
        <div class="modal">
            <div class="modal-header">
                <h3>병원 정보 수정</h3>
                <button class="modal-close" onclick="closeEditHospitalModal()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editHospitalForm">
                    <input type="hidden" id="editHospitalId" name="hospitalId">
                    <div class="form-group">
                        <label for="editHospitalName">병원명</label>
                        <input type="text" id="editHospitalName" name="hospitalName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editHospitalAddress">주소</label>
                        <input type="text" id="editHospitalAddress" name="address" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editHospitalPhone">전화번호</label>
                        <input type="text" id="editHospitalPhone" name="phone" class="form-control" oninput="formatPhoneNumber(this)">
                    </div>
                    <div class="form-group">
                        <label for="editHospitalLat">위도</label>
                        <input type="number" id="editHospitalLat" name="lat" class="form-control" step="any">
                    </div>
                    <div class="form-group">
                        <label for="editHospitalLng">경도</label>
                        <input type="number" id="editHospitalLng" name="lng" class="form-control" step="any">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeEditHospitalModal()">취소</button>
                <button class="btn btn-primary" onclick="updateHospital()">수정</button>
            </div>
        </div>
    </div>

    <!-- 진료과 수정 모달 -->
    <div id="editDepartmentModal" class="modal-overlay" style="display: none;">
        <div class="modal">
            <div class="modal-header">
                <h3>진료과 정보 수정</h3>
                <button class="modal-close" onclick="closeEditDepartmentModal()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editDepartmentForm">
                    <input type="hidden" id="editDepartmentId" name="deptId">
                    <input type="hidden" id="editDepartmentHospitalId" name="hospitalId">
                    <div class="form-group">
                        <label>병원명</label>
                        <div id="editDepartmentHospitalName" class="form-control" style="background-color: #f8f9fa; color: #6c757d;"></div>
                    </div>
                    <div class="form-group">
                        <label for="editDepartmentName">진료과명</label>
                        <input type="text" id="editDepartmentName" name="deptName" class="form-control" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeEditDepartmentModal()">취소</button>
                <button class="btn btn-primary" onclick="updateDepartment()">수정</button>
            </div>
        </div>
    </div>

    <!-- 의사 수정 모달 -->
    <div id="editDoctorModal" class="modal-overlay" style="display: none;">
        <div class="modal">
            <div class="modal-header">
                <h3>의사 정보 수정</h3>
                <button class="modal-close" onclick="closeEditDoctorModal()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editDoctorForm">
                    <input type="hidden" id="editDoctorId" name="doctorId">
                    <input type="hidden" id="editDoctorHospitalId" name="hospitalId">
                    <input type="hidden" id="editDoctorDeptId" name="deptId">
                    <div class="form-group">
                        <label>병원명</label>
                        <div id="editDoctorHospitalName" class="form-control" style="background-color: #f8f9fa; color: #6c757d;"></div>
                    </div>
                    <div class="form-group">
                        <label>진료과명</label>
                        <div id="editDoctorDeptName" class="form-control" style="background-color: #f8f9fa; color: #6c757d;"></div>
                    </div>
                    <div class="form-group">
                        <label for="editDoctorName">의사명</label>
                        <input type="text" id="editDoctorName" name="name" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="editDoctorAvailableTime">진료시간</label>
                        <input type="text" id="editDoctorAvailableTime" name="availableTime" class="form-control" placeholder="예: 09:00-17:00" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closeEditDoctorModal()">취소</button>
                <button class="btn btn-primary" onclick="updateDoctor()">수정</button>
            </div>
        </div>
    </div>

    <script src="/script/admin.js"></script>
</body>
</html>
