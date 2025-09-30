-- ClinicMate 전체 테이블 생성 스크립트

USE clinicmate;

-- USER 테이블 생성
CREATE TABLE IF NOT EXISTS USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('PATIENT', 'ADMIN') NOT NULL DEFAULT 'PATIENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- HOSPITAL 테이블 생성
CREATE TABLE IF NOT EXISTS HOSPITAL (
    hospital_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    lat INT NOT NULL,
    lng INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- DEPARTMENT 테이블 생성
CREATE TABLE IF NOT EXISTS DEPARTMENT (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id INT NOT NULL,
    dept_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (hospital_id) REFERENCES HOSPITAL(hospital_id) ON DELETE CASCADE
);

-- DOCTOR 테이블 생성
CREATE TABLE IF NOT EXISTS DOCTOR (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id INT NOT NULL,
    dept_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    available_time VARCHAR(255) NOT NULL,
    FOREIGN KEY (hospital_id) REFERENCES HOSPITAL(hospital_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id) ON DELETE CASCADE
);

-- 샘플 데이터 삽입
-- 사용자 데이터
INSERT INTO USER (username, password, name, phone, email, role) VALUES
('admin', 'admin123', '관리자', '010-0000-0000', 'admin@clinicmate.com', 'ADMIN'),
('testuser', 'test123', '테스트사용자', '010-1234-5678', 'test@example.com', 'PATIENT');

-- 병원 데이터
INSERT INTO HOSPITAL (hospital_name, address, phone, lat, lng) VALUES
('짱구 정형외과', '경기도 부천시 원미구 계남로 123', '032-123-4567', 37512345, 126782345),
('부천 종합병원', '경기도 부천시 원미구 중동로 456', '032-234-5678', 37513456, 126793456),
('서울 대학병원', '서울특별시 강남구 테헤란로 789', '02-345-6789', 37514567, 127004567);

-- 진료과 데이터
INSERT INTO DEPARTMENT (hospital_id, dept_name) VALUES
(1, '정형외과'),
(1, '내과'),
(2, '정형외과'),
(2, '내과'),
(2, '소아과'),
(3, '정형외과'),
(3, '내과'),
(3, '소아과'),
(3, '산부인과');

-- 의사 데이터
INSERT INTO DOCTOR (hospital_id, dept_id, name, available_time) VALUES
(1, 1, '짱구', '09:00-18:00'),
(1, 2, '철수', '09:00-17:00'),
(2, 3, '유리', '09:00-18:00'),
(2, 4, '맹구', '09:00-17:00'),
(2, 5, '훈이', '09:00-17:00'),
(3, 6, '김의사', '09:00-18:00'),
(3, 7, '이의사', '09:00-17:00'),
(3, 8, '박의사', '09:00-17:00'),
(3, 9, '최의사', '09:00-17:00');

-- 테이블 생성 확인
SELECT 'USER 테이블 생성 완료' as message;
SELECT COUNT(*) as user_count FROM USER;

SELECT 'HOSPITAL 테이블 생성 완료' as message;
SELECT COUNT(*) as hospital_count FROM HOSPITAL;

SELECT 'DEPARTMENT 테이블 생성 완료' as message;
SELECT COUNT(*) as department_count FROM DEPARTMENT;

SELECT 'DOCTOR 테이블 생성 완료' as message;
SELECT COUNT(*) as doctor_count FROM DOCTOR;
