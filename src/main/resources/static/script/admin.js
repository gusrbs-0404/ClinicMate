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
    setupTabNavigation();
    setupSubTabNavigation();
    setupStatsNavigation();
    loadInitialData();
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
    loadUsers();
    loadHospitals();
    loadDepartments();
    loadDoctors();
    loadReservations();
    loadPayments();
    loadNotifications();
    loadStatsChart('monthly');
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
            loadStatsChart('monthly');
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
        case 'schedules':
            loadDoctorSchedules();
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
        
        const tbody = document.getElementById('users-table-body');
        tbody.innerHTML = '';
        
        if (users && users.length > 0) {
            users.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.userId}</td>
                    <td>${user.username}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>${user.phone || '-'}</td>
                    <td><span class="status-badge ${user.role === 'ADMIN' ? 'status-info' : 'status-success'}">${user.role}</span></td>
                    <td>${formatDate(user.createdAt)}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="viewUser(${user.userId})">상세보기</button>
                        <button class="btn btn-sm btn-secondary" onclick="editUser(${user.userId})">수정</button>
                        <button class="btn btn-sm btn-warning" onclick="updateUserRole(${user.userId}, '${user.role === 'ADMIN' ? 'PATIENT' : 'ADMIN'}')">권한</button>
                        <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.userId})">탈퇴승인</button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center">등록된 회원이 없습니다.</td></tr>';
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
            row.innerHTML = `
                <td>${doctor.doctorId}</td>
                <td>${doctor.hospital ? doctor.hospital.hospitalName : '-'}</td>
                <td>${doctor.department ? doctor.department.deptName : '-'}</td>
                <td>${doctor.name}</td>
                <td>${doctor.availableTime || '-'}</td>
                <td>
                    <button class="btn btn-sm btn-secondary" onclick="editDoctor(${doctor.doctorId})">수정</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteDoctor(${doctor.doctorId})">삭제</button>
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
async function loadStatsChart(type) {
    try {
        const response = await fetch(`/admin/stats/${type}`);
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

// 모달 관련 함수들
function showModal(title, content) {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML = content;
    document.getElementById('modal-overlay').style.display = 'block';
}

function closeModal() {
    document.getElementById('modal-overlay').style.display = 'none';
}

// 모달 외부 클릭 시 닫기
document.getElementById('modal-overlay').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

// 모달 관련 함수들 (추후 구현)
function showAddUserModal() {
    // 회원 추가 모달 구현
}

function showAddHospitalModal() {
    // 병원 추가 모달 구현
}

function showAddDepartmentModal() {
    // 진료과 추가 모달 구현
}

function showAddDoctorModal() {
    // 의사 추가 모달 구현
}

function viewUser(userId) {
    // 회원 상세보기 구현
}

function editUser(userId) {
    // 회원 수정 구현
}

function deleteUser(userId) {
    if (confirm('정말로 이 회원을 탈퇴 처리하시겠습니까?')) {
        // 회원 삭제 구현
    }
}

function editHospital(hospitalId) {
    // 병원 수정 구현
}

function deleteHospital(hospitalId) {
    if (confirm('정말로 이 병원을 삭제하시겠습니까?')) {
        // 병원 삭제 구현
    }
}

function editDepartment(deptId) {
    // 진료과 수정 구현
}

function deleteDepartment(deptId) {
    if (confirm('정말로 이 진료과를 삭제하시겠습니까?')) {
        // 진료과 삭제 구현
    }
}

function editDoctor(doctorId) {
    // 의사 수정 구현
}

function deleteDoctor(doctorId) {
    if (confirm('정말로 이 의사를 삭제하시겠습니까?')) {
        // 의사 삭제 구현
    }
}

function loadDoctorSchedules() {
    // 의사 스케줄 로드 구현
}

function loadFailedNotifications() {
    // 실패한 알림만 보기 구현
}
