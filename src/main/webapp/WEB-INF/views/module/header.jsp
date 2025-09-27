<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<header class="header">
    <div class="header-container">
        <div class="logo">
            <a href="/" class="logo-link">
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
    
    if (currentUser) {
        // 로그인한 상태
        guestMenu.style.display = 'none';
        userMenu.style.display = 'flex';
    } else {
        // 로그인 안한 상태
        guestMenu.style.display = 'flex';
        userMenu.style.display = 'none';
    }
}

// 페이지 로드 시 메뉴 상태 업데이트
document.addEventListener('DOMContentLoaded', function() {
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
