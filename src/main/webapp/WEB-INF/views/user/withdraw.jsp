<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 회원 탈퇴</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/user.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <div class="logo">
                <h1>ClinicMate</h1>
            </div>
            <nav class="nav">
                <a href="/" class="nav-link">홈</a>
                <a href="/users/me" class="nav-link">마이페이지</a>
                <button class="btn btn-secondary" onclick="logout()">로그아웃</button>
            </nav>
        </header>
        
        <main class="main-content">
            <div class="withdraw-container">
                <h2>회원 탈퇴</h2>
                
                <div class="warning-box">
                    <h3>⚠️ 탈퇴 안내</h3>
                    <ul>
                        <li>탈퇴 신청 후 관리자 승인이 필요합니다.</li>
                        <li>탈퇴 승인 시 모든 개인정보가 삭제됩니다.</li>
                        <li>진행 중인 예약이 있는 경우 탈퇴가 제한될 수 있습니다.</li>
                        <li>탈퇴 후 동일한 아이디로 재가입이 불가능합니다.</li>
                    </ul>
                </div>
                
                <form id="withdrawForm" class="withdraw-form" action="/users/action/withdraw" method="POST">
                    <div class="form-group">
                        <label for="password">비밀번호 확인</label>
                        <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
                        <div class="error-message" id="passwordError"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="reason">탈퇴 사유</label>
                        <select id="reason" name="reason" required>
                            <option value="">탈퇴 사유를 선택하세요</option>
                            <option value="service_dissatisfaction">서비스 불만족</option>
                            <option value="privacy_concern">개인정보 보호 우려</option>
                            <option value="alternative_service">다른 서비스 이용</option>
                            <option value="no_use">사용하지 않음</option>
                            <option value="other">기타</option>
                        </select>
                        <div class="error-message" id="reasonError"></div>
                    </div>
                    
                    <div class="form-group">
                        <label for="detailReason">상세 사유 (선택사항)</label>
                        <textarea id="detailReason" name="detailReason" placeholder="탈퇴 사유를 자세히 적어주세요" rows="4"></textarea>
                    </div>
                    
                    <div class="checkbox-group">
                        <label class="checkbox-label">
                            <input type="checkbox" id="agreeWithdraw" required>
                            <span class="checkmark"></span>
                            위 내용을 확인했으며, 회원 탈퇴에 동의합니다.
                        </label>
                    </div>
                    
                    <div class="button-group">
                        <button type="submit" class="btn btn-danger">탈퇴 신청</button>
                        <button type="button" class="btn btn-secondary" onclick="goBack()">취소</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/user.js"></script>
</body>
</html>

