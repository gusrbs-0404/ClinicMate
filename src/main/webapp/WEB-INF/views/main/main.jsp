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
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=YOUR_KAKAO_MAP_API_KEY"></script>
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
                            <button id="getCurrentLocation" class="btn btn-primary">내 위치 찾기</button>
                            <button id="showAllHospitals" class="btn btn-secondary">전체 병원 보기</button>
                        </div>
                    </div>
                    <div id="map" class="map-container"></div>
                </div>
                
                <!-- 병원 정보 영역 -->
                <div class="hospital-info-section">
                    <div class="hospital-header">
                        <h3>병원 정보</h3>
                        <div class="search-controls">
                            <input type="text" id="hospitalSearch" placeholder="병원명으로 검색..." class="search-input">
                            <button id="searchHospital" class="btn btn-primary">검색</button>
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
                                            <button class="btn btn-primary btn-sm" onclick="selectHospital(${hospital.hospitalId})">
                                                선택
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
                    <div class="modal-header">
                        <h3 id="modalHospitalName">병원 정보</h3>
                        <span class="close" onclick="closeModal()">&times;</span>
                    </div>
                    <div class="modal-body">
                        <div id="hospitalDetailInfo">
                            <!-- 동적으로 로드됨 -->
                        </div>
                        <div id="departmentList">
                            <!-- 진료과 목록이 동적으로 로드됨 -->
                        </div>
                        <div id="doctorList">
                            <!-- 의사 목록이 동적으로 로드됨 -->
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button id="reservationBtn" class="btn btn-primary" onclick="goToReservation()" style="display: none;">
                            예약하기
                        </button>
                        <button class="btn btn-secondary" onclick="closeModal()">닫기</button>
                    </div>
                </div>
            </div>
        </main>
        
        <!-- 푸터 -->
        <jsp:include page="/WEB-INF/views/module/footer.jsp" />
    </div>
    
    <script src="/script/common.js"></script>
    <script src="/script/main.js"></script>
</body>
</html>
