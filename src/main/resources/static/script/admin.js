// 관리자 페이지 JavaScript

// 전역 변수
let currentTab = 'users';
let currentSubTab = 'hospitals';
let statsChart = null;

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeAdminPage();
});

// 관리자 페이지 초기화
function initializeAdminPage() {
    console.log('🚀 관리자 페이지 초기화 시작');
    setupTabNavigation();
    setupSubTabNavigation();
    setupStatsNavigation();
    loadInitialData();
    console.log('✅ 관리자 페이지 초기화 완료');
}

// 탭 네비게이션 설정
function setupTabNavigation() {
    const navTabs = document.querySelectorAll('.nav-tab');
    const tabContents = document.querySelectorAll('.tab-content');
    
    navTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const targetTab = this.getAttribute('data-tab');
            
            // 활성 탭 변경
            navTabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            // 탭 콘텐츠 변경
            tabContents.forEach(content => content.classList.remove('active'));
            document.getElementById(targetTab + '-tab').classList.add('active');
            
            currentTab = targetTab;
            
            // 탭별 데이터 로드
            loadTabData(targetTab);
        });
    });
}

// 서브 탭 네비게이션 설정
function setupSubTabNavigation() {
    const subNavBtns = document.querySelectorAll('.sub-nav-btn');
    const subTabContents = document.querySelectorAll('.sub-tab-content');
    
    subNavBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const targetSubTab = this.getAttribute('data-sub-tab');
            
            // 활성 서브 탭 변경
            subNavBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 서브 탭 콘텐츠 변경
            subTabContents.forEach(content => content.classList.remove('active'));
            document.getElementById(targetSubTab + '-sub-tab').classList.add('active');
            
            currentSubTab = targetSubTab;
            
            // 서브 탭별 데이터 로드
            loadSubTabData(targetSubTab);
        });
    });
}

// 통계 네비게이션 설정
function setupStatsNavigation() {
    const statsBtns = document.querySelectorAll('.stats-btn');
    
    statsBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const statsType = this.getAttribute('data-stats');
            
            // 활성 통계 버튼 변경
            statsBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 통계 차트 로드
            loadStatsChart(statsType);
        });
    });
}

// 초기 데이터 로드
function loadInitialData() {
    console.log('📊 초기 데이터 로드 시작');
    loadUsers();
    loadHospitals();
    loadDepartments();
    loadDoctors();
    loadReservations();
    loadPayments();
    loadNotifications();
    // loadStatsChart('monthly'); // 기존 차트 비활성화
    loadHospitalFilters();
    console.log('📊 초기 데이터 로드 완료');
}

// 탭별 데이터 로드
function loadTabData(tabName) {
    switch(tabName) {
        case 'users':
            loadUsers();
            break;
        case 'hospitals':
            loadHospitals();
            break;
        case 'reservations':
            loadReservations();
            break;
        case 'payments':
            loadPayments();
            break;
        case 'statistics':
            loadStatistics();
            loadHospitalFilterForStatistics();
            break;
        case 'notifications':
            loadNotifications();
            break;
    }
}

// 서브 탭별 데이터 로드
function loadSubTabData(subTabName) {
    switch(subTabName) {
        case 'hospitals':
            loadHospitals();
            break;
        case 'doctors':
            loadDoctors();
            break;
    }
}

// 회원 목록 로드
async function loadUsers() {
    try {
        const response = await fetch('/admin/users');
        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }
        const users = await response.json();
        
        // 회원관리에서는 모든 회원 표시 (어드민 포함)
        const tbody = document.getElementById('users-table-body');
        tbody.innerHTML = '';
        
        if (users && users.length > 0) {
            users.forEach(user => {
                const row = document.createElement('tr');
                const withdrawalStatusBadge = getWithdrawalStatusBadge(user.withdrawalStatus);
                const actionButtons = getActionButtons(user);
                
                row.innerHTML = `
                    <td>${user.userId}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>${user.phone || '-'}</td>
                    <td><span class="status-badge ${user.role === 'ADMIN' ? 'status-info' : 'status-success'}">${user.role}</span></td>
                    <td>${withdrawalStatusBadge}</td>
                    <td>${formatDate(user.createdAt)}</td>
                    <td>${actionButtons}</td>
                `;
                tbody.appendChild(row);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center">등록된 회원이 없습니다.</td></tr>';
        }
    } catch (error) {
        console.error('회원 목록 로드 실패:', error);
        showAlert('회원 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 병원 목록 로드
async function loadHospitals() {
    try {
        const response = await fetch('/admin/hospitals');
        const hospitals = await response.json();
        
        const tbody = document.getElementById('hospitals-table-body');
        tbody.innerHTML = '';
        
        hospitals.forEach(hospital => {
            const row = document.createElement('tr');
            row.style.cursor = 'pointer';
            row.onclick = () => selectHospitalForDepartment(hospital.hospitalId, hospital.hospitalName);
            row.innerHTML = `
                <td>${hospital.hospitalId}</td>
                <td>${hospital.hospitalName}</td>
                <td>${hospital.address}</td>
                <td>${hospital.phone || '-'}</td>
                <td>${formatDate(hospital.createdAt)}</td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="event.stopPropagation(); editHospital(${hospital.hospitalId})">수정</button>
                    <button class="btn btn-sm btn-danger" onclick="event.stopPropagation(); deleteHospital(${hospital.hospitalId})">삭제</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('병원 목록 로드 실패:', error);
        showAlert('병원 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 진료과 목록 로드
async function loadDepartments() {
    try {
        const response = await fetch('/admin/departments');
        const departments = await response.json();
        
        const tbody = document.getElementById('departments-table-body');
        if (tbody) {
            tbody.innerHTML = '';
            
            departments.forEach(dept => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${dept.deptId}</td>
                    <td>${dept.hospital ? dept.hospital.hospitalName : '-'}</td>
                    <td>${dept.deptName}</td>
                    <td>${formatDate(dept.createdAt)}</td>
                    <td>
                        <button class="btn btn-sm btn-secondary" onclick="editDepartment(${dept.deptId})">수정</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteDepartment(${dept.deptId})">삭제</button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }
    } catch (error) {
        console.error('진료과 목록 로드 실패:', error);
        showAlert('진료과 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 병원 선택 (진료과 관리용)
async function selectHospitalForDepartment(hospitalId, hospitalName) {
    try {
        // 선택된 병원 정보 표시
        document.getElementById('selected-hospital-name').textContent = hospitalName;
        document.getElementById('selected-hospital-departments').style.display = 'block';
        
        // 해당 병원의 진료과 목록 로드
        const response = await fetch(`/admin/departments/hospital/${hospitalId}`);
        const departments = await response.json();
        
        const tbody = document.getElementById('hospital-departments-table-body');
        tbody.innerHTML = '';
        
        departments.forEach(dept => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${dept.deptId}</td>
                <td>${dept.deptName}</td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="editDepartment(${dept.deptId})">수정</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteDepartment(${dept.deptId})">삭제</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('진료과 목록 로드 실패:', error);
        showAlert('진료과 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 의사 목록 로드
async function loadDoctors() {
    try {
        const response = await fetch('/admin/doctors');
        const doctors = await response.json();
        
        // 디버깅: API 응답 확인
        console.log('의사 데이터:', doctors);
        if (doctors.length > 0) {
            console.log('첫 번째 의사 데이터:', doctors[0]);
        }
        
        const tbody = document.getElementById('doctors-table-body');
        tbody.innerHTML = '';
        
        doctors.forEach(doctor => {
            const row = document.createElement('tr');
            row.style.cursor = 'pointer';
            row.onclick = () => selectDoctorForSchedule(doctor);
            row.innerHTML = `
                <td>${doctor.doctorId}</td>
                <td>${doctor.hospital ? doctor.hospital.hospitalName : '-'}</td>
                <td>${doctor.department ? doctor.department.deptName : '-'}</td>
                <td>${doctor.name}</td>
                <td>${doctor.availableTime || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="event.stopPropagation(); editDoctor(${doctor.doctorId})">수정</button>
                    <button class="btn btn-sm btn-danger" onclick="event.stopPropagation(); deleteDoctor(${doctor.doctorId})">삭제</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('의사 목록 로드 실패:', error);
        showAlert('의사 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 예약 목록 로드
async function loadReservations() {
    try {
        const response = await fetch('/admin/reservations');
        const reservations = await response.json();
        
        // 디버깅: API 응답 확인
        console.log('예약 데이터:', reservations);
        if (reservations.length > 0) {
            console.log('첫 번째 예약 데이터:', reservations[0]);
        }
        
        const tbody = document.getElementById('reservations-table-body');
        tbody.innerHTML = '';
        
        reservations.forEach(reservation => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${reservation.resId}</td>
                <td>${reservation.user ? reservation.user.name : '-'}</td>
                <td>${reservation.hospital ? reservation.hospital.hospitalName : '-'}</td>
                <td>${reservation.doctor ? reservation.doctor.name : '-'}</td>
                <td>${reservation.department ? reservation.department.deptName : '-'}</td>
                <td>${formatDateTime(reservation.resDate)}</td>
                <td><span class="status-badge ${getStatusClass(reservation.status)}">${reservation.status}</span></td>
                <td>
                    <select class="form-control" onchange="updateReservationStatus(${reservation.resId}, this.value)">
                        <option value="예약중" ${reservation.status === '예약중' ? 'selected' : ''}>예약중</option>
                        <option value="완료" ${reservation.status === '완료' ? 'selected' : ''}>완료</option>
                        <option value="취소" ${reservation.status === '취소' ? 'selected' : ''}>취소</option>
                    </select>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('예약 목록 로드 실패:', error);
        showAlert('예약 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 결제 목록 로드
async function loadPayments() {
    try {
        const response = await fetch('/admin/payments');
        const payments = await response.json();
        
        // 디버깅: API 응답 확인
        console.log('결제 데이터:', payments);
        if (payments.length > 0) {
            console.log('첫 번째 결제 데이터:', payments[0]);
        }
        
        const tbody = document.getElementById('payments-table-body');
        tbody.innerHTML = '';
        
        payments.forEach(payment => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${payment.payId}</td>
                <td>${payment.reservation && payment.reservation.user ? payment.reservation.user.name : '-'}</td>
                <td>${payment.reservation && payment.reservation.hospital ? payment.reservation.hospital.hospitalName : '-'}</td>
                <td>${payment.reservation && payment.reservation.doctor ? payment.reservation.doctor.name : '-'}</td>
                <td>${payment.reservation && payment.reservation.department ? payment.reservation.department.deptName : '-'}</td>
                <td>${payment.amount ? payment.amount.toLocaleString() + '원' : '-'}</td>
                <td>${payment.method || '-'}</td>
                <td>${payment.reservation ? formatDateTime(payment.reservation.resDate) : '-'}</td>
                <td>${formatDateTime(payment.createdAt)}</td>
                <td><span class="status-badge ${getPaymentStatusClass(payment.status)}">${payment.status}</span></td>
                <td>
                    <select class="form-control" onchange="updatePaymentStatus(${payment.payId}, this.value)">
                        <option value="대기" ${payment.status === '대기' ? 'selected' : ''}>대기</option>
                        <option value="완료" ${payment.status === '완료' ? 'selected' : ''}>완료</option>
                        <option value="취소" ${payment.status === '취소' ? 'selected' : ''}>취소</option>
                    </select>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('결제 목록 로드 실패:', error);
        showAlert('결제 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 알림 목록 로드
async function loadNotifications() {
    try {
        const response = await fetch('/admin/notifications');
        const notifications = await response.json();
        
        const tbody = document.getElementById('notifications-table-body');
        tbody.innerHTML = '';
        
        notifications.forEach(notification => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${notification.notiId}</td>
                <td>${notification.resId}</td>
                <td>${notification.type}</td>
                <td>${notification.recipient}</td>
                <td>${formatDateTime(notification.sentAt)}</td>
                <td><span class="status-badge ${notification.status === '성공' ? 'status-success' : 'status-danger'}">${notification.status}</span></td>
                <td>
                    ${notification.status === '실패' ? 
                        `<button class="btn btn-sm btn-warning" onclick="resendNotification(${notification.notiId})">재발송</button>` : 
                        '-'
                    }
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('알림 목록 로드 실패:', error);
        showAlert('알림 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 통계 차트 로드
async function loadStatsChart(type, hospitalId = null) {
    try {
        let url = `/admin/stats/${type}`;
        if (hospitalId) {
            url += `?hospitalId=${hospitalId}`;
        }
        const response = await fetch(url);
        const data = await response.json();
        
        const ctx = document.getElementById('statsChart').getContext('2d');
        
        if (statsChart) {
            statsChart.destroy();
        }
        
        let chartConfig = {};
        
        switch(type) {
            case 'monthly':
                chartConfig = {
                    type: 'line',
                    data: {
                        labels: data.labels || ['1월', '2월', '3월', '4월', '5월', '6월'],
                        datasets: [{
                            label: '월별 예약 현황',
                            data: data.values || [12, 19, 3, 5, 2, 3],
                            borderColor: 'rgb(75, 192, 192)',
                            tension: 0.1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                };
                break;
            case 'department':
                chartConfig = {
                    type: 'bar',
                    data: {
                        labels: data.labels || ['정형외과', '내과', '소아과', '신경외과', '가정의학과'],
                        datasets: [{
                            label: '진료과별 예약 건수',
                            data: data.values || [12, 19, 3, 5, 2],
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                };
                break;
            case 'payment':
                chartConfig = {
                    type: 'doughnut',
                    data: {
                        labels: data.labels || ['완료', '대기', '취소'],
                        datasets: [{
                            data: data.values || [300, 50, 100],
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(255, 99, 132, 0.2)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(255, 99, 132, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true
                    }
                };
                break;
        }
        
        statsChart = new Chart(ctx, chartConfig);
    } catch (error) {
        console.error('통계 차트 로드 실패:', error);
        showAlert('통계 차트를 불러오는데 실패했습니다.', 'danger');
    }
}

// 상태 업데이트 함수들
async function updateReservationStatus(resId, status) {
    try {
        const response = await fetch(`/admin/reservation/${resId}/status?status=${status}`, {
            method: 'PUT'
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            loadReservations();
        } else {
            showAlert(result.message || '예약 상태 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('예약 상태 업데이트 실패:', error);
        showAlert('예약 상태 업데이트에 실패했습니다.', 'danger');
    }
}

async function updatePaymentStatus(payId, status) {
    try {
        const response = await fetch(`/admin/payment/${payId}/status?status=${status}`, {
            method: 'PUT'
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            loadPayments();
        } else {
            showAlert(result.message || '결제 상태 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('결제 상태 업데이트 실패:', error);
        showAlert('결제 상태 업데이트에 실패했습니다.', 'danger');
    }
}

async function updateUserRole(userId, role) {
    try {
        const response = await fetch(`/admin/user/${userId}/role?role=${role}`, {
            method: 'PUT'
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            loadUsers();
        } else {
            showAlert(result.message || '사용자 권한 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('사용자 권한 업데이트 실패:', error);
        showAlert('사용자 권한 업데이트에 실패했습니다.', 'danger');
    }
}

async function resendNotification(notiId) {
    try {
        const response = await fetch(`/admin/notification/resend/${notiId}`, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            loadNotifications();
        } else {
            showAlert(result.message || '알림 재발송에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('알림 재발송 실패:', error);
        showAlert('알림 재발송에 실패했습니다.', 'danger');
    }
}

// 유틸리티 함수들
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR');
}

function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR');
}

function getStatusClass(status) {
    switch(status) {
        case '예약중': return 'status-warning';
        case '완료': return 'status-success';
        case '취소': return 'status-danger';
        default: return 'status-info';
    }
}

function getPaymentStatusClass(status) {
    switch(status) {
        case '대기': return 'status-warning';
        case '완료': return 'status-success';
        case '취소': return 'status-danger';
        default: return 'status-info';
    }
}

function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    
    const main = document.querySelector('.admin-main');
    main.insertBefore(alertDiv, main.firstChild);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// 탈퇴 상태 배지 생성
function getWithdrawalStatusBadge(status) {
    switch(status) {
        case 'ACTIVE':
            return '<span class="status-badge status-success">활성</span>';
        case 'WITHDRAWAL_REQUESTED':
            return '<span class="status-badge status-warning">탈퇴요청</span>';
        case 'WITHDRAWN':
            return '<span class="status-badge status-danger">탈퇴완료</span>';
        default:
            return '<span class="status-badge status-info">알 수 없음</span>';
    }
}

// 사용자별 액션 버튼 생성
function getActionButtons(user) {
    let buttons = `
        <button class="btn btn-sm btn-primary" onclick="viewUser(${user.userId})">상세보기</button>
        <button class="btn btn-sm btn-secondary" onclick="editUser(${user.userId})">수정</button>
        <button class="btn btn-sm btn-warning" onclick="updateUserRole(${user.userId}, '${user.role === 'ADMIN' ? 'PATIENT' : 'ADMIN'}')">권한</button>
    `;
    
    if (user.withdrawalStatus === 'WITHDRAWAL_REQUESTED') {
        buttons += `<button class="btn btn-sm btn-danger" onclick="approveWithdrawal(${user.userId})">탈퇴승인</button>`;
    } else {
        buttons += `<button class="btn btn-sm btn-secondary" onclick="deleteUser(${user.userId})" disabled>탈퇴승인</button>`;
    }
    
    return buttons;
}

// 모달 관련 함수들
function showModal(title, content) {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = content;
    
    // 모달 버튼들 표시
    const modalFooter = document.querySelector('.modal-footer');
    if (modalFooter) {
        modalFooter.style.display = 'flex';
    }
    
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
    }
    
    document.getElementById('modal-overlay').style.display = 'block';
}

function closeModal() {
    document.getElementById('modal-overlay').style.display = 'none';
    
    // 모달 버튼 상태 초기화
    const modalFooter = document.querySelector('.modal-footer');
    if (modalFooter) {
        modalFooter.style.display = 'flex';
    }
    
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
    }
}

// 모달 외부 클릭 시 닫기
document.getElementById('modal-overlay').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

// 모달 관련 함수들
function showAddUserModal() {
    const content = `
        <form id="addUserForm">
            <div class="form-group">
                <label for="username">아이디</label>
                <input type="text" id="username" name="username" class="form-control" required>
                <div class="error-message" id="usernameError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
            </div>
            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" class="form-control" required>
                <div class="error-message" id="passwordError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
            </div>
            <div class="form-group">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" class="form-control" required>
                <div class="error-message" id="nameError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
            </div>
            <div class="form-group">
                <label for="email">이메일</label>
                <input type="email" id="email" name="email" class="form-control" required>
                <div class="error-message" id="emailError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
            </div>
            <div class="form-group">
                <label for="phone">전화번호</label>
                <input type="tel" id="phone" name="phone" class="form-control">
                <div class="error-message" id="phoneError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
            </div>
            <div class="form-group">
                <label for="role">권한</label>
                <select id="role" name="role" class="form-control" required>
                    <option value="PATIENT">일반 사용자</option>
                    <option value="ADMIN">관리자</option>
                </select>
            </div>
        </form>
    `;
    
    showModal('회원 추가', content);
    
    // 실시간 유효성 검사 이벤트 리스너 추가
    setupUserFormValidation();
    
    // 모달 버튼 확인 및 이벤트 리스너 설정
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
        saveBtn.onclick = function() {
            createUser();
        };
    }
}

function showAddHospitalModal() {
    const content = `
        <form id="addHospitalForm">
            <div class="form-group">
                <label for="hospitalName">병원명</label>
                <input type="text" id="hospitalName" name="hospitalName" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="address">주소</label>
                <input type="text" id="address" name="address" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="phone">전화번호</label>
                <input type="tel" id="phone" name="phone" class="form-control">
            </div>
            <div class="form-group">
                <label for="lat">위도</label>
                <input type="number" id="lat" name="lat" class="form-control" step="any" placeholder="예: 37.5665">
            </div>
            <div class="form-group">
                <label for="lng">경도</label>
                <input type="number" id="lng" name="lng" class="form-control" step="any" placeholder="예: 126.9780">
            </div>
        </form>
    `;
    
    showModal('병원 추가', content);
    
    // 모달 버튼 확인 및 이벤트 리스너 설정
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
        saveBtn.onclick = function() {
            createHospital();
        };
    }
}

function showAddDepartmentModal() {
    // 현재 선택된 병원이 있는지 확인
    const selectedHospitalName = document.getElementById('selected-hospital-name').textContent;
    if (!selectedHospitalName || selectedHospitalName === '선택된 병원') {
        showAlert('❌ 먼저 병원을 선택해주세요.\n병원 목록에서 병원을 클릭하여 선택한 후 진료과를 추가할 수 있습니다.', 'warning');
        return;
    }
    
    const content = `
        <form id="addDepartmentForm">
            <div class="form-group">
                <label>병원</label>
                <input type="text" value="${selectedHospitalName}" class="form-control" readonly>
            </div>
            <div class="form-group">
                <label for="deptName">진료과명 <span style="color: red;">*</span></label>
                <input type="text" id="deptName" name="deptName" class="form-control" required 
                       placeholder="예: 내과, 외과, 소아과, 정형외과, 신경외과" 
                       maxlength="50">
                <small class="form-text text-muted">
                    진료과명은 50자 이내로 입력해주세요.
                </small>
            </div>
        </form>
    `;
    
    showModal('진료과 추가', content);
    
    // 모달 버튼 확인 및 이벤트 리스너 설정
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        console.log('✅ 진료과 추가 모달 - 저장 버튼 찾음');
        saveBtn.style.display = 'inline-block';
        saveBtn.onclick = function() {
            createDepartment();
        };
    } else {
        console.error('❌ 진료과 추가 모달 - 저장 버튼을 찾을 수 없음');
    }
}

function showAddDoctorModal() {
    const content = `
        <form id="addDoctorForm">
            <div class="form-group">
                <label for="doctorHospital">병원</label>
                <select id="doctorHospital" name="hospitalId" class="form-control" required onchange="loadDepartmentsForDoctor()">
                    <option value="">병원을 선택하세요</option>
                </select>
            </div>
            <div class="form-group">
                <label for="doctorDepartment">진료과</label>
                <select id="doctorDepartment" name="deptId" class="form-control" required>
                    <option value="">먼저 병원을 선택하세요</option>
                </select>
            </div>
            <div class="form-group">
                <label for="doctorName">의사명</label>
                <input type="text" id="doctorName" name="name" class="form-control" required>
            </div>
            <div class="form-group">
                <label for="availableTime">진료시간</label>
                <input type="text" id="availableTime" name="availableTime" class="form-control" required placeholder="예: 평일 09:00-18:00">
            </div>
        </form>
    `;
    
    showModal('의사 추가', content);
    
    // 병원 목록 로드
    loadHospitalsForDoctor();
    
    // 모달 버튼 확인 및 이벤트 리스너 설정
    const saveBtn = document.getElementById('modal-save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
        saveBtn.onclick = function() {
            createDoctor();
        };
    }
}

// 회원 생성
async function createUser() {
    try {
        const form = document.getElementById('addUserForm');
        const formData = new FormData(form);
        const userData = Object.fromEntries(formData.entries());
        
        // 조건 필터링 검증
        const validationResult = validateUserData(userData);
        if (!validationResult.isValid) {
            showAlert(validationResult.message, 'danger');
            return;
        }
        
        const response = await fetch('/admin/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeModal();
            loadUsers();
        } else {
            showAlert(result.message || '회원 등록에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('회원 생성 실패:', error);
        showAlert('회원 등록에 실패했습니다.', 'danger');
    }
}

// 회원 폼 실시간 유효성 검사 설정
function setupUserFormValidation() {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    
    // 아이디 실시간 검증
    if (usernameInput) {
        usernameInput.addEventListener('blur', validateUsername);
        usernameInput.addEventListener('input', () => hideFieldError('usernameError'));
    }
    
    // 비밀번호 실시간 검증
    if (passwordInput) {
        passwordInput.addEventListener('blur', validatePassword);
        passwordInput.addEventListener('input', () => hideFieldError('passwordError'));
    }
    
    // 이름 실시간 검증
    if (nameInput) {
        nameInput.addEventListener('blur', validateName);
        nameInput.addEventListener('input', () => hideFieldError('nameError'));
    }
    
    // 이메일 실시간 검증
    if (emailInput) {
        emailInput.addEventListener('blur', validateEmail);
        emailInput.addEventListener('input', () => hideFieldError('emailError'));
    }
    
    // 전화번호 실시간 검증 및 자동 포맷팅
    if (phoneInput) {
        phoneInput.addEventListener('blur', validatePhone);
        phoneInput.addEventListener('input', () => {
            hideFieldError('phoneError');
            formatPhoneNumber(phoneInput);
        });
    }
}

// 회원 수정 폼 실시간 유효성 검사 설정
function setupEditUserFormValidation() {
    const editPasswordInput = document.getElementById('editPassword');
    const editNameInput = document.getElementById('editName');
    const editEmailInput = document.getElementById('editEmail');
    const editPhoneInput = document.getElementById('editPhone');
    
    // 비밀번호 실시간 검증 (수정용)
    if (editPasswordInput) {
        editPasswordInput.addEventListener('blur', validateEditPassword);
        editPasswordInput.addEventListener('input', () => hideFieldError('editPasswordError'));
    }
    
    // 이름 실시간 검증 (수정용)
    if (editNameInput) {
        editNameInput.addEventListener('blur', validateEditName);
        editNameInput.addEventListener('input', () => hideFieldError('editNameError'));
    }
    
    // 이메일 실시간 검증 (수정용)
    if (editEmailInput) {
        editEmailInput.addEventListener('blur', validateEditEmail);
        editEmailInput.addEventListener('input', () => hideFieldError('editEmailError'));
    }
    
    // 전화번호 실시간 검증 및 자동 포맷팅 (수정용)
    if (editPhoneInput) {
        editPhoneInput.addEventListener('blur', validateEditPhone);
        editPhoneInput.addEventListener('input', () => {
            hideFieldError('editPhoneError');
            formatPhoneNumber(editPhoneInput);
        });
    }
}

// 개별 필드 검증 함수들
function validateUsername() {
    const username = document.getElementById('username').value.trim();
    if (!username) {
        showFieldError('usernameError', '아이디를 입력해주세요.');
        return false;
    }
    const usernameRegex = /^[a-zA-Z0-9]{4,20}$/;
    if (!usernameRegex.test(username)) {
        showFieldError('usernameError', '아이디는 영문, 숫자만 사용 가능하며 4-20자여야 합니다.');
        return false;
    }
    hideFieldError('usernameError');
    return true;
}

function validatePassword() {
    const password = document.getElementById('password').value;
    if (!password) {
        showFieldError('passwordError', '비밀번호를 입력해주세요.');
        return false;
    }
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(password)) {
        showFieldError('passwordError', '비밀번호는 최소 8자 이상이며 영문과 숫자를 포함해야 합니다.');
        return false;
    }
    hideFieldError('passwordError');
    return true;
}

function validateName() {
    const name = document.getElementById('name').value.trim();
    if (!name) {
        showFieldError('nameError', '이름을 입력해주세요.');
        return false;
    }
    const nameRegex = /^[가-힣a-zA-Z\s]{2,10}$/;
    if (!nameRegex.test(name)) {
        showFieldError('nameError', '이름은 한글 또는 영문으로 2-10자여야 합니다.');
        return false;
    }
    hideFieldError('nameError');
    return true;
}

function validateEmail() {
    const email = document.getElementById('email').value.trim();
    if (!email) {
        showFieldError('emailError', '이메일을 입력해주세요.');
        return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showFieldError('emailError', '올바른 이메일 형식이 아닙니다.');
        return false;
    }
    hideFieldError('emailError');
    return true;
}

function validatePhone() {
    const phone = document.getElementById('phone').value.trim();
    if (phone) { // 전화번호는 선택사항이므로 입력된 경우에만 검증
        // 하이픈을 제거한 숫자만으로 검증
        const phoneNumbers = phone.replace(/-/g, '');
        const phoneRegex = /^01[0-9][0-9]{8}$/;
        if (!phoneRegex.test(phoneNumbers)) {
            showFieldError('phoneError', '올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)');
            return false;
        }
    }
    hideFieldError('phoneError');
    return true;
}

// 회원 수정용 개별 필드 검증 함수들
function validateEditPassword() {
    const password = document.getElementById('editPassword').value;
    if (password) { // 비밀번호는 선택사항이므로 입력된 경우에만 검증
        const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).{8,}$/;
        if (!passwordRegex.test(password)) {
            showFieldError('editPasswordError', '비밀번호는 최소 8자 이상이며 영문과 숫자를 포함해야 합니다.');
            return false;
        }
    }
    hideFieldError('editPasswordError');
    return true;
}

function validateEditName() {
    const name = document.getElementById('editName').value.trim();
    if (!name) {
        showFieldError('editNameError', '이름을 입력해주세요.');
        return false;
    }
    const nameRegex = /^[가-힣a-zA-Z\s]{2,10}$/;
    if (!nameRegex.test(name)) {
        showFieldError('editNameError', '이름은 한글 또는 영문으로 2-10자여야 합니다.');
        return false;
    }
    hideFieldError('editNameError');
    return true;
}

function validateEditEmail() {
    const email = document.getElementById('editEmail').value.trim();
    if (!email) {
        showFieldError('editEmailError', '이메일을 입력해주세요.');
        return false;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        showFieldError('editEmailError', '올바른 이메일 형식이 아닙니다.');
        return false;
    }
    hideFieldError('editEmailError');
    return true;
}

function validateEditPhone() {
    const phone = document.getElementById('editPhone').value.trim();
    if (phone) { // 전화번호는 선택사항이므로 입력된 경우에만 검증
        // 하이픈을 제거한 숫자만으로 검증
        const phoneNumbers = phone.replace(/-/g, '');
        const phoneRegex = /^01[0-9][0-9]{8}$/;
        if (!phoneRegex.test(phoneNumbers)) {
            showFieldError('editPhoneError', '올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)');
            return false;
        }
    }
    hideFieldError('editPhoneError');
    return true;
}

// 전화번호 자동 포맷팅 함수
function formatPhoneNumber(input) {
    let value = input.value.replace(/\D/g, ''); // 숫자만 추출
    
    if (value.length >= 11) {
        // 01012345678 -> 010-1234-5678
        value = value.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    } else if (value.length >= 7) {
        // 0101234 -> 010-1234
        value = value.replace(/(\d{3})(\d{4})/, '$1-$2');
    } else if (value.length >= 3) {
        // 010 -> 010-
        value = value.replace(/(\d{3})/, '$1-');
    }
    
    input.value = value;
}

// 에러 메시지 표시/숨김 함수들
function showFieldError(errorId, message) {
    const errorElement = document.getElementById(errorId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}

function hideFieldError(errorId) {
    const errorElement = document.getElementById(errorId);
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

// 회원 데이터 검증 (전체 폼 검증용)
function validateUserData(userData) {
    // 모든 필드 검증
    const isUsernameValid = validateUsername();
    const isPasswordValid = validatePassword();
    const isNameValid = validateName();
    const isEmailValid = validateEmail();
    const isPhoneValid = validatePhone();
    
    if (!isUsernameValid || !isPasswordValid || !isNameValid || !isEmailValid || !isPhoneValid) {
        return {
            isValid: false,
            message: '입력 정보를 확인해주세요.'
        };
    }
    
    return { isValid: true };
}

// 회원 상세보기
async function viewUser(userId) {
    try {
        const response = await fetch(`/admin/user/${userId}`);
        const user = await response.json();
        
        if (response.ok) {
            const content = `
                <div class="user-detail">
                    <div class="detail-row">
                        <label>사용자 ID:</label>
                        <span>${user.userId}</span>
                    </div>
                    <div class="detail-row">
                        <label>아이디:</label>
                        <span>${user.username}</span>
                    </div>
                    <div class="detail-row">
                        <label>이름:</label>
                        <span>${user.name}</span>
                    </div>
                    <div class="detail-row">
                        <label>이메일:</label>
                        <span>${user.email}</span>
                    </div>
                    <div class="detail-row">
                        <label>전화번호:</label>
                        <span>${user.phone || '-'}</span>
                    </div>
                    <div class="detail-row">
                        <label>권한:</label>
                        <span class="status-badge ${user.role === 'ADMIN' ? 'status-info' : 'status-success'}">${user.role}</span>
                    </div>
                    <div class="detail-row">
                        <label>탈퇴 상태:</label>
                        ${getWithdrawalStatusBadge(user.withdrawalStatus)}
                    </div>
                    <div class="detail-row">
                        <label>가입일:</label>
                        <span>${formatDateTime(user.createdAt)}</span>
                    </div>
                </div>
            `;
            
            showModal('회원 상세정보', content);
            document.getElementById('modal-save-btn').style.display = 'none';
        } else {
            showAlert('회원 정보를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('회원 상세보기 실패:', error);
        showAlert('회원 정보를 불러오는데 실패했습니다.', 'danger');
    }
}

// 회원 수정
async function editUser(userId) {
    try {
        const response = await fetch(`/admin/user/${userId}`);
        const user = await response.json();
        
        if (response.ok) {
            const content = `
                <form id="editUserForm">
                    <div class="form-group">
                        <label for="editUsername">아이디</label>
                        <input type="text" id="editUsername" name="username" class="form-control" value="${user.username}" readonly>
                    </div>
                    <div class="form-group">
                        <label for="editPassword">비밀번호</label>
                        <input type="password" id="editPassword" name="password" class="form-control" placeholder="새 비밀번호 (변경시에만 입력)">
                        <div class="error-message" id="editPasswordError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                    </div>
                    <div class="form-group">
                        <label for="editName">이름</label>
                        <input type="text" id="editName" name="name" class="form-control" value="${user.name}" required>
                        <div class="error-message" id="editNameError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                    </div>
                    <div class="form-group">
                        <label for="editEmail">이메일</label>
                        <input type="email" id="editEmail" name="email" class="form-control" value="${user.email}" required>
                        <div class="error-message" id="editEmailError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                    </div>
                    <div class="form-group">
                        <label for="editPhone">전화번호</label>
                        <input type="tel" id="editPhone" name="phone" class="form-control" value="${user.phone || ''}">
                        <div class="error-message" id="editPhoneError" style="display: none; color: #dc3545; font-size: 0.875rem; margin-top: 5px;"></div>
                    </div>
                </form>
            `;
            
            showModal('회원 정보 수정', content);
            
            // 실시간 유효성 검사 이벤트 리스너 추가
            setupEditUserFormValidation();
            
            // 모달 버튼 확인 및 이벤트 리스너 설정
            const saveBtn = document.getElementById('modal-save-btn');
            if (saveBtn) {
                saveBtn.style.display = 'inline-block';
                saveBtn.onclick = function() {
                    updateUser(userId);
                };
            }
        } else {
            showAlert('회원 정보를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('회원 수정 모달 로드 실패:', error);
        showAlert('회원 정보를 불러오는데 실패했습니다.', 'danger');
    }
}

// 회원 정보 업데이트
async function updateUser(userId) {
    try {
        const form = document.getElementById('editUserForm');
        const formData = new FormData(form);
        const userData = Object.fromEntries(formData.entries());
        
        // 빈 비밀번호 제거
        if (!userData.password || userData.password.trim() === '') {
            delete userData.password;
        }
        
        // 조건 필터링 검증 (비밀번호가 있는 경우만)
        if (userData.password) {
            const validationResult = validateUserData(userData);
            if (!validationResult.isValid) {
                showAlert(validationResult.message, 'danger');
                return;
            }
        } else {
            // 비밀번호 없이 검증
            const validationResult = validateUserDataWithoutPassword(userData);
            if (!validationResult.isValid) {
                showAlert(validationResult.message, 'danger');
                return;
            }
        }
        
        const response = await fetch(`/admin/user/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeModal();
            loadUsers();
        } else {
            showAlert(result.message || '회원 정보 수정에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('회원 수정 실패:', error);
        showAlert('회원 정보 수정에 실패했습니다.', 'danger');
    }
}

// 비밀번호 없이 회원 데이터 검증
function validateUserDataWithoutPassword(userData) {
    // 모든 필드 검증 (수정용)
    const isPasswordValid = validateEditPassword();
    const isNameValid = validateEditName();
    const isEmailValid = validateEditEmail();
    const isPhoneValid = validateEditPhone();
    
    if (!isPasswordValid || !isNameValid || !isEmailValid || !isPhoneValid) {
        return {
            isValid: false,
            message: '입력 정보를 확인해주세요.'
        };
    }
    
    return { isValid: true };
}

async function deleteUser(userId) {
    if (confirm('정말로 이 회원을 탈퇴 처리하시겠습니까?')) {
        try {
            const response = await fetch(`/admin/user/${userId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.status === 'success') {
                showAlert(result.message, 'success');
                loadUsers();
            } else {
                showAlert(result.message || '회원 삭제에 실패했습니다.', 'danger');
            }
        } catch (error) {
            console.error('회원 삭제 실패:', error);
            showAlert('회원 삭제에 실패했습니다.', 'danger');
        }
    }
}

// 탈퇴 승인
async function approveWithdrawal(userId) {
    if (confirm('정말로 이 회원의 탈퇴를 승인하시겠습니까?')) {
        try {
            const response = await fetch(`/admin/user/${userId}/approve-withdrawal`, {
                method: 'POST'
            });
            
            const result = await response.json();
            
            if (response.ok && result.status === 'success') {
                showAlert(result.message, 'success');
                loadUsers();
            } else {
                showAlert(result.message || '탈퇴 승인에 실패했습니다.', 'danger');
            }
        } catch (error) {
            console.error('탈퇴 승인 실패:', error);
            showAlert('탈퇴 승인에 실패했습니다.', 'danger');
        }
    }
}

// 병원 생성
async function createHospital() {
    try {
        const form = document.getElementById('addHospitalForm');
        const formData = new FormData(form);
        const hospitalData = Object.fromEntries(formData.entries());
        
        // 숫자 필드 변환
        if (hospitalData.lat) hospitalData.lat = parseFloat(hospitalData.lat);
        if (hospitalData.lng) hospitalData.lng = parseFloat(hospitalData.lng);
        
        const response = await fetch('/admin/hospital', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(hospitalData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeModal();
            loadHospitals();
            loadHospitalFilters(); // 필터 목록도 업데이트
        } else {
            showAlert(result.message || '병원 등록에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('병원 생성 실패:', error);
        showAlert('병원 등록에 실패했습니다.', 'danger');
    }
}

async function editHospital(hospitalId) {
    try {
        // 병원 정보 조회
        const response = await fetch(`/admin/hospital/${hospitalId}`);
        const hospital = await response.json();
        
        if (!response.ok) {
            throw new Error('병원 정보를 불러오는데 실패했습니다.');
        }
        
        // 모달 폼에 데이터 채우기
        document.getElementById('editHospitalId').value = hospital.hospitalId;
        document.getElementById('editHospitalName').value = hospital.hospitalName;
        document.getElementById('editHospitalAddress').value = hospital.address;
        document.getElementById('editHospitalPhone').value = hospital.phone || '';
        document.getElementById('editHospitalLat').value = hospital.lat || '';
        document.getElementById('editHospitalLng').value = hospital.lng || '';
        
        // 수정 모달 표시
        showEditHospitalModal();
        
    } catch (error) {
        console.error('병원 정보 로드 실패:', error);
        showAlert('병원 정보를 불러오는데 실패했습니다.', 'danger');
    }
}

// 병원 수정 모달 표시
function showEditHospitalModal() {
    const modal = document.getElementById('editHospitalModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 병원 수정 모달 닫기
function closeEditHospitalModal() {
    const modal = document.getElementById('editHospitalModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 병원 정보 업데이트
async function updateHospital() {
    try {
        const form = document.getElementById('editHospitalForm');
        const formData = new FormData(form);
        const hospitalData = Object.fromEntries(formData.entries());
        
        // 숫자 필드 변환
        if (hospitalData.lat) hospitalData.lat = parseFloat(hospitalData.lat);
        if (hospitalData.lng) hospitalData.lng = parseFloat(hospitalData.lng);
        
        const hospitalId = hospitalData.hospitalId;
        delete hospitalData.hospitalId; // ID는 URL에 포함되므로 제거
        
        const response = await fetch(`/admin/hospital/${hospitalId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(hospitalData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeEditHospitalModal();
            loadHospitals();
        } else {
            showAlert(result.message || '병원 정보 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('병원 업데이트 실패:', error);
        showAlert('병원 정보 업데이트에 실패했습니다.', 'danger');
    }
}

async function deleteHospital(hospitalId) {
    if (confirm('정말로 이 병원을 삭제하시겠습니까?')) {
        try {
            const response = await fetch(`/admin/hospital/${hospitalId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.status === 'success') {
                showAlert(result.message, 'success');
                loadHospitals();
                loadHospitalFilters(); // 필터 목록도 업데이트
            } else {
                showAlert(result.message || '병원 삭제에 실패했습니다.', 'danger');
            }
        } catch (error) {
            console.error('병원 삭제 실패:', error);
            showAlert('병원 삭제에 실패했습니다.', 'danger');
        }
    }
}

// 진료과 생성
async function createDepartment() {
    try {
        console.log('🏥 진료과 추가 시작');
        
        const form = document.getElementById('addDepartmentForm');
        if (!form) {
            console.error('❌ 진료과 추가 폼을 찾을 수 없습니다.');
            showAlert('❌ 진료과 추가 폼을 찾을 수 없습니다.', 'danger');
            return;
        }
        
        const formData = new FormData(form);
        const deptName = formData.get('deptName');
        
        console.log('🏥 진료과명:', deptName);
        
        if (!deptName || deptName.trim() === '') {
            showAlert('❌ 진료과명을 입력해주세요.', 'danger');
            return;
        }
        
        // 현재 선택된 병원 ID 찾기
        const selectedHospitalName = document.getElementById('selected-hospital-name').textContent;
        console.log('🏥 선택된 병원명:', selectedHospitalName);
        
        const hospitalRows = document.querySelectorAll('#hospitals-table-body tr');
        console.log('🏥 병원 행 개수:', hospitalRows.length);
        
        let hospitalId = null;
        
        for (let row of hospitalRows) {
            const hospitalNameCell = row.cells[1]; // 병원명이 두 번째 셀
            if (hospitalNameCell && hospitalNameCell.textContent === selectedHospitalName) {
                hospitalId = row.cells[0].textContent; // 병원 ID가 첫 번째 셀
                console.log('🏥 찾은 병원 ID:', hospitalId);
                break;
            }
        }
        
        if (!hospitalId) {
            console.error('❌ 병원 ID를 찾을 수 없습니다.');
            showAlert('❌ 병원 정보를 찾을 수 없습니다. 먼저 병원을 선택해주세요.', 'danger');
            return;
        }
        
        const requestData = {
            hospitalId: parseInt(hospitalId),
            deptName: deptName.trim()
        };
        
        console.log('🏥 요청 데이터:', requestData);
        
        const response = await fetch('/admin/department', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });
        
        console.log('🏥 응답 상태:', response.status);
        
        const result = await response.json();
        console.log('🏥 응답 데이터:', result);
        
        if (response.ok && result.status === 'success') {
            showAlert('✅ ' + result.message, 'success');
            closeModal();
            // 선택된 병원의 진료과 목록 다시 로드
            selectHospitalForDepartment(hospitalId, selectedHospitalName);
        } else {
            showAlert('❌ ' + (result.message || '진료과 등록에 실패했습니다.'), 'danger');
        }
    } catch (error) {
        console.error('❌ 진료과 생성 실패:', error);
        showAlert('❌ 진료과 등록 중 오류가 발생했습니다: ' + error.message, 'danger');
    }
}

async function editDepartment(deptId) {
    try {
        // 진료과 정보 조회
        const response = await fetch(`/admin/department/${deptId}`);
        const department = await response.json();
        
        if (!response.ok) {
            throw new Error('진료과 정보를 불러오는데 실패했습니다.');
        }
        
        // 모달 폼에 데이터 채우기
        document.getElementById('editDepartmentId').value = department.deptId;
        document.getElementById('editDepartmentName').value = department.deptName;
        document.getElementById('editDepartmentHospitalId').value = department.hospital.hospitalId;
        document.getElementById('editDepartmentHospitalName').textContent = department.hospital.hospitalName;
        
        // 수정 모달 표시
        showEditDepartmentModal();
        
    } catch (error) {
        console.error('진료과 정보 로드 실패:', error);
        showAlert('진료과 정보를 불러오는데 실패했습니다.', 'danger');
    }
}

// 진료과 수정 모달 표시
function showEditDepartmentModal() {
    const modal = document.getElementById('editDepartmentModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 진료과 수정 모달 닫기
function closeEditDepartmentModal() {
    const modal = document.getElementById('editDepartmentModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 진료과 정보 업데이트
async function updateDepartment() {
    try {
        const form = document.getElementById('editDepartmentForm');
        const formData = new FormData(form);
        const departmentData = Object.fromEntries(formData.entries());
        
        const deptId = departmentData.deptId;
        delete departmentData.deptId; // ID는 URL에 포함되므로 제거
        
        const response = await fetch(`/admin/department/${deptId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(departmentData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeEditDepartmentModal();
            // 선택된 병원의 진료과 목록 새로고침
            const hospitalId = document.getElementById('editDepartmentHospitalId').value;
            if (hospitalId) {
                selectHospitalForDepartment(hospitalId, document.getElementById('editDepartmentHospitalName').textContent);
            }
        } else {
            showAlert(result.message || '진료과 정보 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('진료과 업데이트 실패:', error);
        showAlert('진료과 정보 업데이트에 실패했습니다.', 'danger');
    }
}

async function deleteDepartment(deptId) {
    if (confirm('정말로 이 진료과를 삭제하시겠습니까?')) {
        try {
            const response = await fetch(`/admin/department/${deptId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.status === 'success') {
                showAlert(result.message, 'success');
                // 현재 선택된 병원의 진료과 목록 다시 로드
                const selectedHospitalName = document.getElementById('selected-hospital-name').textContent;
                const hospitalRows = document.querySelectorAll('#hospitals-table-body tr');
                let hospitalId = null;
                
                for (let row of hospitalRows) {
                    const hospitalNameCell = row.cells[1];
                    if (hospitalNameCell && hospitalNameCell.textContent === selectedHospitalName) {
                        hospitalId = row.cells[0].textContent;
                        break;
                    }
                }
                
                if (hospitalId) {
                    selectHospitalForDepartment(hospitalId, selectedHospitalName);
                }
            } else {
                showAlert(result.message || '진료과 삭제에 실패했습니다.', 'danger');
            }
        } catch (error) {
            console.error('진료과 삭제 실패:', error);
            showAlert('진료과 삭제에 실패했습니다.', 'danger');
        }
    }
}

// 의사 추가용 병원 목록 로드
async function loadHospitalsForDoctor() {
    try {
        const response = await fetch('/admin/hospitals');
        const hospitals = await response.json();
        
        const select = document.getElementById('doctorHospital');
        select.innerHTML = '<option value="">병원을 선택하세요</option>';
        hospitals.forEach(hospital => {
            const option = document.createElement('option');
            option.value = hospital.hospitalId;
            option.textContent = hospital.hospitalName;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('병원 목록 로드 실패:', error);
    }
}

// 의사 추가용 진료과 목록 로드
async function loadDepartmentsForDoctor() {
    const hospitalId = document.getElementById('doctorHospital').value;
    const deptSelect = document.getElementById('doctorDepartment');
    
    if (!hospitalId) {
        deptSelect.innerHTML = '<option value="">먼저 병원을 선택하세요</option>';
        return;
    }
    
    try {
        const response = await fetch(`/admin/departments/hospital/${hospitalId}`);
        const departments = await response.json();
        
        deptSelect.innerHTML = '<option value="">진료과를 선택하세요</option>';
        departments.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.deptId;
            option.textContent = dept.deptName;
            deptSelect.appendChild(option);
        });
    } catch (error) {
        console.error('진료과 목록 로드 실패:', error);
        deptSelect.innerHTML = '<option value="">진료과 로드 실패</option>';
    }
}

// 의사 생성
async function createDoctor() {
    try {
        const form = document.getElementById('addDoctorForm');
        const formData = new FormData(form);
        const doctorData = Object.fromEntries(formData.entries());
        
        const response = await fetch('/admin/doctor', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(doctorData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeModal();
            loadDoctors();
        } else {
            showAlert(result.message || '의사 등록에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('의사 생성 실패:', error);
        showAlert('의사 등록에 실패했습니다.', 'danger');
    }
}

async function editDoctor(doctorId) {
    try {
        // 의사 정보 조회
        const response = await fetch(`/admin/doctor/${doctorId}`);
        const doctor = await response.json();
        
        if (!response.ok) {
            throw new Error('의사 정보를 불러오는데 실패했습니다.');
        }
        
        // 모달 폼에 데이터 채우기
        document.getElementById('editDoctorId').value = doctor.doctorId;
        document.getElementById('editDoctorName').value = doctor.name;
        document.getElementById('editDoctorAvailableTime').value = doctor.availableTime;
        document.getElementById('editDoctorHospitalId').value = doctor.hospital.hospitalId;
        document.getElementById('editDoctorHospitalName').textContent = doctor.hospital.hospitalName;
        document.getElementById('editDoctorDeptId').value = doctor.department.deptId;
        document.getElementById('editDoctorDeptName').textContent = doctor.department.deptName;
        
        // 수정 모달 표시
        showEditDoctorModal();
        
    } catch (error) {
        console.error('의사 정보 로드 실패:', error);
        showAlert('의사 정보를 불러오는데 실패했습니다.', 'danger');
    }
}

// 의사 수정 모달 표시
function showEditDoctorModal() {
    const modal = document.getElementById('editDoctorModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 의사 수정 모달 닫기
function closeEditDoctorModal() {
    const modal = document.getElementById('editDoctorModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// 의사 정보 업데이트
async function updateDoctor() {
    try {
        const form = document.getElementById('editDoctorForm');
        const formData = new FormData(form);
        const doctorData = Object.fromEntries(formData.entries());
        
        const doctorId = doctorData.doctorId;
        delete doctorData.doctorId; // ID는 URL에 포함되므로 제거
        
        const response = await fetch(`/admin/doctor/${doctorId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(doctorData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.status === 'success') {
            showAlert(result.message, 'success');
            closeEditDoctorModal();
            loadDoctors();
        } else {
            showAlert(result.message || '의사 정보 업데이트에 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('의사 업데이트 실패:', error);
        showAlert('의사 정보 업데이트에 실패했습니다.', 'danger');
    }
}

async function deleteDoctor(doctorId) {
    if (confirm('정말로 이 의사를 삭제하시겠습니까?')) {
        try {
            const response = await fetch(`/admin/doctor/${doctorId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.status === 'success') {
                showAlert(result.message, 'success');
                loadDoctors();
            } else {
                showAlert(result.message || '의사 삭제에 실패했습니다.', 'danger');
            }
        } catch (error) {
            console.error('의사 삭제 실패:', error);
            showAlert('의사 삭제에 실패했습니다.', 'danger');
        }
    }
}

// 의사 선택 시 스케줄 관리 표시
function selectDoctorForSchedule(doctor) {
    // 이전 선택된 행의 스타일 제거
    const previousSelected = document.querySelector('.data-table tbody tr.selected');
    if (previousSelected) {
        previousSelected.classList.remove('selected');
    }
    
    // 현재 선택된 행에 스타일 추가
    event.currentTarget.classList.add('selected');
    
    // 선택된 의사 정보 저장
    window.selectedDoctor = doctor;
    
    // 의사 정보 표시
    document.getElementById('selected-doctor-name').textContent = `${doctor.name} 의사`;
    document.getElementById('selected-doctor-details-name').textContent = `${doctor.name} 의사`;
    document.getElementById('selected-doctor-details').innerHTML = `
        <strong>병원:</strong> ${doctor.hospital.hospitalName}<br>
        <strong>진료과:</strong> ${doctor.department.deptName}<br>
        <strong>진료시간:</strong> ${doctor.availableTime}
    `;
    
    // 오늘 날짜로 기본 설정
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('schedule-date-select').value = today;
    
    // 스케줄 관리 영역 표시
    document.getElementById('selected-doctor-schedule').style.display = 'block';
    
    // 스케줄 로드
    loadSelectedDoctorSchedule();
}

// 선택된 의사의 스케줄 로드
async function loadSelectedDoctorSchedule() {
    if (!window.selectedDoctor) {
        showAlert('의사를 먼저 선택해주세요.', 'warning');
        return;
    }
    
    const date = document.getElementById('schedule-date-select').value;
    
    if (!date) {
        showAlert('날짜를 선택해주세요.', 'warning');
        return;
    }
    
    try {
        const response = await fetch(`/admin/schedule/doctor/${window.selectedDoctor.doctorId}?date=${date}`);
        const data = await response.json();
        
        if (!data.success) {
            throw new Error(data.message);
        }
        
        // 스케줄 표시
        displaySchedule(data.doctor, data.reservations, date);
        
    } catch (error) {
        console.error('스케줄 로드 실패:', error);
        showAlert('스케줄을 불러오는데 실패했습니다.', 'danger');
    }
}

function displaySchedule(doctor, reservations, date) {
    const scheduleDisplay = document.getElementById('schedule-display');
    const scheduleGrid = document.getElementById('schedule-grid');
    
    // 진료시간 파싱 (예: "09:00-17:00")
    const availableTime = doctor.availableTime;
    const [startTime, endTime] = availableTime.split('-');
    const [startHour, startMin] = startTime.split(':').map(Number);
    const [endHour, endMin] = endTime.split(':').map(Number);
    
    // 예약된 시간 목록
    const bookedTimes = reservations.map(r => {
        const time = new Date(r.resDate).toLocaleTimeString('ko-KR', { 
            hour: '2-digit', 
            minute: '2-digit',
            hour12: false 
        });
        return { time, reservation: r };
    });
    
    // 시간 슬롯 생성 (30분 단위)
    scheduleGrid.innerHTML = '';
    const timeSlots = [];
    
    for (let hour = startHour; hour < endHour; hour++) {
        for (let min = 0; min < 60; min += 30) {
            if (hour === endHour && min >= endMin) break;
            
            const timeStr = `${hour.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}`;
            const isBooked = bookedTimes.find(bt => bt.time === timeStr);
            
            const slot = document.createElement('div');
            slot.className = `schedule-slot ${isBooked ? 'booked' : 'available'}`;
            slot.innerHTML = `
                <div class="time">${timeStr}</div>
                <div class="status">
                    ${isBooked ? 
                        `<span class="patient">${isBooked.reservation.user.name}</span>
                         <span class="res-status">${isBooked.reservation.status}</span>` : 
                        '<span class="available-text">예약 가능</span>'
                    }
                </div>
                <div class="actions">
                    ${isBooked ? 
                        `<button class="btn btn-sm btn-secondary" onclick="editReservation(${isBooked.reservation.resId})">수정</button>
                         <button class="btn btn-sm btn-danger" onclick="cancelReservation(${isBooked.reservation.resId})">취소</button>` :
                        `<button class="btn btn-sm btn-primary" onclick="createReservation('${doctor.doctorId}', '${date}', '${timeStr}')">예약</button>`
                    }
                </div>
            `;
            
            scheduleGrid.appendChild(slot);
        }
    }
    
}

// 관리자 예약 생성
async function createReservation(doctorId, date, time) {
    // 환자 선택 모달 표시
    showPatientSelectionModal(doctorId, date, time);
}

// 환자 선택 모달 표시
async function showPatientSelectionModal(doctorId, date, time) {
    try {
        // 사용자 목록 로드
        const response = await fetch('/admin/users');
        const users = await response.json();
        
        // 환자만 필터링
        const patients = users.filter(user => user.role === 'PATIENT' && user.withdrawalStatus === 'ACTIVE');
        
        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.style.display = 'block';
        modal.innerHTML = `
            <div class="modal-content" style="max-width: 600px; max-height: 80vh;">
                <div class="modal-header">
                    <h3>환자 선택</h3>
                    <span class="close" onclick="closePatientModal()">&times;</span>
                </div>
                <div class="modal-body">
                    <div class="reservation-info" style="background: #f8f9fa; padding: 1rem; border-radius: 8px; margin-bottom: 1rem;">
                        <h4>예약 정보</h4>
                        <p><strong>날짜:</strong> ${date}</p>
                        <p><strong>시간:</strong> ${time}</p>
                        <p><strong>의사:</strong> ${window.selectedDoctor ? window.selectedDoctor.name : '선택된 의사'}</p>
                    </div>
                    
                    <div class="search-section" style="margin-bottom: 1rem;">
                        <input type="text" id="patient-search" placeholder="환자 이름, 이메일, 전화번호로 검색..." 
                               style="width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px;"
                               onkeyup="filterPatients()">
                    </div>
                    
                    <div class="patient-list" id="patient-list">
                        ${patients.map(patient => `
                            <div class="patient-item" data-name="${patient.name.toLowerCase()}" data-email="${patient.email.toLowerCase()}" data-phone="${patient.phone}" 
                                 onclick="selectPatient(${patient.userId}, '${patient.name}', '${doctorId}', '${date}', '${time}')">
                                <div class="patient-avatar" style="width: 50px; height: 50px; background: #134686; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; margin-right: 1rem;">
                                    ${patient.name.charAt(0)}
                                </div>
                                <div class="patient-details">
                                    <div class="patient-name">${patient.name}</div>
                                    <div class="patient-info">${patient.email}</div>
                                    <div class="patient-info">${patient.phone}</div>
                                </div>
                                <div class="patient-select-btn">
                                    <button class="btn btn-primary btn-sm">선택</button>
                                </div>
                            </div>
                        `).join('')}
                    </div>
                    
                    ${patients.length === 0 ? '<p style="text-align: center; color: #666; padding: 2rem;">등록된 환자가 없습니다.</p>' : ''}
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        // 모달 외부 클릭 시 닫기
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closePatientModal();
            }
        });
        
    } catch (error) {
        console.error('환자 목록 로드 실패:', error);
        showAlert('환자 목록을 불러오는데 실패했습니다.', 'danger');
    }
}

// 환자 선택
async function selectPatient(userId, userName, doctorId, date, time) {
    // 확인 메시지
    if (!confirm(`${userName}님에게 예약을 생성하시겠습니까?\n\n날짜: ${date}\n시간: ${time}`)) {
        return;
    }
    
    try {
        const response = await fetch('/admin/schedule/reservation', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                doctorId: doctorId,
                userId: userId,
                date: date,
                time: time
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showAlert(`${userName}님의 예약이 성공적으로 생성되었습니다.`, 'success');
            closePatientModal();
            loadSelectedDoctorSchedule(); // 스케줄 새로고침
        } else {
            throw new Error(data.message);
        }
        
    } catch (error) {
        console.error('예약 생성 실패:', error);
        showAlert('예약 생성에 실패했습니다: ' + error.message, 'danger');
    }
}

// 환자 검색 필터링
function filterPatients() {
    const searchTerm = document.getElementById('patient-search').value.toLowerCase();
    const patientItems = document.querySelectorAll('.patient-item');
    
    patientItems.forEach(item => {
        const name = item.getAttribute('data-name');
        const email = item.getAttribute('data-email');
        const phone = item.getAttribute('data-phone');
        
        if (name.includes(searchTerm) || email.includes(searchTerm) || phone.includes(searchTerm)) {
            item.style.display = 'flex';
        } else {
            item.style.display = 'none';
        }
    });
}

// 환자 선택 모달 닫기
function closePatientModal() {
    const modal = document.querySelector('.modal');
    if (modal) {
        modal.remove();
    }
}

// 예약 수정
function editReservation(reservationId) {
    // 예약 수정 모달 표시 (구현 예정)
    showAlert('예약 수정 기능은 구현 예정입니다.', 'info');
}

// 예약 취소
async function cancelReservation(reservationId) {
    if (!confirm('정말로 이 예약을 취소하시겠습니까?')) {
        return;
    }
    
    try {
        const response = await fetch(`/admin/reservation/${reservationId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                status: '취소'
            })
        });
        
        const data = await response.json();
        
        if (data.status === 'success') {
            showAlert('예약이 취소되었습니다.', 'success');
            loadSelectedDoctorSchedule(); // 스케줄 새로고침
        } else {
            throw new Error(data.message);
        }
        
    } catch (error) {
        console.error('예약 취소 실패:', error);
        showAlert('예약 취소에 실패했습니다: ' + error.message, 'danger');
    }
}

function loadFailedNotifications() {
    // 실패한 알림만 보기 구현
}

// 병원 필터 로드
async function loadHospitalFilters() {
    try {
        console.log('🏥 병원 필터 로드 시작...');
        const response = await fetch('/admin/hospitals');
        
        console.log('병원 API 응답 상태:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const hospitals = await response.json();
        console.log('로드된 병원 목록:', hospitals);
        console.log('병원 개수:', hospitals.length);
        
        // 예약 관리 필터
        const reservationFilter = document.getElementById('reservation-hospital-filter');
        if (reservationFilter) {
            reservationFilter.innerHTML = '<option value="">전체 병원</option>';
            hospitals.forEach(hospital => {
                const option = document.createElement('option');
                option.value = hospital.hospitalId;
                option.textContent = hospital.hospitalName;
                reservationFilter.appendChild(option);
            });
            console.log('예약 관리 필터 업데이트 완료');
        }
        
        // 결제 관리 필터
        const paymentFilter = document.getElementById('payment-hospital-filter');
        if (paymentFilter) {
            paymentFilter.innerHTML = '<option value="">전체 병원</option>';
            hospitals.forEach(hospital => {
                const option = document.createElement('option');
                option.value = hospital.hospitalId;
                option.textContent = hospital.hospitalName;
                paymentFilter.appendChild(option);
            });
            console.log('결제 관리 필터 업데이트 완료');
        }
        
        // 통계 관리 필터
        const statsFilter = document.getElementById('stats-hospital-filter');
        if (statsFilter) {
            statsFilter.innerHTML = '<option value="">전체 병원</option>';
            hospitals.forEach(hospital => {
                const option = document.createElement('option');
                option.value = hospital.hospitalId;
                option.textContent = hospital.hospitalName;
                statsFilter.appendChild(option);
            });
            console.log('통계 관리 필터 업데이트 완료');
        }
    } catch (error) {
        console.error('병원 필터 로드 실패:', error);
    }
}

// 예약을 병원별로 필터링
async function filterReservationsByHospital(hospitalId) {
    try {
        let url = '/admin/reservations';
        if (hospitalId) {
            url += `?hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const reservations = await response.json();
        
        // reservations가 배열인지 확인
        if (!Array.isArray(reservations)) {
            console.error('예약 데이터가 배열이 아닙니다:', reservations);
            throw new Error('예약 데이터 형식이 올바르지 않습니다.');
        }
        
        const tbody = document.getElementById('reservations-table-body');
        tbody.innerHTML = '';
        
        reservations.forEach(reservation => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${reservation.resId}</td>
                <td>${reservation.user ? reservation.user.name : '-'}</td>
                <td>${reservation.hospital ? reservation.hospital.hospitalName : '-'}</td>
                <td>${reservation.doctor ? reservation.doctor.name : '-'}</td>
                <td>${reservation.department ? reservation.department.deptName : '-'}</td>
                <td>${formatDateTime(reservation.resDate)}</td>
                <td><span class="status-badge ${getStatusClass(reservation.status)}">${reservation.status}</span></td>
                <td>
                    <select class="form-control" onchange="updateReservationStatus(${reservation.resId}, this.value)">
                        <option value="예약중" ${reservation.status === '예약중' ? 'selected' : ''}>예약중</option>
                        <option value="완료" ${reservation.status === '완료' ? 'selected' : ''}>완료</option>
                        <option value="취소" ${reservation.status === '취소' ? 'selected' : ''}>취소</option>
                    </select>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('예약 필터링 실패:', error);
        showAlert('예약 필터링에 실패했습니다.', 'danger');
    }
}

// 결제를 병원별로 필터링
async function filterPaymentsByHospital(hospitalId) {
    try {
        let url = '/admin/payments';
        if (hospitalId) {
            url += `?hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const payments = await response.json();
        
        // payments가 배열인지 확인
        if (!Array.isArray(payments)) {
            console.error('결제 데이터가 배열이 아닙니다:', payments);
            throw new Error('결제 데이터 형식이 올바르지 않습니다.');
        }
        
        const tbody = document.getElementById('payments-table-body');
        tbody.innerHTML = '';
        
        payments.forEach(payment => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${payment.payId}</td>
                <td>${payment.reservation && payment.reservation.user ? payment.reservation.user.name : '-'}</td>
                <td>${payment.reservation && payment.reservation.hospital ? payment.reservation.hospital.hospitalName : '-'}</td>
                <td>${payment.reservation && payment.reservation.doctor ? payment.reservation.doctor.name : '-'}</td>
                <td>${payment.reservation && payment.reservation.department ? payment.reservation.department.deptName : '-'}</td>
                <td>${payment.amount ? payment.amount.toLocaleString() + '원' : '-'}</td>
                <td>${payment.method || '-'}</td>
                <td>${payment.reservation ? formatDateTime(payment.reservation.resDate) : '-'}</td>
                <td>${formatDateTime(payment.createdAt)}</td>
                <td><span class="status-badge ${getPaymentStatusClass(payment.status)}">${payment.status}</span></td>
                <td>
                    <select class="form-control" onchange="updatePaymentStatus(${payment.payId}, this.value)">
                        <option value="대기" ${payment.status === '대기' ? 'selected' : ''}>대기</option>
                        <option value="완료" ${payment.status === '완료' ? 'selected' : ''}>완료</option>
                        <option value="취소" ${payment.status === '취소' ? 'selected' : ''}>취소</option>
                    </select>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('결제 필터링 실패:', error);
        showAlert('결제 필터링에 실패했습니다.', 'danger');
    }
}

// 통계를 병원별로 필터링
async function filterStatsByHospital(hospitalId) {
    const activeStatsBtn = document.querySelector('.stats-btn.active');
    if (activeStatsBtn) {
        const statsType = activeStatsBtn.getAttribute('data-stats');
        await loadStatsChart(statsType, hospitalId);
    }
}

// 통계 데이터 로드
async function loadStatistics(hospitalId = null) {
    try {
        console.log('📊 통계 데이터 로드 시작', hospitalId ? `(병원 ID: ${hospitalId})` : '(전체)');
        
        // 병렬로 모든 통계 로드
        await Promise.all([
            loadMonthlyReservations(hospitalId),
            loadDailyReservations('2025', '01', hospitalId),
            loadDepartmentReservations(hospitalId),
            loadPaymentStatistics(hospitalId)
        ]);
        
        console.log('✅ 모든 통계 데이터 로드 완료');
    } catch (error) {
        console.error('통계 데이터 로드 실패:', error);
        showAlert('통계 데이터를 불러오는데 실패했습니다.', 'danger');
    }
}

// 월별 예약 현황 로드
async function loadMonthlyReservations(hospitalId = null) {
    try {
        const currentYear = new Date().getFullYear();
        let url = `/admin/statistics/monthly?year=${currentYear}`;
        if (hospitalId) {
            url += `&hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            displayMonthlyChart(result.data);
        } else {
            console.error('월별 예약 통계 로드 실패:', result.message);
            showAlert('월별 예약 통계를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('월별 예약 통계 로드 실패:', error);
        showAlert('월별 예약 통계를 불러오는데 실패했습니다.', 'danger');
    }
}

// 일별 예약 현황 로드
async function loadDailyReservations(year, month, hospitalId = null) {
    try {
        let url = `/admin/statistics/daily?year=${year}&month=${month}`;
        if (hospitalId) {
            url += `&hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            displayDailyChart(result.data);
        } else {
            console.error('일별 예약 통계 로드 실패:', result.message);
            showAlert('일별 예약 통계를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('일별 예약 통계 로드 실패:', error);
        showAlert('일별 예약 통계를 불러오는데 실패했습니다.', 'danger');
    }
}

// 진료과별 예약 건수 로드
async function loadDepartmentReservations(hospitalId = null) {
    try {
        let url = '/admin/statistics/department';
        if (hospitalId) {
            url += `?hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            displayDepartmentChart(result.data);
        } else {
            console.error('진료과별 예약 통계 로드 실패:', result.message);
            showAlert('진료과별 예약 통계를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('진료과별 예약 통계 로드 실패:', error);
        showAlert('진료과별 예약 통계를 불러오는데 실패했습니다.', 'danger');
    }
}

// 결제 통계 로드
async function loadPaymentStatistics(hospitalId = null) {
    try {
        let url = '/admin/statistics/payment';
        if (hospitalId) {
            url += `?hospitalId=${hospitalId}`;
        }
        
        const response = await fetch(url);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            displayPaymentStatistics(result.data);
        } else {
            console.error('결제 통계 로드 실패:', result.message);
            showAlert('결제 통계를 불러오는데 실패했습니다.', 'danger');
        }
    } catch (error) {
        console.error('결제 통계 로드 실패:', error);
        showAlert('결제 통계를 불러오는데 실패했습니다.', 'danger');
    }
}

// 월별 차트 표시
function displayMonthlyChart(data) {
    const container = document.getElementById('monthly-chart-container');
    if (!container) return;
    
    const months = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
    const counts = new Array(12).fill(0);
    
    data.forEach(item => {
        counts[item.month - 1] = item.count;
    });
    
    container.innerHTML = `
        <div class="chart-container">
            <h4>월별 예약 현황</h4>
            <div class="chart-bars">
                ${months.map((month, index) => `
                    <div class="chart-bar">
                        <div class="bar" style="height: ${Math.max(counts[index] * 10, 20)}px;" data-count="${counts[index]}"></div>
                        <span class="bar-label">${month}</span>
                        <span class="bar-value">${counts[index]}건</span>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 일별 차트 표시
function displayDailyChart(data) {
    const container = document.getElementById('daily-chart-container');
    if (!container) return;
    
    const days = [];
    const counts = [];
    
    data.forEach(item => {
        days.push(`${item.day}일`);
        counts.push(item.count);
    });
    
    container.innerHTML = `
        <div class="chart-container">
            <h4>일별 예약 현황</h4>
            <div class="chart-bars">
                ${days.map((day, index) => `
                    <div class="chart-bar">
                        <div class="bar" style="height: ${Math.max(counts[index] * 10, 20)}px;" data-count="${counts[index]}"></div>
                        <span class="bar-label">${day}</span>
                        <span class="bar-value">${counts[index]}건</span>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 진료과별 차트 표시
function displayDepartmentChart(data) {
    const container = document.getElementById('department-chart-container');
    if (!container) return;
    
    container.innerHTML = `
        <div class="chart-container">
            <h4>진료과별 예약 건수</h4>
            <div class="department-list">
                ${data.map(item => `
                    <div class="department-item">
                        <span class="department-name">${item.departmentName}</span>
                        <span class="department-count">${item.count}건</span>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

// 결제 통계 표시
function displayPaymentStatistics(data) {
    const container = document.getElementById('payment-stats-container');
    if (!container) return;
    
    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('ko-KR').format(amount) + '원';
    };
    
    container.innerHTML = `
        <div class="payment-stats">
            <h4>결제 통계</h4>
            <div class="stats-grid">
                <div class="stat-card">
                    <h5>총 결제 금액</h5>
                    <p class="stat-value">${formatCurrency(data.totalAmount)}</p>
                    <p class="stat-count">완료된 결제만</p>
                </div>
                <div class="stat-card">
                    <h5>완료된 결제</h5>
                    <p class="stat-value">${formatCurrency(data.completedAmount)}</p>
                    <p class="stat-count">${data.completedCount}건</p>
                </div>
                <div class="stat-card">
                    <h5>대기 중인 결제</h5>
                    <p class="stat-value">${formatCurrency(data.pendingAmount)}</p>
                    <p class="stat-count">${data.pendingCount}건</p>
                </div>
                <div class="stat-card">
                    <h5>취소된 결제</h5>
                    <p class="stat-value">${formatCurrency(data.cancelledAmount)}</p>
                    <p class="stat-count">${data.cancelledCount}건</p>
                </div>
            </div>
        </div>
    `;
}

// 통계용 병원 필터 로드
async function loadHospitalFilterForStatistics() {
    try {
        const response = await fetch('/admin/hospitals');
        if (response.ok) {
            const hospitals = await response.json();
            const filter = document.getElementById('statistics-hospital-filter');
            
            // 기존 옵션 제거 (전체 병원 제외)
            while (filter.children.length > 1) {
                filter.removeChild(filter.lastChild);
            }
            
            // 병원 옵션 추가
            hospitals.forEach(hospital => {
                const option = document.createElement('option');
                option.value = hospital.hospitalId;
                option.textContent = hospital.hospitalName;
                filter.appendChild(option);
            });
        }
    } catch (error) {
        console.error('통계용 병원 필터 로드 실패:', error);
    }
}

// 통계 병원별 필터링
function filterStatisticsByHospital(hospitalId) {
    const hospitalIdValue = hospitalId ? parseInt(hospitalId) : null;
    loadStatistics(hospitalIdValue);
}
