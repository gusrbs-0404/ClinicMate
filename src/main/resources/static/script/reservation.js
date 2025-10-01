class ReservationPageHandler {
    constructor() {
        this.hospitalId = null;
        this.selectedHospital = null;
        this.selectedDepartment = null;
        this.selectedDoctor = null;
        this.selectedDate = null;
        this.selectedTime = null;

        this.init();
    }

    async init() {
        console.log('🚀 ReservationPageHandler 초기화 시작');
        
        // 로그인 체크
        if (!this.checkLoginStatus()) {
            return;
        }
        
        this.parseUrlParams();
        this.loadUserInfo(); // 사용자 정보 로드
        await this.loadInitialData();
        
        // 사용자 정보가 없으면 1초 후 재시도
        setTimeout(() => {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) {
                console.log('사용자 정보 재로드 시도');
                this.loadUserInfo();
            }
        }, 1000);
        this.initFlatpickr();
        this.bindEvents();
        this.updateSummary();
        console.log('✅ ReservationPageHandler 초기화 완료');
    }
    
    // 로그인 상태 체크
    checkLoginStatus() {
        if (!Utils.isLoggedIn()) {
            alert('예약을 위해서는 로그인이 필요합니다.');
            window.location.href = '/users/signin';
            return false;
        }
        return true;
    }
    
    // 사용자 정보 로드 및 표시
    loadUserInfo() {
        const currentUser = Utils.getCurrentUser();
        console.log('현재 사용자 정보:', currentUser);
        
        if (currentUser) {
            const summaryUserNameEl = document.getElementById('summaryUserName');
            const summaryUserPhoneEl = document.getElementById('summaryUserPhone');
            const summaryUserEmailEl = document.getElementById('summaryUserEmail');
            
            if (summaryUserNameEl) summaryUserNameEl.textContent = currentUser.name || '정보 없음';
            if (summaryUserPhoneEl) summaryUserPhoneEl.textContent = currentUser.phone || '정보 없음';
            if (summaryUserEmailEl) summaryUserEmailEl.textContent = currentUser.email || '정보 없음';
            
            console.log('사용자 정보 표시 완료:', {
                name: currentUser.name,
                phone: currentUser.phone,
                email: currentUser.email
            });
        } else {
            console.log('사용자 정보를 찾을 수 없습니다.');
            // 사용자 정보가 없으면 기본값 표시
            const summaryUserNameEl = document.getElementById('summaryUserName');
            const summaryUserPhoneEl = document.getElementById('summaryUserPhone');
            const summaryUserEmailEl = document.getElementById('summaryUserEmail');
            
            if (summaryUserNameEl) summaryUserNameEl.textContent = '정보 없음';
            if (summaryUserPhoneEl) summaryUserPhoneEl.textContent = '정보 없음';
            if (summaryUserEmailEl) summaryUserEmailEl.textContent = '정보 없음';
        }
    }

    parseUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        this.hospitalId = urlParams.get('hospitalId');
        const deptId = urlParams.get('deptId');
        const doctorId = urlParams.get('doctorId');

        if (deptId) this.selectedDepartment = { deptId: deptId };
        if (doctorId) this.selectedDoctor = { doctorId: doctorId };

        console.log('URL Params:', { hospitalId: this.hospitalId, deptId, doctorId });
    }

    async loadInitialData() {
        if (this.hospitalId) {
            try {
                // 병원 정보 로드
                const hospitalResponse = await fetch(`/api/hospitals/${this.hospitalId}`);
                if (!hospitalResponse.ok) throw new Error(`HTTP error! status: ${hospitalResponse.status}`);
                this.selectedHospital = await hospitalResponse.json();
                console.log('🏥 로드된 병원:', this.selectedHospital);
                this.updateHospitalInfo();

                // 진료과 목록 로드
                await this.loadDepartments();

                // 선택된 진료과가 있다면 의사 목록 로드
                if (this.selectedDepartment && this.selectedDepartment.deptId) {
                    await this.loadDoctors(this.selectedDepartment.deptId);
                }
            } catch (error) {
                console.error('❌ 초기 데이터 로드 실패:', error);
                alert('병원 정보를 불러오는 데 실패했습니다.');
            }
        } else {
            alert('병원 정보가 없습니다. 메인 페이지로 돌아갑니다.');
            window.location.href = '/main';
        }
    }

    updateHospitalInfo() {
        if (this.selectedHospital) {
            const hospitalNameEl = document.getElementById('hospitalName');
            const hospitalAddressEl = document.getElementById('hospitalAddress');
            const hospitalPhoneEl = document.getElementById('hospitalPhone');
            
            if (hospitalNameEl) hospitalNameEl.textContent = this.selectedHospital.hospitalName;
            if (hospitalAddressEl) hospitalAddressEl.textContent = this.selectedHospital.address;
            if (hospitalPhoneEl) hospitalPhoneEl.textContent = this.selectedHospital.phone || '전화번호 없음';
        }
    }

    async loadDepartments() {
        const departmentListDiv = document.getElementById('departmentList');
        departmentListDiv.innerHTML = '<p>진료과를 불러오는 중...</p>';
        try {
            const response = await fetch(`/api/hospitals/${this.hospitalId}/departments`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const departments = await response.json();
            console.log('🏥 로드된 진료과:', departments);

            if (departments && departments.length > 0) {
                departmentListDiv.innerHTML = departments.map(dept => `
                    <div class="selection-item ${this.selectedDepartment?.deptId == dept.deptId ? 'selected' : ''}" 
                         data-dept-id="${dept.deptId}" data-dept-name="${dept.deptName}">
                        <span class="item-name">${dept.deptName}</span>
                    </div>
                `).join('');

                // 이벤트 리스너 추가
                departmentListDiv.querySelectorAll('.selection-item').forEach(item => {
                    item.addEventListener('click', (e) => this.handleDepartmentSelection(e.currentTarget));
                });

                // URL 파라미터로 선택된 진료과가 있다면 해당 진료과 정보 업데이트
                if (this.selectedDepartment && this.selectedDepartment.deptId) {
                    const preselectedDept = departments.find(d => d.deptId == this.selectedDepartment.deptId);
                    if (preselectedDept) {
                        this.selectedDepartment.deptName = preselectedDept.deptName;
                        this.updateSummary();
                    }
                }
            } else {
                departmentListDiv.innerHTML = '<p>등록된 진료과가 없습니다.</p>';
            }
        } catch (error) {
            console.error('❌ 진료과 로드 실패:', error);
            departmentListDiv.innerHTML = '<p>진료과를 불러오는데 실패했습니다.</p>';
        }
    }

    async handleDepartmentSelection(element) {
        // 기존 선택 해제
        document.querySelectorAll('#departmentList .selection-item').forEach(item => item.classList.remove('selected'));
        // 새 선택 적용
        element.classList.add('selected');

        this.selectedDepartment = {
            deptId: element.dataset.deptId,
            deptName: element.dataset.deptName
        };
        this.selectedDoctor = null; // 진료과 변경 시 의사 초기화
        this.selectedDate = null; // 진료과 변경 시 날짜 초기화
        this.selectedTime = null; // 진료과 변경 시 시간 초기화

        this.updateSummary();
        await this.loadDoctors(this.selectedDepartment.deptId);
        this.renderTimeSlots(); // 의사 초기화 후 시간 슬롯도 초기화
    }

    async loadDoctors(deptId) {
        const doctorListDiv = document.getElementById('doctorList');
        doctorListDiv.innerHTML = '<p>의사 정보를 불러오는 중...</p>';
        try {
            const response = await fetch(`/api/hospitals/${this.hospitalId}/departments/${deptId}/doctors`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const doctors = await response.json();
            console.log('👨‍⚕️ 로드된 의사:', doctors);

            if (doctors && doctors.length > 0) {
                doctorListDiv.innerHTML = doctors.map(doctor => `
                    <div class="selection-item ${this.selectedDoctor?.doctorId == doctor.doctorId ? 'selected' : ''}" 
                         data-doctor-id="${doctor.doctorId}" data-doctor-name="${doctor.name}" 
                         data-available-time="${doctor.availableTime}">
                        <span class="item-name">${doctor.name} 의사</span>
                        <span class="item-detail">진료시간: ${doctor.availableTime}</span>
                    </div>
                `).join('');

                // 이벤트 리스너 추가
                doctorListDiv.querySelectorAll('.selection-item').forEach(item => {
                    item.addEventListener('click', (e) => this.handleDoctorSelection(e.currentTarget));
                });

                // URL 파라미터로 선택된 의사가 있다면 해당 의사 정보 업데이트
                if (this.selectedDoctor && this.selectedDoctor.doctorId) {
                    const preselectedDoctor = doctors.find(d => d.doctorId == this.selectedDoctor.doctorId);
                    if (preselectedDoctor) {
                        this.selectedDoctor.name = preselectedDoctor.name;
                        this.selectedDoctor.availableTime = preselectedDoctor.availableTime;
                        this.updateSummary();
                    }
                }
            } else {
                doctorListDiv.innerHTML = '<p>등록된 의사가 없습니다.</p>';
            }
        } catch (error) {
            console.error('❌ 의사 정보 로드 실패:', error);
            doctorListDiv.innerHTML = '<p>의사 정보를 불러오는데 실패했습니다.</p>';
        }
    }

    handleDoctorSelection(element) {
        // 기존 선택 해제
        document.querySelectorAll('#doctorList .selection-item').forEach(item => item.classList.remove('selected'));
        // 새 선택 적용
        element.classList.add('selected');

        this.selectedDoctor = {
            doctorId: element.dataset.doctorId,
            name: element.dataset.doctorName,
            availableTime: element.dataset.availableTime
        };
        this.selectedDate = null; // 의사 변경 시 날짜 초기화
        this.selectedTime = null; // 의사 변경 시 시간 초기화

        this.updateSummary();
        this.renderTimeSlots(); // 의사 선택 시 시간 슬롯 다시 렌더링
    }

    initFlatpickr() {
        flatpickr("#datePicker", {
            inline: true, // 달력을 항상 보이게
            minDate: "today",
            dateFormat: "Y-m-d",
            locale: "ko", // 한국어 로케일 (필요시)
            onChange: (selectedDates, dateStr, instance) => {
                this.selectedDate = dateStr;
                this.selectedTime = null; // 날짜 변경 시 시간 초기화
                this.updateSummary();
                this.renderTimeSlots(); // 날짜 선택 시 시간 슬롯 렌더링
            }
        });
    }

    async renderTimeSlots() {
        const timeSlotsDiv = document.getElementById('timeSlots');
        timeSlotsDiv.innerHTML = '<p>시간 정보를 불러오는 중...</p>';

        if (!this.selectedDoctor || !this.selectedDate) {
            timeSlotsDiv.innerHTML = '<p>날짜와 의사를 선택하면 예약 가능한 시간이 표시됩니다.</p>';
            this.updateConfirmButton();
            return;
        }

        try {
            // 예약된 시간 조회
            const response = await fetch(`/reservation/api/booked-times?doctorId=${this.selectedDoctor.doctorId}&date=${this.selectedDate}`);
            const result = await response.json();
            
            let bookedTimes = [];
            if (result.success) {
                bookedTimes = result.bookedTimes || [];
            }

            // 예약 가능한 시간 생성
            const availableTimes = this.generateTimeSlots(this.selectedDoctor.availableTime);
            
            // 현재 시간 확인
            const now = new Date();
            const today = now.toISOString().split('T')[0];
            const currentTime = now.toTimeString().substr(0, 5);

            if (availableTimes.length > 0) {
                timeSlotsDiv.innerHTML = availableTimes.map(time => {
                    const isBooked = bookedTimes.includes(time);
                    const isPastTime = this.selectedDate === today && time <= currentTime;
                    const isDisabled = isBooked || isPastTime;
                    
                    let statusText = '';
                    if (isBooked) statusText = ' (예약됨)';
                    else if (isPastTime) statusText = ' (지난 시간)';
                    
                    return `
                        <div class="time-slot ${this.selectedTime === time ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}" 
                             data-time="${time}" ${isDisabled ? 'data-disabled="true"' : ''}>
                            ${time}${statusText}
                        </div>
                    `;
                }).join('');

                timeSlotsDiv.querySelectorAll('.time-slot').forEach(slot => {
                    const isDisabled = slot.dataset.disabled === 'true';
                    if (!isDisabled) {
                        slot.addEventListener('click', (e) => this.handleTimeSelection(e.currentTarget));
                    }
                });
            } else {
                timeSlotsDiv.innerHTML = '<p>예약 가능한 시간이 없습니다.</p>';
            }
        } catch (error) {
            console.error('❌ 시간 슬롯 로드 실패:', error);
            timeSlotsDiv.innerHTML = '<p>시간 정보를 불러오는데 실패했습니다.</p>';
        }
        
        this.updateConfirmButton();
    }

    generateTimeSlots(availableTimeRange) {
        // 예: "09:00-18:00"
        const [startTimeStr, endTimeStr] = availableTimeRange.split('-');
        const [startHour, startMinute] = startTimeStr.split(':').map(Number);
        const [endHour, endMinute] = endTimeStr.split(':').map(Number);

        const timeSlots = [];
        let currentTime = new Date();
        currentTime.setHours(startHour, startMinute, 0, 0);

        let endTime = new Date();
        endTime.setHours(endHour, endMinute, 0, 0);

        while (currentTime <= endTime) {
            timeSlots.push(currentTime.toTimeString().substring(0, 5));
            currentTime.setMinutes(currentTime.getMinutes() + 30);
        }
        return timeSlots;
    }

    handleTimeSelection(element) {
        // 기존 선택 해제
        document.querySelectorAll('#timeSlots .time-slot').forEach(item => item.classList.remove('selected'));
        // 새 선택 적용
        element.classList.add('selected');

        this.selectedTime = element.dataset.time;
        this.updateSummary();
        this.updateConfirmButton();
    }

    updateSummary() {
        const summaryHospitalNameEl = document.getElementById('summaryHospitalName');
        const summaryDepartmentNameEl = document.getElementById('summaryDepartmentName');
        const summaryDoctorNameEl = document.getElementById('summaryDoctorName');
        const summaryDateEl = document.getElementById('summaryDate');
        const summaryTimeEl = document.getElementById('summaryTime');
        
        if (summaryHospitalNameEl) summaryHospitalNameEl.textContent = this.selectedHospital?.hospitalName || '선택 안됨';
        if (summaryDepartmentNameEl) summaryDepartmentNameEl.textContent = this.selectedDepartment?.deptName || '선택 안됨';
        if (summaryDoctorNameEl) summaryDoctorNameEl.textContent = this.selectedDoctor?.name || '선택 안됨';
        if (summaryDateEl) summaryDateEl.textContent = this.selectedDate || '선택 안됨';
        if (summaryTimeEl) summaryTimeEl.textContent = this.selectedTime || '선택 안됨';

        this.updateConfirmButton();
    }

    updateConfirmButton() {
        const confirmBtn = document.getElementById('confirmReservationBtn');
        if (confirmBtn) {
            if (this.selectedHospital && this.selectedDepartment && this.selectedDoctor && this.selectedDate && this.selectedTime) {
                confirmBtn.disabled = false;
            } else {
                confirmBtn.disabled = true;
            }
        }
    }

    bindEvents() {
        const confirmBtn = document.getElementById('confirmReservationBtn');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', () => this.confirmReservation());
        }
    }

    async confirmReservation() {
        if (this.selectedHospital && this.selectedDepartment && this.selectedDoctor && this.selectedDate && this.selectedTime) {
            const currentUser = Utils.getCurrentUser();
            const userInfo = currentUser ? 
                `예약자: ${currentUser.name}\n전화번호: ${currentUser.phone}\n이메일: ${currentUser.email}\n\n` : 
                '예약자 정보를 불러올 수 없습니다.\n\n';
            
            const reservationInfo = `예약 확정!\n\n${userInfo}병원: ${this.selectedHospital.hospitalName}\n진료과: ${this.selectedDepartment.deptName}\n의사: ${this.selectedDoctor.name}\n날짜: ${this.selectedDate}\n시간: ${this.selectedTime}`;
            
            if (confirm(reservationInfo + '\n\n예약을 확정하시겠습니까?')) {
                try {
                    // 예약 API 호출
                    const response = await fetch('/api/reservations', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({
                            hospitalId: this.selectedHospital.hospitalId,
                            doctorId: this.selectedDoctor.doctorId,
                            deptId: this.selectedDepartment.deptId,
                            resDate: this.selectedDate,
                            resTime: this.selectedTime
                        })
                    });
                    
                    const result = await response.json();
                    
                    if (result.success) {
                        alert('예약이 성공적으로 완료되었습니다!');
                        // 마이페이지로 이동
                        window.location.href = '/users/me';
                    } else {
                        alert('예약 실패: ' + result.message);
                    }
                } catch (error) {
                    console.error('예약 API 호출 실패:', error);
                    alert('예약 처리 중 오류가 발생했습니다.');
                }
            }
        } else {
            alert('모든 예약 정보를 선택해주세요.');
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.reservationPageHandler = new ReservationPageHandler();
});