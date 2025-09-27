// 공통 JavaScript 함수들

// 유틸리티 함수들
const Utils = {
    // 이메일 유효성 검사
    validateEmail: function(email) {
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return emailRegex.test(email);
    },

    // 전화번호 유효성 검사
    validatePhone: function(phone) {
        const phoneRegex = /^010-\d{4}-\d{4}$/;
        return phoneRegex.test(phone);
    },

    // 아이디 유효성 검사
    validateUsername: function(username) {
        const usernameRegex = /^[a-zA-Z0-9-_]{6,20}$/;
        return usernameRegex.test(username);
    },

    // 비밀번호 유효성 검사
    validatePassword: function(password) {
        const passwordRegex = /^[A-Za-z0-9]*[!@#$%^&*][A-Za-z0-9]*$/;
        return passwordRegex.test(password) && password.length >= 8;
    },

    // 이름 유효성 검사
    validateName: function(name) {
        const nameRegex = /^[가-힣]{2,5}$/;
        return nameRegex.test(name);
    },

    // 공백 검사
    validateNotBlank: function(value) {
        return value.trim().length > 0;
    },

    // 전화번호 포맷팅
    formatPhone: function(phone) {
        const cleaned = phone.replace(/\D/g, '');
        if (cleaned.length === 11) {
            return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        }
        return phone;
    },

    // 에러 메시지 표시
    showError: function(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('show');
        }
    },

    // 에러 메시지 숨기기
    hideError: function(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('show');
        }
    },

    // 성공 메시지 표시
    showSuccess: function(message) {
        this.showMessage(message, 'success');
    },

    // 에러 메시지 표시
    showErrorMessage: function(message) {
        this.showMessage(message, 'error');
    },

    // 정보 메시지 표시
    showInfo: function(message) {
        this.showMessage(message, 'info');
    },

    // 메시지 표시 (공통)
    showMessage: function(message, type) {
        // 기존 메시지 제거
        const existingMessage = document.querySelector('.message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // 새 메시지 생성
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        messageDiv.textContent = message;

        // 폼 앞에 메시지 삽입
        const form = document.querySelector('form');
        if (form) {
            form.parentNode.insertBefore(messageDiv, form);
        }

        // 5초 후 자동 제거
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    },

    // 로딩 상태 표시
    showLoading: function(button) {
        const originalText = button.textContent;
        button.dataset.originalText = originalText;
        button.innerHTML = '<span class="loading"></span> 처리 중...';
        button.disabled = true;
    },

    // 로딩 상태 해제
    hideLoading: function(button) {
        const originalText = button.dataset.originalText;
        button.textContent = originalText;
        button.disabled = false;
    },

    // API 호출
    apiCall: async function(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const mergedOptions = { ...defaultOptions, ...options };

        try {
            const response = await fetch(url, mergedOptions);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || '요청 처리 중 오류가 발생했습니다.');
            }

            return data;
        } catch (error) {
            console.error('API 호출 오류:', error);
            throw error;
        }
    },

    // 로컬 스토리지에서 사용자 정보 가져오기
    getCurrentUser: function() {
        // 프리뷰 경로에서는 더미 사용자로 동작
        const path = window.location && window.location.pathname ? window.location.pathname : '';
        if (path.startsWith('/preview')) {
            return {
                username: 'previewUser',
                name: '프리뷰',
                email: 'preview@example.com',
                role: 'patient'
            };
        }
        
        // 실제 로그인 상태 확인
        const userStr = localStorage.getItem('currentUser');
        if (userStr) return JSON.parse(userStr);
        
        return null;
    },

    // 로컬 스토리지에 사용자 정보 저장
    setCurrentUser: function(user) {
        localStorage.setItem('currentUser', JSON.stringify(user));
    },

    // 로컬 스토리지에서 사용자 정보 제거
    removeCurrentUser: function() {
        localStorage.removeItem('currentUser');
    },

    // 로그인 상태 확인
    isLoggedIn: function() {
        return this.getCurrentUser() !== null;
    },

    // 로그인 페이지로 리다이렉트
    redirectToLogin: function() {
        const path = window.location && window.location.pathname ? window.location.pathname : '';
        // 프리뷰 경로는 리다이렉트 금지
        if (path.startsWith('/preview')) return;
        window.location.href = '/users/signin';
    },

    // 페이지 새로고침
    reloadPage: function() {
        window.location.reload();
    }
};

// 전화번호 자동 포맷팅
document.addEventListener('DOMContentLoaded', function() {
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 11) {
                value = value.substring(0, 11);
            }
            e.target.value = Utils.formatPhone(value);
        });
    });
});

// 전역 함수들
window.Utils = Utils;
