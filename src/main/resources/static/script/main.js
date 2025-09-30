// 메인 화면 JavaScript
class MainPageHandler {
    constructor() {
        this.map = null;
        this.markers = [];
        this.currentLocation = null;
        this.selectedHospital = null;
        this.selectedDepartment = null;
        this.selectedDoctor = null;
        
        this.init();
    }
    
    init() {
        this.initMap();
        this.bindEvents();
        this.loadHospitals();
    }
    
    // 지도 초기화
    initMap() {
        // 기본 위치 (부천시)
        const defaultLat = 37.5044;
        const defaultLng = 126.7659;
        
        const mapContainer = document.getElementById('map');
        const mapOption = {
            center: new kakao.maps.LatLng(defaultLat, defaultLng),
            level: 8
        };
        
        this.map = new kakao.maps.Map(mapContainer, mapOption);
        
        // 지도 클릭 이벤트
        kakao.maps.event.addListener(this.map, 'click', (mouseEvent) => {
            const latlng = mouseEvent.latLng;
            this.searchNearbyHospitals(latlng.getLat(), latlng.getLng());
        });
    }
    
    // 이벤트 바인딩
    bindEvents() {
        // 내 위치 찾기
        document.getElementById('getCurrentLocation').addEventListener('click', () => {
            this.getCurrentLocation();
        });
        
        // 전체 병원 보기
        document.getElementById('showAllHospitals').addEventListener('click', () => {
            this.showAllHospitals();
        });
        
        // 병원 검색
        document.getElementById('searchHospital').addEventListener('click', () => {
            this.searchHospitals();
        });
        
        // 엔터키로 검색
        document.getElementById('hospitalSearch').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchHospitals();
            }
        });
        
        // 병원 카드 클릭
        document.addEventListener('click', (e) => {
            if (e.target.closest('.hospital-card')) {
                const card = e.target.closest('.hospital-card');
                const hospitalId = card.dataset.hospitalId;
                this.selectHospital(hospitalId);
            }
        });
    }
    
    // 현재 위치 가져오기
    getCurrentLocation() {
        if (!navigator.geolocation) {
            alert('이 브라우저에서는 위치 서비스를 지원하지 않습니다.');
            return;
        }
        
        const button = document.getElementById('getCurrentLocation');
        const originalText = button.textContent;
        button.textContent = '위치 찾는 중...';
        button.disabled = true;
        
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                
                this.currentLocation = { lat, lng };
                
                // 지도 중심을 현재 위치로 이동
                const moveLatLon = new kakao.maps.LatLng(lat, lng);
                this.map.setCenter(moveLatLon);
                this.map.setLevel(5);
                
                // 현재 위치 마커 표시
                this.addCurrentLocationMarker(lat, lng);
                
                // 주변 병원 검색
                this.searchNearbyHospitals(lat, lng);
                
                button.textContent = originalText;
                button.disabled = false;
            },
            (error) => {
                console.error('위치 정보를 가져올 수 없습니다:', error);
                alert('위치 정보를 가져올 수 없습니다. 다시 시도해주세요.');
                button.textContent = originalText;
                button.disabled = false;
            }
        );
    }
    
    // 현재 위치 마커 추가
    addCurrentLocationMarker(lat, lng) {
        // 기존 현재 위치 마커 제거
        this.markers.forEach(marker => {
            if (marker.isCurrentLocation) {
                marker.setMap(null);
            }
        });
        
        const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png';
        const imageSize = new kakao.maps.Size(24, 35);
        const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
        
        const marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(lat, lng),
            image: markerImage,
            title: '현재 위치'
        });
        
        marker.isCurrentLocation = true;
        marker.setMap(this.map);
        this.markers.push(marker);
    }
    
    // 모든 병원 표시
    showAllHospitals() {
        this.clearMarkers();
        this.loadHospitals();
    }
    
    // 병원 목록 로드
    async loadHospitals() {
        try {
            const response = await fetch('/api/hospitals');
            const data = await response.json();
            
            if (data.success) {
                this.displayHospitals(data.data);
                this.addHospitalMarkers(data.data);
            } else {
                console.error('병원 목록 로드 실패:', data.message);
            }
        } catch (error) {
            console.error('병원 목록 로드 중 오류:', error);
        }
    }
    
    // 병원 목록 표시
    displayHospitals(hospitals) {
        const hospitalList = document.getElementById('hospitalList');
        
        if (hospitals.length === 0) {
            hospitalList.innerHTML = '<div class="no-data"><p>등록된 병원이 없습니다.</p></div>';
            return;
        }
        
        hospitalList.innerHTML = hospitals.map(hospital => `
            <div class="hospital-card" data-hospital-id="${hospital.hospitalId}" 
                 data-lat="${hospital.lat}" data-lng="${hospital.lng}">
                <div class="hospital-info">
                    <h4 class="hospital-name">${hospital.hospitalName}</h4>
                    <p class="hospital-address">${hospital.address}</p>
                    <p class="hospital-phone">${hospital.phone || '전화번호 없음'}</p>
                </div>
                <div class="hospital-actions">
                    <button class="btn btn-primary btn-sm" onclick="selectHospital(${hospital.hospitalId})">
                        선택
                    </button>
                </div>
            </div>
        `).join('');
    }
    
    // 병원 마커 추가
    addHospitalMarkers(hospitals) {
        hospitals.forEach(hospital => {
            if (hospital.lat && hospital.lng) {
                const marker = new kakao.maps.Marker({
                    position: new kakao.maps.LatLng(hospital.lat, hospital.lng),
                    title: hospital.hospitalName
                });
                
                marker.hospitalId = hospital.hospitalId;
                marker.setMap(this.map);
                this.markers.push(marker);
                
                // 마커 클릭 이벤트
                kakao.maps.event.addListener(marker, 'click', () => {
                    this.selectHospital(hospital.hospitalId);
                });
            }
        });
    }
    
    // 마커 모두 제거
    clearMarkers() {
        this.markers.forEach(marker => {
            marker.setMap(null);
        });
        this.markers = [];
    }
    
    // 병원 검색
    async searchHospitals() {
        const searchTerm = document.getElementById('hospitalSearch').value.trim();
        
        if (!searchTerm) {
            this.loadHospitals();
            return;
        }
        
        try {
            const response = await fetch(`/api/hospitals/search?name=${encodeURIComponent(searchTerm)}`);
            const data = await response.json();
            
            if (data.success) {
                this.displayHospitals(data.data);
                this.clearMarkers();
                this.addHospitalMarkers(data.data);
            } else {
                console.error('병원 검색 실패:', data.message);
            }
        } catch (error) {
            console.error('병원 검색 중 오류:', error);
        }
    }
    
    // 주변 병원 검색
    async searchNearbyHospitals(lat, lng, radius = 5) {
        try {
            const response = await fetch(`/api/hospitals/nearby?lat=${lat}&lng=${lng}&radius=${radius}`);
            const data = await response.json();
            
            if (data.success) {
                this.displayHospitals(data.data);
                this.clearMarkers();
                this.addCurrentLocationMarker(lat, lng);
                this.addHospitalMarkers(data.data);
            } else {
                console.error('주변 병원 검색 실패:', data.message);
            }
        } catch (error) {
            console.error('주변 병원 검색 중 오류:', error);
        }
    }
    
    // 병원 선택
    async selectHospital(hospitalId) {
        try {
            // 병원 정보 조회
            const hospitalResponse = await fetch(`/api/hospitals/${hospitalId}`);
            const hospitalData = await hospitalResponse.json();
            
            if (!hospitalData.success) {
                throw new Error(hospitalData.message);
            }
            
            this.selectedHospital = hospitalData.data;
            
            // 진료과 목록 조회
            const deptResponse = await fetch(`/api/hospitals/${hospitalId}/departments`);
            const deptData = await deptResponse.json();
            
            if (!deptData.success) {
                throw new Error(deptData.message);
            }
            
            this.showHospitalDetailModal(this.selectedHospital, deptData.data);
            
        } catch (error) {
            console.error('병원 정보 조회 실패:', error);
            alert('병원 정보를 불러오는데 실패했습니다.');
        }
    }
    
    // 병원 상세 정보 모달 표시
    showHospitalDetailModal(hospital, departments) {
        const modal = document.getElementById('hospitalDetailModal');
        const modalTitle = document.getElementById('modalHospitalName');
        const hospitalInfo = document.getElementById('hospitalDetailInfo');
        const departmentList = document.getElementById('departmentList');
        const doctorList = document.getElementById('doctorList');
        const reservationBtn = document.getElementById('reservationBtn');
        
        // 모달 제목
        modalTitle.textContent = hospital.hospitalName;
        
        // 병원 정보
        hospitalInfo.innerHTML = `
            <div class="hospital-detail">
                <h4>병원 정보</h4>
                <p><strong>주소:</strong> ${hospital.address}</p>
                <p><strong>전화번호:</strong> ${hospital.phone || '전화번호 없음'}</p>
            </div>
        `;
        
        // 진료과 목록
        if (departments.length > 0) {
            departmentList.innerHTML = `
                <div class="department-list">
                    <h4>진료과</h4>
                    ${departments.map(dept => `
                        <div class="department-item" data-dept-id="${dept.deptId}" onclick="selectDepartment(${dept.deptId})">
                            ${dept.deptName}
                        </div>
                    `).join('')}
                </div>
            `;
        } else {
            departmentList.innerHTML = '<div class="no-data"><p>등록된 진료과가 없습니다.</p></div>';
        }
        
        // 의사 목록 초기화
        doctorList.innerHTML = '';
        reservationBtn.style.display = 'none';
        
        // 모달 표시
        modal.style.display = 'block';
    }
    
    // 진료과 선택
    async selectDepartment(deptId) {
        // 기존 선택 해제
        document.querySelectorAll('.department-item').forEach(item => {
            item.classList.remove('selected');
        });
        
        // 현재 선택 표시
        document.querySelector(`[data-dept-id="${deptId}"]`).classList.add('selected');
        
        this.selectedDepartment = deptId;
        
        try {
            // 의사 목록 조회
            const response = await fetch(`/api/hospitals/${this.selectedHospital.hospitalId}/departments/${deptId}/doctors`);
            const data = await response.json();
            
            if (data.success) {
                this.displayDoctors(data.data);
            } else {
                console.error('의사 목록 조회 실패:', data.message);
            }
        } catch (error) {
            console.error('의사 목록 조회 중 오류:', error);
        }
    }
    
    // 의사 목록 표시
    displayDoctors(doctors) {
        const doctorList = document.getElementById('doctorList');
        const reservationBtn = document.getElementById('reservationBtn');
        
        if (doctors.length > 0) {
            doctorList.innerHTML = `
                <div class="doctor-list">
                    <h4>의사 목록</h4>
                    ${doctors.map(doctor => `
                        <div class="doctor-item" data-doctor-id="${doctor.doctorId}" onclick="selectDoctor(${doctor.doctorId})">
                            <div class="doctor-name">${doctor.name}</div>
                            <div class="doctor-time">진료시간: ${doctor.availableTime}</div>
                        </div>
                    `).join('')}
                </div>
            `;
            reservationBtn.style.display = 'block';
        } else {
            doctorList.innerHTML = '<div class="no-data"><p>등록된 의사가 없습니다.</p></div>';
            reservationBtn.style.display = 'none';
        }
    }
    
    // 의사 선택
    selectDoctor(doctorId) {
        this.selectedDoctor = doctorId;
        
        // 기존 선택 해제
        document.querySelectorAll('.doctor-item').forEach(item => {
            item.classList.remove('selected');
        });
        
        // 현재 선택 표시
        document.querySelector(`[data-doctor-id="${doctorId}"]`).classList.add('selected');
    }
    
    // 예약 페이지로 이동
    goToReservation() {
        if (!this.selectedHospital || !this.selectedDepartment || !this.selectedDoctor) {
            alert('병원, 진료과, 의사를 모두 선택해주세요.');
            return;
        }
        
        // 로그인 체크
        const currentUser = getCurrentUser();
        if (!currentUser) {
            alert('예약을 위해서는 로그인이 필요합니다.');
            window.location.href = '/users/signin';
            return;
        }
        
        // 예약 페이지로 이동 (파라미터 전달)
        const params = new URLSearchParams({
            hospitalId: this.selectedHospital.hospitalId,
            deptId: this.selectedDepartment,
            doctorId: this.selectedDoctor
        });
        
        window.location.href = `/reservation?${params.toString()}`;
    }
}

// 모달 닫기
function closeModal() {
    document.getElementById('hospitalDetailModal').style.display = 'none';
}

// 전역 함수들
function selectHospital(hospitalId) {
    if (window.mainHandler) {
        window.mainHandler.selectHospital(hospitalId);
    }
}

function selectDepartment(deptId) {
    if (window.mainHandler) {
        window.mainHandler.selectDepartment(deptId);
    }
}

function selectDoctor(doctorId) {
    if (window.mainHandler) {
        window.mainHandler.selectDoctor(doctorId);
    }
}

function goToReservation() {
    if (window.mainHandler) {
        window.mainHandler.goToReservation();
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    window.mainHandler = new MainPageHandler();
});
