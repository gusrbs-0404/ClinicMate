-- ClinicMate 전체 데이터베이스 설정 스크립트

USE clinicmate;

-- 1. USER 테이블 생성
CREATE TABLE IF NOT EXISTS USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role ENUM('PATIENT', 'ADMIN') DEFAULT 'PATIENT',
    withdrawal_status ENUM('ACTIVE', 'WITHDRAWAL_REQUESTED', 'WITHDRAWN') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. HOSPITAL 테이블 생성
CREATE TABLE IF NOT EXISTS HOSPITAL (
    hospital_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    lat DOUBLE,
    lng DOUBLE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. DEPARTMENT 테이블 생성
CREATE TABLE IF NOT EXISTS DEPARTMENT (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id INT NOT NULL,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    
    FOREIGN KEY (hospital_id) REFERENCES HOSPITAL(hospital_id) ON DELETE CASCADE
);

-- 4. DOCTOR 테이블 생성
CREATE TABLE IF NOT EXISTS DOCTOR (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id INT NOT NULL,
    dept_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    available_time VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (hospital_id) REFERENCES HOSPITAL(hospital_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id) ON DELETE CASCADE
);

-- 5. RESERVATION 테이블 생성
CREATE TABLE IF NOT EXISTS RESERVATION (
    res_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    hospital_id INT NOT NULL,
    doctor_id INT NOT NULL,
    dept_id INT NOT NULL,
    res_date DATETIME NOT NULL,
    status ENUM('예약중', '완료', '취소') DEFAULT '예약중',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES HOSPITAL(hospital_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES DOCTOR(doctor_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES DEPARTMENT(dept_id) ON DELETE CASCADE
);

-- 6. PAYMENT 테이블 생성
CREATE TABLE IF NOT EXISTS PAYMENT (
    pay_id INT AUTO_INCREMENT PRIMARY KEY,
    res_id INT NOT NULL,
    amount INT NOT NULL,
    method ENUM('카드', '현금') NOT NULL,
    status ENUM('대기', '완료', '취소') DEFAULT '대기',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (res_id) REFERENCES RESERVATION(res_id) ON DELETE CASCADE
);

-- 7. NOTIFICATION 테이블 생성 (알림 정보)
CREATE TABLE IF NOT EXISTS NOTIFICATION (
    noti_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    res_id INT,
    pay_id INT,
    type ENUM('회원가입', '예약완료', '예약취소', '결제완료', '결제취소', '탈퇴신청', '탈퇴승인', '탈퇴취소', '관리자_예약취소', '관리자_결제취소', '관리자_탈퇴신청', '관리자_탈퇴취소') NOT NULL,
    recipient VARCHAR(100) NOT NULL,
    recipient_type ENUM('USER', 'ADMIN') DEFAULT 'USER',
    subject VARCHAR(200),
    content TEXT,
    status ENUM('성공', '실패') DEFAULT '성공',
    error_message TEXT,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE CASCADE,
    FOREIGN KEY (res_id) REFERENCES RESERVATION(res_id) ON DELETE CASCADE,
    FOREIGN KEY (pay_id) REFERENCES PAYMENT(pay_id) ON DELETE CASCADE
);