<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<header class="header">
    <div class="header-container">
        <div class="logo">
            <a href="/main" class="logo-link">
                <h1>ClinicMate</h1>
                <span class="logo-subtitle">병원 예약 관리 시스템</span>
            </a>
        </div>
        
        <div class="auth-section">
            <!-- 로그인 안한 상태 -->
            <div id="guestMenu" class="auth-menu">
                <a href="/users/signin" class="nav-link">로그인</a>
                <a href="/users/signup" class="nav-link">회원가입</a>
            </div>
            
            <!-- 로그인한 상태 -->
            <div id="userMenu" class="auth-menu" style="display: none;">
                <a href="/users/me" class="nav-link">마이페이지</a>
                <button class="btn btn-secondary" onclick="logout()">로그아웃</button>
            </div>
            
        </div>
    </div>
</header>

<script>
// 헤더 메뉴 상태 관리
function updateHeaderMenu() {
    const currentUser = Utils.getCurrentUser();
    const guestMenu = document.getElementById('guestMenu');
    const userMenu = document.getElementById('userMenu');
    
    // 프리뷰 경로가 아닌 경우에만 실제 로그인 상태 확인
    const path = window.location && window.location.pathname ? window.location.pathname : '';
    const isPreview = path.startsWith('/preview');
    
    // 디버깅을 위한 로그
    console.log('updateHeaderMenu - path:', path, 'isPreview:', isPreview, 'currentUser:', currentUser);
    
    // 로그인 페이지에서는 항상 게스트 메뉴 표시
    if (path === '/users/signin' || path === '/users/signup') {
        guestMenu.style.display = 'flex';
        userMenu.style.display = 'none';
        console.log('헤더: 로그인/회원가입 페이지 - 게스트 메뉴 표시');
        return;
    }
    
    if (currentUser && !isPreview) {
        // 실제 로그인한 상태 (프리뷰가 아닌 경우)
        guestMenu.style.display = 'none';
        userMenu.style.display = 'flex';
        console.log('헤더: 로그인 상태로 변경');
    } else {
        // 로그인 안한 상태 또는 프리뷰 모드
        guestMenu.style.display = 'flex';
        userMenu.style.display = 'none';
        console.log('헤더: 비로그인 상태로 변경');
    }
}

// 페이지 로드 시 메뉴 상태 업데이트
document.addEventListener('DOMContentLoaded', function() {
    // 페이지 로드 시 localStorage 정리
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
        try {
            const user = JSON.parse(userStr);
            // 유효하지 않은 사용자 정보면 제거
            if (!user || !user.username || user.username === 'previewUser') {
                localStorage.removeItem('currentUser');
                console.log('유효하지 않은 사용자 정보 제거됨');
            }
        } catch (e) {
            localStorage.removeItem('currentUser');
            console.log('잘못된 사용자 정보 제거됨');
        }
    }
    
    updateHeaderMenu();
});

// 로그아웃 함수 수정
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        Utils.removeCurrentUser();
        updateHeaderMenu(); // 헤더 메뉴 즉시 업데이트
        Utils.showSuccess('로그아웃되었습니다.');
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
    }
}

</script>
