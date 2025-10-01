// 사용자 관련 JavaScript

// 회원가입 관련 함수들
const SignupHandler = {
    // 폼 초기화
    init: function() {
        const form = document.getElementById('signupForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
            this.setupValidation();
        }
    },

    // 실시간 유효성 검사 설정
    setupValidation: function() {
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const nameInput = document.getElementById('name');
        const emailInput = document.getElementById('email');
        const phoneInput = document.getElementById('phone');

        if (usernameInput) {
            usernameInput.addEventListener('blur', () => this.validateUsername());
            usernameInput.addEventListener('input', () => Utils.hideError('usernameError'));
        }

        if (passwordInput) {
            passwordInput.addEventListener('blur', () => this.validatePassword());
            passwordInput.addEventListener('input', () => Utils.hideError('passwordError'));
        }

        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener('blur', () => this.validateConfirmPassword());
            confirmPasswordInput.addEventListener('input', () => Utils.hideError('confirmPasswordError'));
        }

        if (nameInput) {
            nameInput.addEventListener('blur', () => this.validateName());
            nameInput.addEventListener('input', () => Utils.hideError('nameError'));
        }

        if (emailInput) {
            emailInput.addEventListener('blur', () => this.validateEmail());
            emailInput.addEventListener('input', () => Utils.hideError('emailError'));
        }

        if (phoneInput) {
            phoneInput.addEventListener('blur', () => this.validatePhone());
            phoneInput.addEventListener('input', () => Utils.hideError('phoneError'));
        }
    },

    // 아이디 유효성 검사
    validateUsername: function() {
        const username = document.getElementById('username').value.trim();
        
        if (!Utils.validateNotBlank(username)) {
            Utils.showError('usernameError', '아이디를 입력해주세요.');
            return false;
        }

        if (!Utils.validateUsername(username)) {
            Utils.showError('usernameError', '6-20자 영문, 숫자 조합으로 입력해주세요.');
            return false;
        }

        // 중복 검사 (실제 구현에서는 서버 API 호출)
        this.checkUsernameDuplicate(username);
        return true;
    },

    // 아이디 중복 검사
    async checkUsernameDuplicate(username) {
        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall(`/api/users/check-username?username=${username}`);
            // if (response.exists) {
            //     Utils.showError('usernameError', '이미 사용 중인 아이디입니다.');
            //     return false;
            // }
            
            // 임시로 성공 처리
            Utils.hideError('usernameError');
            return true;
        } catch (error) {
            Utils.showError('usernameError', '아이디 중복 검사 중 오류가 발생했습니다.');
            return false;
        }
    },

    // 비밀번호 유효성 검사
    validatePassword: function() {
        const password = document.getElementById('password').value;
        
        if (!Utils.validateNotBlank(password)) {
            Utils.showError('passwordError', '비밀번호를 입력해주세요.');
            return false;
        }

        if (!Utils.validatePassword(password)) {
            Utils.showError('passwordError', '영문, 숫자, 특수문자(!@#$%^&*)를 포함하여 8자 이상 입력해주세요.');
            return false;
        }

        Utils.hideError('passwordError');
        return true;
    },

    // 비밀번호 확인 검사
    validateConfirmPassword: function() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (!Utils.validateNotBlank(confirmPassword)) {
            Utils.showError('confirmPasswordError', '비밀번호 확인을 입력해주세요.');
            return false;
        }

        if (password !== confirmPassword) {
            Utils.showError('confirmPasswordError', '비밀번호가 일치하지 않습니다.');
            return false;
        }

        Utils.hideError('confirmPasswordError');
        return true;
    },

    // 이름 유효성 검사
    validateName: function() {
        const name = document.getElementById('name').value.trim();
        
        if (!Utils.validateNotBlank(name)) {
            Utils.showError('nameError', '이름을 입력해주세요.');
            return false;
        }

        if (!Utils.validateName(name)) {
            Utils.showError('nameError', '한글 2-5자로 입력해주세요.');
            return false;
        }

        Utils.hideError('nameError');
        return true;
    },

    // 이메일 유효성 검사
    validateEmail: function() {
        const email = document.getElementById('email').value.trim();
        
        if (!Utils.validateNotBlank(email)) {
            Utils.showError('emailError', '이메일을 입력해주세요.');
            return false;
        }

        if (!Utils.validateEmail(email)) {
            Utils.showError('emailError', '올바른 이메일 형식을 입력해주세요.');
            return false;
        }

        // 중복 검사 (실제 구현에서는 서버 API 호출)
        this.checkEmailDuplicate(email);
        return true;
    },

    // 이메일 중복 검사
    async checkEmailDuplicate(email) {
        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall(`/api/users/check-email?email=${email}`);
            // if (response.exists) {
            //     Utils.showError('emailError', '이미 사용 중인 이메일입니다.');
            //     return false;
            // }
            
            // 임시로 성공 처리
            Utils.hideError('emailError');
            return true;
        } catch (error) {
            Utils.showError('emailError', '이메일 중복 검사 중 오류가 발생했습니다.');
            return false;
        }
    },

    // 전화번호 유효성 검사
    validatePhone: function() {
        const phone = document.getElementById('phone').value.trim();
        
        if (!Utils.validateNotBlank(phone)) {
            Utils.showError('phoneError', '전화번호를 입력해주세요.');
            return false;
        }

        if (!Utils.validatePhone(phone)) {
            Utils.showError('phoneError', '010-1234-5678 형식으로 입력해주세요.');
            return false;
        }

        // 중복 검사 (실제 구현에서는 서버 API 호출)
        this.checkPhoneDuplicate(phone);
        return true;
    },

    // 전화번호 중복 검사
    async checkPhoneDuplicate(phone) {
        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall(`/api/users/check-phone?phone=${phone}`);
            // if (response.exists) {
            //     Utils.showError('phoneError', '이미 사용 중인 전화번호입니다.');
            //     return false;
            // }
            
            // 임시로 성공 처리
            Utils.hideError('phoneError');
            return true;
        } catch (error) {
            Utils.showError('phoneError', '전화번호 중복 검사 중 오류가 발생했습니다.');
            return false;
        }
    },

    // 전체 폼 유효성 검사
    validateForm: function() {
        const isValid = 
            this.validateUsername() &&
            this.validatePassword() &&
            this.validateConfirmPassword() &&
            this.validateName() &&
            this.validateEmail() &&
            this.validatePhone();

        return isValid;
    },

    // 폼 제출 처리
    async handleSubmit(event) {
        event.preventDefault();

        if (!this.validateForm()) {
            Utils.showErrorMessage('입력 정보를 확인해주세요.');
            return;
        }

        const formData = new FormData(event.target);
        const userData = Object.fromEntries(formData.entries());

        const submitBtn = document.getElementById('signupBtn');
        Utils.showLoading(submitBtn);

        try {
            // 실제 서버 API 호출
            const response = await fetch('/users/action/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(userData)
            });

            const result = await response.json();

            if (result.success) {
                Utils.showSuccess('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
                
                setTimeout(() => {
                    window.location.href = '/users/signin';
                }, 2000);
            } else {
                Utils.showErrorMessage(result.message || '회원가입 중 오류가 발생했습니다.');
            }

        } catch (error) {
            Utils.showErrorMessage('회원가입 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(submitBtn);
        }
    }
};

// 로그인 관련 함수들
const SigninHandler = {
    // 폼 초기화
    init: function() {
        const form = document.getElementById('signinForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
        }

        const sendCodeBtn = document.getElementById('sendCodeBtn');
        if (sendCodeBtn) {
            sendCodeBtn.addEventListener('click', this.sendVerificationCode.bind(this));
        }
    },

    // 인증코드 발송
    async sendVerificationCode() {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        if (!username || !password) {
            Utils.showErrorMessage('아이디와 비밀번호를 먼저 입력해주세요.');
            return;
        }

        const sendCodeBtn = document.getElementById('sendCodeBtn');
        Utils.showLoading(sendCodeBtn);

        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/action/send-verification', {
            //     method: 'POST',
            //     body: JSON.stringify({ username, password })
            // });

            // 임시로 성공 처리
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            Utils.showSuccess('인증코드가 이메일로 발송되었습니다.');
            
            // 버튼 비활성화 (60초)
            sendCodeBtn.disabled = true;
            let countdown = 60;
            const timer = setInterval(() => {
                sendCodeBtn.textContent = `재발송 (${countdown}초)`;
                countdown--;
                
                if (countdown < 0) {
                    clearInterval(timer);
                    sendCodeBtn.disabled = false;
                    sendCodeBtn.textContent = '인증코드 발송';
                }
            }, 1000);

        } catch (error) {
            Utils.showErrorMessage(error.message || '인증코드 발송 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(sendCodeBtn);
        }
    },

    // 로그인 처리
    async handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const loginData = Object.fromEntries(formData.entries());

        const submitBtn = document.getElementById('signinBtn');
        Utils.showLoading(submitBtn);

        try {
            // 실제 서버 API 호출
            const response = await fetch('/users/action/signin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(loginData)
            });

            const result = await response.json();

            if (result.success) {
                // 사용자 정보 저장 (서버에서 받은 데이터)
                Utils.setCurrentUser(result.user);
                
                // 헤더 메뉴 업데이트
                if (typeof updateHeaderMenu === 'function') {
                    updateHeaderMenu();
                }
                
                Utils.showSuccess('로그인되었습니다. 마이페이지로 이동합니다.');
                
                setTimeout(() => {
                    window.location.href = '/users/me';
                }, 1500);
            } else {
                Utils.showErrorMessage(result.message || '로그인 중 오류가 발생했습니다.');
            }

        } catch (error) {
            Utils.showErrorMessage('로그인 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(submitBtn);
        }
    }
};

// 마이페이지 관련 함수들
const MyPageHandler = {
    // 페이지 초기화
    init: async function() {
        try {
            await this.loadUserInfo();
            // JSP에서 예약 데이터가 이미 렌더링되므로 loadReservations 호출하지 않음
            console.log('마이페이지 초기화 완료');
        } catch (error) {
            console.error('마이페이지 초기화 오류:', error);
        }
    },

    // 사용자 정보 로드
    async loadUserInfo() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) {
                Utils.redirectToLogin();
                return;
            }

            // 서버에서 전달받은 사용자 정보가 이미 JSP에 포함되어 있으므로
            // JavaScript로 DOM 요소를 업데이트할 필요가 없음
            // JSP에서 JSTL을 통해 사용자 정보가 이미 표시됨
            console.log('마이페이지 사용자 정보 로드 완료');

        } catch (error) {
            console.error('사용자 정보 로드 오류:', error);
            Utils.showErrorMessage('사용자 정보를 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 예약 내역 로드
    async loadReservations() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) return;

            // JSP에서 이미 예약 데이터가 렌더링되어 있으므로
            // JavaScript로 추가 로드할 필요 없음
            console.log('예약 내역 로드 완료 (JSP에서 렌더링됨)');

        } catch (error) {
            console.error('예약 내역 로드 오류:', error);
            Utils.showErrorMessage('예약 내역을 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 예약 내역 표시
    displayReservations(reservations) {
        const container = document.getElementById('reservationList');
        
        if (!container) {
            console.log('예약 내역 컨테이너를 찾을 수 없습니다.');
            return;
        }
        
        if (!reservations || reservations.length === 0) {
            container.innerHTML = '<div class="no-data"><p>예약 내역이 없습니다.</p></div>';
            return;
        }

        const html = reservations.map(reservation => `
            <div class="reservation-item">
                <div class="reservation-header">
                    <h4>${reservation.hospitalName}</h4>
                    <span class="reservation-status status-${reservation.status}">
                        ${this.getStatusText(reservation.status)}
                    </span>
                </div>
                <div class="reservation-details">
                    <div class="detail-item">
                        <span class="detail-label">진료과</span>
                        <span class="detail-value">${reservation.deptName}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">의사</span>
                        <span class="detail-value">${reservation.doctorName}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">예약일시</span>
                        <span class="detail-value">${reservation.resDate}</span>
                    </div>
                </div>
            </div>
        `).join('');

        container.innerHTML = html;
    },

    // 상태 텍스트 변환
    getStatusText(status) {
        const statusMap = {
            'pending': '예약중',
            'confirmed': '확정',
            'cancelled': '취소'
        };
        return statusMap[status] || status;
    }
};

// 회원정보 수정 관련 함수들
const EditProfileHandler = {
    // 폼 초기화
    init: function() {
        const form = document.getElementById('editProfileForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
            this.setupValidation();
        }
        this.loadUserInfo();
    },

    // 실시간 유효성 검사 설정
    setupValidation: function() {
        const currentPasswordInput = document.getElementById('currentPassword');
        const newPasswordInput = document.getElementById('newPassword');
        const confirmNewPasswordInput = document.getElementById('confirmNewPassword');
        const nameInput = document.getElementById('name');
        const emailInput = document.getElementById('email');
        const phoneInput = document.getElementById('phone');

        if (currentPasswordInput) {
            currentPasswordInput.addEventListener('blur', () => this.validateCurrentPassword());
            currentPasswordInput.addEventListener('input', () => Utils.hideError('currentPasswordError'));
        }

        if (newPasswordInput) {
            newPasswordInput.addEventListener('blur', () => this.validateNewPassword());
            newPasswordInput.addEventListener('input', () => Utils.hideError('newPasswordError'));
        }

        if (confirmNewPasswordInput) {
            confirmNewPasswordInput.addEventListener('blur', () => this.validateConfirmNewPassword());
            confirmNewPasswordInput.addEventListener('input', () => Utils.hideError('confirmNewPasswordError'));
        }

        if (nameInput) {
            nameInput.addEventListener('blur', () => this.validateName());
            nameInput.addEventListener('input', () => Utils.hideError('nameError'));
        }

        if (emailInput) {
            emailInput.addEventListener('blur', () => this.validateEmail());
            emailInput.addEventListener('input', () => Utils.hideError('emailError'));
        }

        if (phoneInput) {
            phoneInput.addEventListener('blur', () => this.validatePhone());
            phoneInput.addEventListener('input', () => Utils.hideError('phoneError'));
        }
    },

            // 현재 비밀번호 유효성 검사
            validateCurrentPassword: function() {
                const currentPassword = document.getElementById('currentPassword').value;
                
                // 현재 비밀번호는 항상 필수
                if (!currentPassword || currentPassword.trim() === '') {
                    Utils.showError('currentPasswordError', '현재 비밀번호를 입력해주세요.');
                    return false;
                }

                Utils.hideError('currentPasswordError');
                return true;
            },

    // 새 비밀번호 유효성 검사
    validateNewPassword: function() {
        const newPassword = document.getElementById('newPassword').value;
        
        // 비밀번호가 입력되지 않았으면 통과 (선택사항)
        if (!newPassword || newPassword.trim() === '') {
            Utils.hideError('newPasswordError');
            return true;
        }

        if (!Utils.validatePassword(newPassword)) {
            Utils.showError('newPasswordError', '영문, 숫자, 특수문자(!@#$%^&*)를 포함하여 8자 이상 입력해주세요.');
            return false;
        }

        Utils.hideError('newPasswordError');
        return true;
    },

    // 새 비밀번호 확인 검사
    validateConfirmNewPassword: function() {
        const newPassword = document.getElementById('newPassword').value;
        const confirmNewPassword = document.getElementById('confirmNewPassword').value;
        
        // 새 비밀번호가 입력되지 않았으면 통과
        if (!newPassword || newPassword.trim() === '') {
            Utils.hideError('confirmNewPasswordError');
            return true;
        }

        if (!Utils.validateNotBlank(confirmNewPassword)) {
            Utils.showError('confirmNewPasswordError', '새 비밀번호 확인을 입력해주세요.');
            return false;
        }

        if (newPassword !== confirmNewPassword) {
            Utils.showError('confirmNewPasswordError', '새 비밀번호가 일치하지 않습니다.');
            return false;
        }

        Utils.hideError('confirmNewPasswordError');
        return true;
    },

    // 이름 유효성 검사
    validateName: function() {
        const name = document.getElementById('name').value.trim();
        
        if (!Utils.validateNotBlank(name)) {
            Utils.showError('nameError', '이름을 입력해주세요.');
            return false;
        }

        if (!Utils.validateName(name)) {
            Utils.showError('nameError', '한글 2-5자로 입력해주세요.');
            return false;
        }

        Utils.hideError('nameError');
        return true;
    },

    // 이메일 유효성 검사
    validateEmail: function() {
        const email = document.getElementById('email').value.trim();
        
        if (!Utils.validateNotBlank(email)) {
            Utils.showError('emailError', '이메일을 입력해주세요.');
            return false;
        }

        if (!Utils.validateEmail(email)) {
            Utils.showError('emailError', '올바른 이메일 형식을 입력해주세요.');
            return false;
        }

        Utils.hideError('emailError');
        return true;
    },

    // 전화번호 유효성 검사
    validatePhone: function() {
        const phone = document.getElementById('phone').value.trim();
        
        if (!Utils.validateNotBlank(phone)) {
            Utils.showError('phoneError', '전화번호를 입력해주세요.');
            return false;
        }

        if (!Utils.validatePhone(phone)) {
            Utils.showError('phoneError', '010-1234-5678 형식으로 입력해주세요.');
            return false;
        }

        Utils.hideError('phoneError');
        return true;
    },

    // 전체 폼 유효성 검사
    validateForm: function() {
        const isValid = 
            this.validateCurrentPassword() &&
            this.validateNewPassword() &&
            this.validateConfirmNewPassword() &&
            this.validateName() &&
            this.validateEmail() &&
            this.validatePhone();

        return isValid;
    },

    // 사용자 정보 로드
    async loadUserInfo() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) {
                Utils.redirectToLogin();
                return;
            }

            // 서버에서 전달받은 사용자 정보가 이미 JSP에 포함되어 있으므로
            // JavaScript로 DOM 요소를 업데이트할 필요가 없음
            // JSP에서 JSTL을 통해 사용자 정보가 이미 표시됨
            console.log('정보 수정 페이지 사용자 정보 로드 완료');

        } catch (error) {
            console.error('사용자 정보 로드 오류:', error);
            Utils.showErrorMessage('사용자 정보를 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 폼 제출 처리
    async handleSubmit(event) {
        event.preventDefault();

        if (!this.validateForm()) {
            Utils.showErrorMessage('입력 정보를 확인해주세요.');
            return;
        }

        const formData = new FormData(event.target);
        const updateData = Object.fromEntries(formData.entries());

        const submitBtn = event.target.querySelector('button[type="submit"]');
        Utils.showLoading(submitBtn);

        try {
            // 실제 서버 API 호출
            const response = await fetch('/users/action/edit', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(updateData)
            });

            const result = await response.json();

            if (result.success) {
                Utils.showSuccess('회원정보가 수정되었습니다.');
                
                setTimeout(() => {
                    window.location.href = '/users/me';
                }, 1500);
            } else {
                Utils.showErrorMessage(result.message || '회원정보 수정 중 오류가 발생했습니다.');
            }

        } catch (error) {
            Utils.showErrorMessage('회원정보 수정 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(submitBtn);
        }
    }
};

// 회원 탈퇴 관련 함수들
const WithdrawHandler = {
    // 폼 초기화
    init: function() {
        const form = document.getElementById('withdrawForm');
        if (form) {
            form.addEventListener('submit', this.handleSubmit.bind(this));
            this.setupValidation();
        }
    },

    // 실시간 유효성 검사 설정
    setupValidation: function() {
        const passwordInput = document.getElementById('password');
        const reasonSelect = document.getElementById('reason');
        const agreeCheckbox = document.getElementById('agreeWithdraw');

        if (passwordInput) {
            passwordInput.addEventListener('blur', () => this.validatePassword());
            passwordInput.addEventListener('input', () => Utils.hideError('passwordError'));
        }

        if (reasonSelect) {
            reasonSelect.addEventListener('change', () => this.validateReason());
        }

        if (agreeCheckbox) {
            agreeCheckbox.addEventListener('change', () => this.validateAgreement());
        }
    },

    // 비밀번호 유효성 검사
    validatePassword: function() {
        const password = document.getElementById('password').value;
        
        if (!Utils.validateNotBlank(password)) {
            Utils.showError('passwordError', '비밀번호를 입력해주세요.');
            return false;
        }

        Utils.hideError('passwordError');
        return true;
    },

    // 탈퇴 사유 유효성 검사
    validateReason: function() {
        const reason = document.getElementById('reason').value;
        
        if (!reason || reason.trim() === '') {
            Utils.showError('reasonError', '탈퇴 사유를 선택해주세요.');
            return false;
        }

        Utils.hideError('reasonError');
        return true;
    },

    // 동의 체크박스 유효성 검사
    validateAgreement: function() {
        const agreeCheckbox = document.getElementById('agreeWithdraw');
        
        if (!agreeCheckbox.checked) {
            Utils.showError('agreementError', '탈퇴 동의에 체크해주세요.');
            return false;
        }

        Utils.hideError('agreementError');
        return true;
    },

    // 전체 폼 유효성 검사
    validateForm: function() {
        const isValid = 
            this.validatePassword() &&
            this.validateReason() &&
            this.validateAgreement();

        return isValid;
    },

    // 탈퇴 신청 처리
    async handleSubmit(event) {
        event.preventDefault();

        if (!this.validateForm()) {
            Utils.showErrorMessage('입력 정보를 확인해주세요.');
            return;
        }

        // 최종 확인
        if (!confirm('정말로 회원 탈퇴를 진행하시겠습니까?\n탈퇴 후에는 되돌릴 수 없습니다.')) {
            return;
        }

        const formData = new FormData(event.target);
        const withdrawData = Object.fromEntries(formData.entries());

        const submitBtn = event.target.querySelector('button[type="submit"]');
        Utils.showLoading(submitBtn);

        try {
            // 실제 서버 API 호출
            const response = await fetch('/users/action/withdraw', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(withdrawData)
            });

            const result = await response.json();

            if (result.success) {
                Utils.showSuccess('회원 탈퇴가 완료되었습니다.');
                
                setTimeout(() => {
                    Utils.removeCurrentUser();
                    window.location.href = '/';
                }, 2000);
            } else {
                Utils.showErrorMessage(result.message || '회원 탈퇴 중 오류가 발생했습니다.');
            }

        } catch (error) {
            Utils.showErrorMessage('회원 탈퇴 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(submitBtn);
        }
    }
};

// 전역 함수들
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        Utils.removeCurrentUser();
        Utils.showSuccess('로그아웃되었습니다.');
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
}

function editProfile() {
    window.location.href = '/users/edit-profile';
}

function withdrawRequest() {
    window.location.href = '/users/withdraw';
}

function goBack() {
    window.history.back();
}

// 예약 관련 전역 함수들
let currentReservationId = null;

function goToPayment(reservationId) {
    currentReservationId = reservationId;
    openPaymentModal(reservationId);
}

async function cancelReservation(reservationId) {
    if (confirm('정말로 예약을 취소하시겠습니까?\n\n예약 취소 시 결제도 함께 취소됩니다.')) {
        try {
            const response = await fetch(`/api/reservations/${reservationId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (result.success) {
                alert('예약이 취소되었습니다.\n결제도 함께 취소되었습니다.');
                // 페이지 새로고침
                window.location.reload();
            } else {
                alert('예약 취소 실패: ' + result.message);
            }
        } catch (error) {
            console.error('예약 취소 API 호출 실패:', error);
            alert('예약 취소 처리 중 오류가 발생했습니다.');
        }
    }
}

// 결제 모달 관련 함수들
function openPaymentModal(reservationId) {
    // 예약 정보를 모달에 표시
    loadReservationInfoForPayment(reservationId);
    
    // 모달 표시
    const modal = document.getElementById('paymentModal');
    modal.style.display = 'block';
    
    // 모달 외부 클릭 시 닫기
    window.onclick = function(event) {
        if (event.target === modal) {
            closePaymentModal();
        }
    };
}

function closePaymentModal() {
    const modal = document.getElementById('paymentModal');
    modal.style.display = 'none';
    currentReservationId = null;
}

async function loadReservationInfoForPayment(reservationId) {
    try {
        // 예약 정보를 가져와서 모달에 표시
        const reservationInfo = document.getElementById('paymentReservationInfo');
        
        // 현재 페이지의 예약 정보에서 해당 예약 찾기
        const reservationItems = document.querySelectorAll('.reservation-item');
        let reservationData = null;
        
        for (let item of reservationItems) {
            const cancelBtn = item.querySelector('button[onclick*="cancelReservation"]');
            if (cancelBtn) {
                const onclickAttr = cancelBtn.getAttribute('onclick');
                const match = onclickAttr.match(/cancelReservation\((\d+)\)/);
                if (match && match[1] == reservationId) {
                    // 예약 정보 추출
                    const hospitalName = item.querySelector('h4').textContent;
                    const deptName = item.querySelector('p:nth-of-type(1)').textContent.replace('진료과: ', '');
                    const doctorName = item.querySelector('p:nth-of-type(2)').textContent.replace('의사: ', '');
                    const resDate = item.querySelector('p:nth-of-type(3)').textContent.replace('예약일시: ', '');
                    
                    reservationData = {
                        hospitalName,
                        deptName,
                        doctorName,
                        resDate
                    };
                    break;
                }
            }
        }
        
        if (reservationData) {
            reservationInfo.innerHTML = `
                <p><strong>병원:</strong> ${reservationData.hospitalName}</p>
                <p><strong>진료과:</strong> ${reservationData.deptName}</p>
                <p><strong>의사:</strong> ${reservationData.doctorName}</p>
                <p><strong>예약일시:</strong> ${reservationData.resDate}</p>
            `;
        } else {
            reservationInfo.innerHTML = '<p>예약 정보를 불러올 수 없습니다.</p>';
        }
    } catch (error) {
        console.error('예약 정보 로드 실패:', error);
        document.getElementById('paymentReservationInfo').innerHTML = '<p>예약 정보를 불러올 수 없습니다.</p>';
    }
}

async function processPayment() {
    if (!currentReservationId) {
        alert('예약 정보를 찾을 수 없습니다.');
        return;
    }
    
    const amount = document.getElementById('paymentAmount').value;
    const method = document.querySelector('input[name="paymentMethod"]:checked').value;
    
    // 입력 검증
    if (!amount || amount < 1000) {
        alert('결제 금액은 1,000원 이상이어야 합니다.');
        return;
    }
    
    if (!method) {
        alert('결제 방법을 선택해주세요.');
        return;
    }
    
    if (confirm(`결제 금액: ${parseInt(amount).toLocaleString()}원\n결제 방법: ${method}\n\n결제를 진행하시겠습니까?`)) {
        try {
            // 결제 생성 API 호출
            const response = await fetch('/api/payments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    resId: currentReservationId,
                    amount: amount,
                    method: method
                })
            });
            
            const result = await response.json();
            
            if (result.success) {
                // 결제 완료 처리
                const completeResponse = await fetch(`/api/payments/${result.paymentId}/complete`, {
                    method: 'POST'
                });
                
                const completeResult = await completeResponse.json();
                
                if (completeResult.success) {
                    alert('결제가 완료되었습니다!');
                    closePaymentModal();
                    // 페이지 새로고침
                    window.location.reload();
                } else {
                    alert('결제 완료 처리 실패: ' + completeResult.message);
                }
            } else {
                alert('결제 실패: ' + result.message);
            }
        } catch (error) {
            console.error('결제 API 호출 실패:', error);
            alert('결제 처리 중 오류가 발생했습니다.');
        }
    }
}

// 전역 함수 등록
window.goToPayment = goToPayment;
window.cancelReservation = cancelReservation;
window.openPaymentModal = openPaymentModal;
window.closePaymentModal = closePaymentModal;
window.processPayment = processPayment;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    
    if (currentPath.includes('/signup')) {
        SignupHandler.init();
    } else if (currentPath.includes('/signin')) {
        SigninHandler.init();
    } else if (currentPath.includes('/mypage') || currentPath.includes('/me')) {
        MyPageHandler.init();
    } else if (currentPath.includes('/edit-profile')) {
        EditProfileHandler.init();
    } else if (currentPath.includes('/withdraw')) {
        WithdrawHandler.init();
    }
});
