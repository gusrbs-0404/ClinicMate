// 메인 화면 JavaScript
class MainPageHandler {
    constructor() {
        this.map = null;
        this.markers = [];
        this.currentLocation = null;
        this.selectedHospital = null;
        this.selectedDepartment = null;
        this.selectedDoctor = null;
        this.hospitals = [];
        this.isInitialized = false;
        
        this.init();
    }
    
    async init() {
        console.log('MainPageHandler 초기화 시작');
        await this.waitForKakaoMap();
        this.bindEvents();
        await this.loadHospitals();
        this.isInitialized = true;
        console.log('MainPageHandler 초기화 완료');
    }
    
    // 카카오맵 API 로드 대기
    async waitForKakaoMap() {
        return new Promise((resolve) => {
            const checkKakao = () => {
                if (typeof kakao !== 'undefined' && kakao.maps) {
                    console.log('카카오맵 API 로드 완료');
                    resolve();
                } else {
                    console.log('카카오맵 API 로드 대기 중...');
                    setTimeout(checkKakao, 100);
                }
            };
            checkKakao();
        });
    }
    
    // 지도 초기화
    initMap() {
        console.log('initMap() 함수 시작');
        
        // 기본 위치 (부천시)
        const defaultLat = 37.5044;
        const defaultLng = 126.7677;
        
        // 여러 방법으로 map 요소 찾기
        let mapContainer = document.getElementById('map');
        if (!mapContainer) {
            mapContainer = document.querySelector('#map');
        }
        if (!mapContainer) {
            mapContainer = document.querySelector('.map-container');
        }
        
        console.log('map 요소:', mapContainer);
        console.log('map 요소 스타일:', mapContainer ? mapContainer.style : 'null');
        
        if (!mapContainer) {
            console.error('map 요소를 찾을 수 없습니다!');
            console.log('전체 DOM 구조 확인:', document.body.innerHTML.substring(0, 500));
            // 2초 후 다시 시도
            setTimeout(() => this.initMap(), 2000);
            return;
        }
        
        // 요소가 화면에 보이는지 확인
        const rect = mapContainer.getBoundingClientRect();
        console.log('map 요소 크기:', rect);
        
        if (rect.width === 0 || rect.height === 0) {
            console.warn('map 요소의 크기가 0입니다. CSS 문제일 수 있습니다.');
        }
        
        const mapOption = {
            center: new kakao.maps.LatLng(defaultLat, defaultLng),
            level: 5
        };
        
        console.log('카카오맵 생성 중...');
        try {
            this.map = new kakao.maps.Map(mapContainer, mapOption);
            console.log('카카오맵 생성 완료:', this.map);
        } catch (error) {
            console.error('카카오맵 생성 실패:', error);
            return;
        }
        
        // 지도 확대/축소 컨트롤 생성
        const zoomControl = new kakao.maps.ZoomControl();
        this.map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
        
        // 현재 위치 가져오기 시도
        this.getCurrentLocation();
        console.log('initMap() 함수 완료');
    }
    
    // 이벤트 바인딩
    bindEvents() {
        document.getElementById('currentLocationBtn').addEventListener('click', () => this.getCurrentLocation());
        document.getElementById('showAllHospitalsBtn').addEventListener('click', () => this.showAllHospitals());
        document.getElementById('hospitalSearchBtn').addEventListener('click', () => this.searchHospitals());
        document.getElementById('hospitalSearchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.searchHospitals();
            }
        });
        
        // 모달 닫기 버튼
        const closeButton = document.querySelector('.modal .close-button');
        if (closeButton) {
            closeButton.addEventListener('click', () => this.closeHospitalDetailModal());
        }
        
        // 모달 외부 클릭 시 닫기
        const modal = document.getElementById('hospitalDetailModal');
        if (modal) {
            modal.addEventListener('click', (event) => {
                if (event.target === modal) {
                    this.closeHospitalDetailModal();
                }
            });
        }
        
        // 예약하기 버튼
        document.getElementById('reserveBtn').addEventListener('click', () => this.goToReservation());
    }
    
    // 현재 위치 가져오기
    getCurrentLocation() {
        const status = document.getElementById('currentLocationBtn');
        status.disabled = true;
        status.textContent = '위치 찾는 중...';
        
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    this.currentLocation = new kakao.maps.LatLng(lat, lng);
                    
                    if (this.map) {
                        this.map.setCenter(this.currentLocation);
                        this.addCurrentLocationMarker(lat, lng);
                        console.log('✅ 현재 위치로 지도 중심 이동 완료:', lat, lng);
                    }
                    
                    status.textContent = '현재 위치 찾기';
                    status.disabled = false;
                },
                (error) => {
                    console.error('Geolocation 오류:', error);
                    alert('현재 위치를 가져올 수 없습니다. 기본 위치로 표시합니다.');
                    status.textContent = '현재 위치 찾기';
                    status.disabled = false;
                },
                {
                    enableHighAccuracy: true,
                    timeout: 5000,
                    maximumAge: 0
                }
            );
        } else {
            alert('Geolocation을 지원하지 않는 브라우저입니다. 기본 위치로 표시합니다.');
            status.textContent = '현재 위치 찾기';
            status.disabled = false;
        }
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
        
        // 현재 위치 인포윈도우
        const infowindow = new kakao.maps.InfoWindow({
            content: '<div style="padding: 5px; font-size: 12px;"><strong>현재 위치</strong></div>'
        });
        
        infowindow.open(this.map, marker);
        marker.infowindow = infowindow;
    }
    
    // 마커 모두 제거
    clearMarkers() {
        this.markers.forEach(marker => {
            marker.setMap(null);
        });
        this.markers = [];
    }
    
    // 모든 병원 표시
    showAllHospitals() {
        console.log('🏥 모든 병원 표시');
        this.clearMarkers();
        this.loadHospitals();
        
        // 현재 위치가 있으면 현재 위치 중심으로 지도 이동
        if (this.currentLocation && this.map) {
            this.map.setCenter(this.currentLocation);
            this.addCurrentLocationMarker(this.currentLocation.getLat(), this.currentLocation.getLng());
        }
    }
    
    // 병원 목록 로드
    async loadHospitals() {
        try {
            const response = await fetch('/api/hospitals');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            this.hospitals = await response.json();
            console.log('🏥 로드된 병원 수:', this.hospitals.length);
            this.displayHospitals(this.hospitals);
            this.addHospitalMarkers(this.hospitals);
        } catch (error) {
            console.error('❌ 병원 목록 로드 실패:', error);
            alert('병원 목록을 불러오는 데 실패했습니다.');
        }
    }
    
    // 병원 목록 표시
    displayHospitals(hospitals) {
        const hospitalList = document.getElementById('hospitalList');
        hospitalList.innerHTML = hospitals.map(hospital => `
            <div class="hospital-card" data-hospital-id="${hospital.hospitalId}" 
                 data-lat="${hospital.lat}" data-lng="${hospital.lng}">
                <div class="hospital-details" onclick="moveToHospitalLocation('${hospital.hospitalId}', ${hospital.lat}, ${hospital.lng})">
                    <h3>${hospital.hospitalName}</h3>
                    <p>${hospital.address}</p>
                    <p>${hospital.phone || '전화번호 없음'}</p>
                </div>
                <button class="btn btn-primary select-hospital-btn" data-hospital-id="${hospital.hospitalId}">예약하기</button>
            </div>
        `).join('');
        
        // 동적으로 생성된 버튼에 이벤트 리스너 추가
        document.querySelectorAll('.select-hospital-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const hospitalId = e.target.dataset.hospitalId;
                this.goToReservationDirect(hospitalId);
            });
        });
    }
    
    // 병원 마커 추가
    addHospitalMarkers(hospitals) {
        hospitals.forEach(hospital => {
            if (hospital.lat && hospital.lng) {
                // 마커 이미지 설정
                const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png';
                const imageSize = new kakao.maps.Size(24, 35);
                const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
                
                const marker = new kakao.maps.Marker({
                    position: new kakao.maps.LatLng(hospital.lat, hospital.lng),
                    image: markerImage,
                    title: hospital.hospitalName
                });
                
                marker.hospitalId = hospital.hospitalId;
                marker.setMap(this.map);
                this.markers.push(marker);
                
                // 인포윈도우 생성
                const infowindow = new kakao.maps.InfoWindow({
                    content: `
                        <div style="padding: 5px; font-size: 12px;">
                            <strong>${hospital.hospitalName}</strong><br>
                            ${hospital.address}<br>
                            ${hospital.phone || '전화번호 없음'}
                        </div>
                    `
                });
                
                // 마커 클릭 이벤트
                kakao.maps.event.addListener(marker, 'click', () => {
                    // 기존 인포윈도우 닫기
                    this.markers.forEach(m => {
                        if (m.infowindow) {
                            m.infowindow.close();
                        }
                    });
                    
                    // 현재 마커의 인포윈도우 열기
                    infowindow.open(this.map, marker);
                    marker.infowindow = infowindow;
                    
                    this.selectHospital(hospital.hospitalId);
                });
            }
        });
    }
    
    // 병원 검색
    async searchHospitals() {
        const searchTerm = document.getElementById('hospitalSearchInput').value.trim();
        console.log('🔍 병원 검색:', searchTerm);
        
        if (!searchTerm) {
            this.loadHospitals(); // 검색어 없으면 전체 병원 로드
            return;
        }
        
        // 클라이언트 사이드에서 병원 이름 검색
        const filteredHospitals = this.hospitals.filter(hospital => 
            hospital.hospitalName.toLowerCase().includes(searchTerm.toLowerCase())
        );
        
        console.log(`검색 결과: ${filteredHospitals.length}개 병원 발견`);
        
        if (filteredHospitals.length > 0) {
            this.displayHospitals(filteredHospitals);
            this.clearMarkers();
            this.addHospitalMarkers(filteredHospitals);
            
            // 검색된 첫 번째 병원으로 지도 중심 이동
            if (this.map && filteredHospitals[0].lat && filteredHospitals[0].lng) {
                this.map.setCenter(new kakao.maps.LatLng(filteredHospitals[0].lat, filteredHospitals[0].lng));
            }
        } else {
            // 검색 결과가 없을 때
            this.displayHospitals([]);
            this.clearMarkers();
            alert(`"${searchTerm}"에 해당하는 병원을 찾을 수 없습니다.`);
        }
    }
    
    // 병원 선택 (모달 표시)
    async selectHospital(hospitalId) {
        console.log('🏥 병원 선택:', hospitalId);
        this.selectedHospital = this.hospitals.find(h => h.hospitalId == hospitalId);
        if (!this.selectedHospital) {
            console.error('❌ 선택된 병원을 찾을 수 없습니다. hospitalId:', hospitalId);
            console.log('현재 로드된 병원들:', this.hospitals.map(h => ({ id: h.hospitalId, name: h.hospitalName })));
            return;
        }
        
        // 모달에 정보 채우기
        document.getElementById('modalHospitalName').textContent = this.selectedHospital.hospitalName;
        document.getElementById('modalHospitalAddress').textContent = this.selectedHospital.address;
        document.getElementById('modalHospitalPhone').textContent = this.selectedHospital.phone || '전화번호 없음';
        
        // 진료과 로드
        const departmentListDiv = document.getElementById('modalDepartmentList');
        departmentListDiv.innerHTML = '<p>진료과 로드 중...</p>';
        try {
            const response = await fetch(`/api/hospitals/${hospitalId}/departments`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const departments = await response.json();
            console.log('🏥 로드된 진료과:', departments);
            
            if (departments && departments.length > 0) {
                departmentListDiv.innerHTML = departments.map(dept => 
                    `<div class="department-item">
                        <span class="dept-name">${dept.deptName}</span>
                        <button class="btn btn-primary btn-sm" onclick="selectDepartment('${dept.deptId}', '${dept.deptName}')">
                            예약하기
                        </button>
                    </div>`
                ).join('');
            } else {
                departmentListDiv.innerHTML = '<p>등록된 진료과가 없습니다.</p>';
            }
        } catch (error) {
            console.error('❌ 진료과 로드 실패:', error);
            departmentListDiv.innerHTML = '<p>진료과를 불러오는데 실패했습니다.</p>';
        }
        
        // 모달 열기
        document.getElementById('hospitalDetailModal').style.display = 'flex';
    }
    
    // 진료과 선택
    async selectDepartment(deptId, deptName) {
        console.log('🏥 진료과 선택:', deptId, deptName);
        
        // 선택된 진료과 정보 저장
        this.selectedDepartment = { deptId: deptId, deptName: deptName };
        
        // 의사 정보 로드
        const doctorListDiv = document.getElementById('modalDepartmentList');
        doctorListDiv.innerHTML = '<p>의사 정보 로드 중...</p>';
        
        try {
            const response = await fetch(`/api/hospitals/${this.selectedHospital.hospitalId}/departments/${deptId}/doctors`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const doctors = await response.json();
            console.log('👨‍⚕️ 로드된 의사:', doctors);
            
            if (doctors && doctors.length > 0) {
                doctorListDiv.innerHTML = `
                    <h4>${deptName} - 의사 목록</h4>
                    ${doctors.map(doctor => `
                        <div class="doctor-item">
                            <div class="doctor-info">
                                <h5>${doctor.name} 의사</h5>
                                <p class="available-time">진료시간: ${doctor.availableTime}</p>
                            </div>
                            <button class="btn btn-primary btn-sm" onclick="selectDoctor('${doctor.doctorId}', '${doctor.name}', '${deptName}')">
                                예약하기
                            </button>
                        </div>
                    `).join('')}
                `;
            } else {
                doctorListDiv.innerHTML = '<p>등록된 의사가 없습니다.</p>';
            }
        } catch (error) {
            console.error('❌ 의사 정보 로드 실패:', error);
            doctorListDiv.innerHTML = '<p>의사 정보를 불러오는데 실패했습니다.</p>';
        }
    }
    
    // 의사 선택
    selectDoctor(doctorId, doctorName, deptName) {
        console.log('👨‍⚕️ 의사 선택:', doctorId, doctorName, deptName);
        
        // 예약 페이지로 이동
        const deptId = this.selectedDepartment?.deptId;
        if (deptId) {
            window.location.href = `/reservation?hospitalId=${this.selectedHospital.hospitalId}&deptId=${deptId}&doctorId=${doctorId}`;
        } else {
            alert('진료과 정보를 찾을 수 없습니다.');
        }
    }
    
    // 병원 상세 모달 닫기
    closeHospitalDetailModal() {
        document.getElementById('hospitalDetailModal').style.display = 'none';
        this.selectedHospital = null;
    }
    
    // 예약하기 버튼 클릭 시
    goToReservation() {
        if (!this.selectedHospital) {
            alert('먼저 병원을 선택해주세요.');
            return;
        }
        
        // TODO: 로그인 여부 확인 후 예약 페이지로 이동
        // 현재는 바로 이동
        alert(`${this.selectedHospital.hospitalName} 예약 페이지로 이동합니다.`);
        // window.location.href = `/reservation?hospitalId=${this.selectedHospital.hospitalId}`;
    }
    
    // 직접 예약 페이지로 이동
    goToReservationDirect(hospitalId) {
        // 로그인 상태 확인
        if (!Utils.isLoggedIn()) {
            alert('예약을 위해서는 로그인이 필요합니다.');
            window.location.href = '/users/signin';
            return;
        }
        
        window.location.href = `/reservation?hospitalId=${hospitalId}`;
    }
    
    // 병원 위치로 지도 이동
    moveToHospitalLocation(hospitalId, lat, lng) {
        if (this.map && lat && lng) {
            const hospitalPosition = new kakao.maps.LatLng(lat, lng);
            this.map.setCenter(hospitalPosition);
            this.map.setLevel(3); // 확대
            
            // 해당 병원 마커에 인포윈도우 표시
            const hospital = this.hospitals.find(h => h.hospitalId == hospitalId);
            if (hospital) {
                // hospitalId로 마커 찾기
                const marker = this.markers.find(m => m.hospitalId == hospitalId);
                if (marker && marker.infowindow) {
                    marker.infowindow.open(this.map, marker);
                } else {
                    // 마커를 찾지 못한 경우 새로 생성
                    this.createHospitalMarker(hospital);
                }
            }
        }
    }
    
    // 병원 마커 생성 (개별)
    createHospitalMarker(hospital) {
        if (!hospital.lat || !hospital.lng) return;
        
        const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png';
        const imageSize = new kakao.maps.Size(24, 35);
        const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
        
        const marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(hospital.lat, hospital.lng),
            image: markerImage,
            title: hospital.hospitalName
        });
        
        marker.hospitalId = hospital.hospitalId;
        marker.setMap(this.map);
        this.markers.push(marker);
        
        // 인포윈도우 생성 및 표시
        const infowindow = new kakao.maps.InfoWindow({
            content: `
                <div style="padding: 5px; font-size: 12px;">
                    <strong>${hospital.hospitalName}</strong><br>
                    ${hospital.address}<br>
                    ${hospital.phone || '전화번호 없음'}
                </div>
            `
        });
        
        marker.infowindow = infowindow;
        infowindow.open(this.map, marker);
    }
}

// 전역 함수로 selectHospital 정의
function selectHospital(hospitalId) {
    if (window.mainHandler && window.mainHandler.isInitialized) {
        window.mainHandler.selectHospital(hospitalId);
    } else {
        console.warn('⚠️ MainPageHandler가 아직 초기화되지 않았습니다. 잠시 후 다시 시도해주세요.');
        // 1초 후 재시도
        setTimeout(() => {
            if (window.mainHandler && window.mainHandler.isInitialized) {
                window.mainHandler.selectHospital(hospitalId);
            } else {
                console.error('❌ MainPageHandler 초기화 실패');
            }
        }, 1000);
    }
}

// 전역 함수로 selectDepartment 정의
function selectDepartment(deptId, deptName) {
    if (window.mainHandler && window.mainHandler.isInitialized) {
        window.mainHandler.selectDepartment(deptId, deptName);
    } else {
        console.error('❌ MainPageHandler가 초기화되지 않았습니다.');
    }
}

// 전역 함수로 selectDoctor 정의
function selectDoctor(doctorId, doctorName, deptName) {
    if (window.mainHandler && window.mainHandler.isInitialized) {
        window.mainHandler.selectDoctor(doctorId, doctorName, deptName);
    } else {
        console.error('❌ MainPageHandler가 초기화되지 않았습니다.');
    }
}

// 전역 함수로 goToReservation 정의
function goToReservation(hospitalId) {
    // 로그인 상태 확인
    if (!Utils.isLoggedIn()) {
        alert('예약을 위해서는 로그인이 필요합니다.');
        window.location.href = '/users/signin';
        return;
    }
    
    if (window.mainHandler && window.mainHandler.isInitialized) {
        window.mainHandler.goToReservationDirect(hospitalId);
    } else {
        console.error('❌ MainPageHandler가 초기화되지 않았습니다.');
        // MainPageHandler가 초기화되지 않았어도 로그인 상태라면 직접 이동
        window.location.href = `/reservation?hospitalId=${hospitalId}`;
    }
}

// 전역 함수로 moveToHospitalLocation 정의
function moveToHospitalLocation(hospitalId, lat, lng) {
    if (window.mainHandler && window.mainHandler.isInitialized) {
        window.mainHandler.moveToHospitalLocation(hospitalId, lat, lng);
    } else {
        console.error('❌ MainPageHandler가 초기화되지 않았습니다.');
    }
}

// 작동하는 지도 초기화 함수 (JSP에서 분리된 코드)
function initSimpleMap() {
    // 카카오맵 API 로딩 대기
    function waitForKakaoMap() {
        return new Promise((resolve, reject) => {
            let attempts = 0;
            const maxAttempts = 100;
            
            const checkKakao = () => {
                attempts++;
                
                if (typeof kakao !== 'undefined' && kakao.maps) {
                    resolve();
                    return;
                }
                
                if (attempts >= maxAttempts) {
                    console.error('❌ 카카오맵 API 로딩 타임아웃');
                    reject(new Error('카카오맵 API 로딩 타임아웃'));
                    return;
                }
                
                setTimeout(checkKakao, 100);
            };
            checkKakao();
        });
    }
    
    // 현재 위치 가져오기
    function getCurrentPosition() {
        return new Promise((resolve, reject) => {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        resolve({ lat, lng });
                    },
                    (error) => {
                        console.warn('현재 위치를 가져올 수 없습니다. 기본 위치를 사용합니다.');
                        // 기본 위치 (부천시)
                        resolve({ lat: 37.5044, lng: 126.7677 });
                    }
                );
            } else {
                console.warn('Geolocation을 지원하지 않습니다. 기본 위치를 사용합니다.');
                resolve({ lat: 37.5044, lng: 126.7677 });
            }
        });
    }
    
    // 지도 초기화
    waitForKakaoMap().then(async () => {
        const mapContainer = document.getElementById('map');
        if (!mapContainer) {
            console.error('❌ 지도 컨테이너를 찾을 수 없습니다');
            return;
        }
        
        // 현재 위치 가져오기
        const position = await getCurrentPosition();
        
        const mapOption = {
            center: new kakao.maps.LatLng(position.lat, position.lng),
            level: 5
        };
        
        try {
            const map = new kakao.maps.Map(mapContainer, mapOption);
            
            // MainPageHandler에 지도 참조 설정
            if (window.mainHandler) {
                window.mainHandler.map = map;
                window.mainHandler.currentLocation = new kakao.maps.LatLng(position.lat, position.lng);
            }
            
            console.log('✅ ClinicMate 지도 시스템 로드 완료 (현재 위치 중심)');
        } catch (error) {
            console.error('❌ 지도 생성 실패:', error);
        }
    }).catch((error) => {
        console.error('❌ 카카오맵 로딩 실패:', error);
    });
}

// 즉시 실행 (가장 확실한 방법)
console.log('🗺️ ClinicMate 지도 시스템 초기화 시작');

// MainPageHandler 인스턴스 생성
window.mainHandler = new MainPageHandler();

// 1초 후 지도 초기화 (DOM이 완전히 준비될 때까지 대기)
setTimeout(() => {
    initSimpleMap();
}, 1000);

// 추가 안전장치 - 3초 후에도 지도가 없으면 재시도
setTimeout(() => {
    const mapContainer = document.getElementById('map');
    if (mapContainer && (!mapContainer.hasChildNodes() || mapContainer.children.length === 0)) {
        console.log('⚠️ 지도 초기화 재시도');
        initSimpleMap();
    }
}, 3000);
