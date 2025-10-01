<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ClinicMate - 병원 예약 시스템</title>
    <link rel="stylesheet" href="/style/common.css">
    <link rel="stylesheet" href="/style/main.css">
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=aa5282a38622982d20efb6e4e5a4b894"></script>
    <script>
        // 카카오맵 API 키 설정
        const KAKAO_MAP_API_KEY = 'aa5282a38622982d20efb6e4e5a4b894';
        console.log('카카오맵 API 스크립트 로드됨');
    </script>
    <script src="/script/common.js"></script>
</head>
<body>
    <div class="container">
        <!-- 헤더 -->
        <jsp:include page="/WEB-INF/views/module/header.jsp" />
        
        <!-- 메인 콘텐츠 -->
        <main class="main-content">
            <div class="main-container">
                <!-- 지도 영역 -->
                <div class="map-section">
                    <div class="map-header">
                        <h2>주변 병원 찾기</h2>
                        <div class="location-controls">
                            <button id="currentLocationBtn" class="btn btn-primary">현재 위치 찾기</button>
                            <button id="showAllHospitalsBtn" class="btn btn-secondary">모든 병원 보기</button>
                        </div>
                    </div>
                    <div id="map" class="map-container"></div>
                </div>
                
                <!-- 병원 정보 영역 -->
                <div class="hospital-info-section">
                    <div class="hospital-header">
                        <h3>병원 정보</h3>
                        <div class="search-controls">
                            <input type="text" id="hospitalSearchInput" placeholder="병원명으로 검색..." class="search-input">
                            <button id="hospitalSearchBtn" class="btn btn-primary">검색</button>
                        </div>
                    </div>
                    
                    <div class="hospital-list" id="hospitalList">
                        <c:choose>
                            <c:when test="${not empty hospitals}">
                                <c:forEach var="hospital" items="${hospitals}">
                                    <div class="hospital-card" data-hospital-id="${hospital.hospitalId}" 
                                         data-lat="${hospital.lat}" data-lng="${hospital.lng}">
                                        <div class="hospital-info">
                                            <h4 class="hospital-name">${hospital.hospitalName}</h4>
                                            <p class="hospital-address">${hospital.address}</p>
                                            <p class="hospital-phone">${hospital.phone}</p>
                                        </div>
                                        <div class="hospital-actions">
                                            <button class="btn btn-primary btn-sm" onclick="goToReservation('${hospital.hospitalId}')">
                                                예약하기
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="no-data">
                                    <p>등록된 병원이 없습니다.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            
            <!-- 병원 상세 정보 모달 -->
            <div id="hospitalDetailModal" class="modal">
                <div class="modal-content">
                    <span class="close-button">&times;</span>
                    <h2 id="modalHospitalName"></h2>
                    <p><strong>주소:</strong> <span id="modalHospitalAddress"></span></p>
                    <p><strong>전화번호:</strong> <span id="modalHospitalPhone"></span></p>
                    <h3>진료과</h3>
                    <div id="modalDepartmentList"></div>
                    <button id="reserveBtn" class="btn btn-primary">예약하기</button>
                </div>
            </div>
        </main>
        
        <!-- 푸터 -->
        <jsp:include page="/WEB-INF/views/module/footer.jsp" />
    </div>
    
    <script src="/script/main.js"></script>
</body>
</html>
