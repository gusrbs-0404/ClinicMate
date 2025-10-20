# 🏥 ClinicMate
> 병원 예약·결제·알림을 하나로 관리하는 통합 웹 서비스

<p align="center">
  <img src="docs/cover.png" width="720" alt="ClinicMate">
</p>

## 🔗 목차
- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [화면 구성](#-화면-구성)
- [개발 환경](#-개발-환경)
- [실행 방법](#-실행-방법)
- [API 개요](#-api-개요)
- [DB 설계](#-db-설계)
- [프로젝트 구조](#-프로젝트-구조)
- [레퍼런스](#-레퍼런스)

---

## 🔥 프로젝트 소개
ClinicMate는 환자·관리자 모두를 위한 병원 운영 통합 플랫폼입니다.  
병원/진료과/의사 관리부터 예약·결제·통계·알림까지 한 곳에서 처리할 수 있도록 설계했습니다.

- 실사용을 고려한 예약 동선과 검증 로직
- JSP 기반 SSR + JS 동적 UI 의 균형
- 월/일/진료과/결제 통계 및 병원별 필터링
- 이메일 알림(회원가입/예약/결제/탈퇴 등) 발송 및 이력 관리

---

## 🧰 기술 스택

- Backend: Java Developmet kit(JDK) 17, Spring Boot, Spring MVC, Spring Data JPA, Lombok, Jakarta Mail
- DB: MySQL 8.0.33, JPA/Hibernate
- Frontend: JSP/JSTL, JavaScript(ES6), HTML/CSS, Chart.js, Bootstrap
- Infra/Build: Gradle, Embedded Tomcat
- Mail: Gmail SMTP(App Password)
- 기타: Pagination(Spring Data Page), RESTful API

---

## ✅ 주요 기능

### 1) 회원/권한 관리
- 회원가입/로그인, 마이페이지, 정보수정
- 탈퇴 요청 → 관리자 승인 → 탈퇴 완료(2단계)
- 관리자/일반회원 권한 분리

### 2) 병원/진료과/의사 관리
- 병원 등록/수정/삭제, 전화번호 자동 포맷팅
- 병원별 진료과 관리(중복 방지, 정확 매칭)
- 의사 관리 및 스케줄 연계

### 3) 예약 관리
- 의사별 스케줄 조회, 가능한/예약된 시간 표시
- 관리자 대리 예약(환자 모달에서 선택)
- 예약 취소 시 결제 연계 처리(이메일 발송 순서 보장)

### 4) 결제 관리
- 결제 완료/취소 처리
- 취소 시 사용자/관리자 각각에 이메일 알림
- 통계에서 “완료된 결제 금액만” 집계

### 5) 통계 관리
- 월별/일별 예약 현황, 진료과별 예약 건수
- 결제 요약(총액=완료 금액), 완료/대기/취소 건수
- “전체 병원/특정 병원” 필터링

### 6) 알림 관리
- 이메일 알림 12종(회원/관리자)
- 발송 성공/실패 로그 및 재발송
- 실패만 보기/삭제/상세 조회, 페이징

### 7) 페이징/UX
- 회원/병원/진료과(선택병원)/의사/예약/결제/알림 목록 10개씩 페이징
- 페이지 버튼 10개씩 그룹(1~10, 11~20 …)
- 오류 메시지 개선 및 폰번호 자동 하이픈

---

## 🖥 화면 구성
- 메인: `/main` → `main/main.jsp`
- 예약: `/reservation` → `reservation/reservation.jsp`
- 로그인: `/users/signin` → `user/signin.jsp`
- 회원가입: `/users/signup` → `user/signup.jsp`
- 마이페이지: `/users/me` → `user/mypage.jsp`
- 정보 수정: `/users/edit-profile` → `user/edit-profile.jsp`
- 탈퇴 요청: `/users/withdraw` → `user/withdraw.jsp`
- 관리자: `/admin` → `admin/admin.jsp`

---

## 📡 API 개요
- Admin
  - GET `/admin/users`, `/admin/hospitals`, `/admin/departments`, `/admin/doctors` … (모두 페이징)
  - GET `/admin/reservations`, `/admin/payments` (병원 필터링 + 페이징)
  - GET `/admin/statistics/monthly|daily|department|payment` (hospitalId 옵션)
  - GET `/admin/notifications` (페이징), `/admin/notifications/all`, `/admin/notifications/{id}`
  - POST/PUT/DELETE: 병원/진료과/의사/유저/예약/결제 관리 일체

- Reservation
  - GET `/reservation` (뷰)
  - GET `/reservation/api/booked-times?doctorId=&date=`

- User
  - GET `/users/signup|signin|me|edit-profile|withdraw` (뷰)

상세 스펙은 `src/main/java/.../controller`에 정리되어 있습니다.

---

## 🗂 DB 설계
- 핵심 엔터티: USER, HOSPITAL, DEPARTMENT, DOCTOR, RESERVATION, PAYMENT, NOTIFICATION
- 예약↔결제: 1:1, 예약↔알림/결제↔알림: 1:N
- 통계 쿼리: 월/일/진료과/결제 집계 + 병원 필터
- 전화번호/중복 검증/정합성 보장
- 스키마: `database_setup.sql` 참조

---

## 📁 프로젝트 구조
src/
├─ main/java/com/example/ClinicMate
│  ├─ controller      # Admin, Users, Reservation 등
│  ├─ service         # User/Hospital/Dept/Doctor/Res/Pay/Mail/Notification
│  ├─ repository      # JpaRepository + 커스텀 쿼리
│  ├─ entity          # 엔터티 및 Enum
│  └─ filter          # AuthFilter 등
├─ main/webapp/WEB-INF/views
│  ├─ main/ admin/ reservation/ user/ module/
│  └─ *.jsp
├─ main/resources
│  ├─ static/{script,style,imges}
│  └─ application.properties
└─ database_setup.sql


