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
            Utils.showError('usernameError', '6-20자 영문, 숫자, 특수문자(-, _) 조합으로 입력해주세요.');
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
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/action/signup', {
            //     method: 'POST',
            //     body: JSON.stringify(userData)
            // });

            // 임시로 성공 처리
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            Utils.showSuccess('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
            
            setTimeout(() => {
                window.location.href = '/users/signin';
            }, 2000);

        } catch (error) {
            Utils.showErrorMessage(error.message || '회원가입 중 오류가 발생했습니다.');
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
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/action/signin', {
            //     method: 'POST',
            //     body: JSON.stringify(loginData)
            // });

            // 임시로 성공 처리
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            // 사용자 정보 저장 (실제로는 서버에서 받은 데이터)
            const userData = {
                username: loginData.username,
                name: '홍길동', // 임시 데이터
                email: 'test@example.com',
                role: 'patient'
            };
            Utils.setCurrentUser(userData);
            
            // 헤더 메뉴 업데이트
            if (typeof updateHeaderMenu === 'function') {
                updateHeaderMenu();
            }
            
            Utils.showSuccess('로그인되었습니다. 메인 페이지로 이동합니다.');
            
            setTimeout(() => {
                window.location.href = '/';
            }, 1500);

        } catch (error) {
            Utils.showErrorMessage(error.message || '로그인 중 오류가 발생했습니다.');
        } finally {
            Utils.hideLoading(submitBtn);
        }
    }
};

// 마이페이지 관련 함수들
const MyPageHandler = {
    // 페이지 초기화
    init: function() {
        this.loadUserInfo();
        this.loadReservations();
    },

    // 사용자 정보 로드
    async loadUserInfo() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) {
                Utils.redirectToLogin();
                return;
            }

            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/me');
            // const userInfo = response.data;

            // 임시 데이터
            const userInfo = {
                username: currentUser.username,
                name: currentUser.name,
                email: currentUser.email,
                phone: '010-1234-5678',
                createdAt: '2024-01-01'
            };

            // 사용자 정보 표시
            document.getElementById('userUsername').textContent = userInfo.username;
            document.getElementById('userName').textContent = userInfo.name;
            document.getElementById('userEmail').textContent = userInfo.email;
            document.getElementById('userPhone').textContent = userInfo.phone;
            document.getElementById('userCreatedAt').textContent = userInfo.createdAt;

        } catch (error) {
            Utils.showErrorMessage('사용자 정보를 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 예약 내역 로드
    async loadReservations() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) return;

            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall(`/reservations/user/${currentUser.userId}`);
            // const reservations = response.data;

            // 임시 데이터
            const reservations = [
                {
                    resId: 1,
                    hospitalName: '서울대병원',
                    deptName: '내과',
                    doctorName: '김의사',
                    resDate: '2024-01-15 14:00',
                    status: 'confirmed'
                },
                {
                    resId: 2,
                    hospitalName: '삼성서울병원',
                    deptName: '외과',
                    doctorName: '이의사',
                    resDate: '2024-01-20 10:30',
                    status: 'pending'
                }
            ];

            this.displayReservations(reservations);

        } catch (error) {
            Utils.showErrorMessage('예약 내역을 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 예약 내역 표시
    displayReservations(reservations) {
        const container = document.getElementById('reservationList');
        
        if (reservations.length === 0) {
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
        }
        this.loadUserInfo();
    },

    // 사용자 정보 로드
    async loadUserInfo() {
        try {
            const currentUser = Utils.getCurrentUser();
            if (!currentUser) {
                Utils.redirectToLogin();
                return;
            }

            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/me');
            // const userInfo = response.data;

            // 임시 데이터
            const userInfo = {
                name: currentUser.name,
                email: currentUser.email,
                phone: '010-1234-5678'
            };

            // 폼에 데이터 설정
            document.getElementById('name').value = userInfo.name;
            document.getElementById('email').value = userInfo.email;
            document.getElementById('phone').value = userInfo.phone;

        } catch (error) {
            Utils.showErrorMessage('사용자 정보를 불러오는 중 오류가 발생했습니다.');
        }
    },

    // 폼 제출 처리
    async handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const updateData = Object.fromEntries(formData.entries());

        const submitBtn = event.target.querySelector('button[type="submit"]');
        Utils.showLoading(submitBtn);

        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/action/edit', {
            //     method: 'PUT',
            //     body: JSON.stringify(updateData)
            // });

            // 임시로 성공 처리
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            Utils.showSuccess('회원정보가 수정되었습니다.');
            
            setTimeout(() => {
                window.location.href = '/users/me';
            }, 1500);

        } catch (error) {
            Utils.showErrorMessage(error.message || '회원정보 수정 중 오류가 발생했습니다.');
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
        }
    },

    // 탈퇴 신청 처리
    async handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const withdrawData = Object.fromEntries(formData.entries());

        const submitBtn = event.target.querySelector('button[type="submit"]');
        Utils.showLoading(submitBtn);

        try {
            // 실제 구현에서는 서버 API 호출
            // const response = await Utils.apiCall('/users/action/withdraw', {
            //     method: 'POST',
            //     body: JSON.stringify(withdrawData)
            // });

            // 임시로 성공 처리
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            Utils.showSuccess('탈퇴 신청이 완료되었습니다. 관리자 승인 후 처리됩니다.');
            
            setTimeout(() => {
                Utils.removeCurrentUser();
                window.location.href = '/';
            }, 2000);

        } catch (error) {
            Utils.showErrorMessage(error.message || '탈퇴 신청 중 오류가 발생했습니다.');
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
