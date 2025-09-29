-- ClinicMate 데이터베이스 테이블 생성 스크립트

USE clinicmate;

-- USER 테이블 생성 (username + name 구조)
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

-- 샘플 데이터 삽입
INSERT INTO USER (username, password, name, phone, email, role) VALUES
('admin', 'admin123', '관리자', '010-0000-0000', 'admin@clinicmate.com', 'ADMIN'),
('testuser', 'test123', '테스트사용자', '010-1234-5678', 'test@example.com', 'PATIENT');

-- 테이블 생성 확인
SELECT 'USER 테이블 생성 완료' as message;
SELECT COUNT(*) as user_count FROM USER;
