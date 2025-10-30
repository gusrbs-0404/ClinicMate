# 🏥 ClinicMate
> 병원 예약·결제·알림을 하나로 관리하는 통합 웹 서비스

## 🔗 목차
- [프로젝트 소개](#-프로젝트-소개)
- [기술 스택](#-기술-스택)
- [문서 관리](#-문서-관리)
- [주요 기능](#-주요-기능)
- [API 개요](#-api-개요)
- [DB 설계](#-db-설계)
- [프로젝트 후기](#-프로젝트-후기)

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

## 📁 문서 관리

- [요구사항 정의서](https://docs.google.com/spreadsheets/d/1kL7PHTHXrzrFsVtXJqbQyZNUQL9rdYtF/edit?usp=drive_link&ouid=116757251093669453414&rtpof=true&sd=true)
- [화면 설계서](https://drive.google.com/file/d/1IIRIpnzKCuaUoaKg-2w1muwyaGt3ZXpv/view?usp=drive_link)
- [테이블 정의서](https://docs.google.com/spreadsheets/d/18qbMQSDvZel3Lrd6NjtywcrZTsmCd8zT/edit?usp=drive_link&ouid=116757251093669453414&rtpof=true&sd=true)
- [인터페이스 정의서](https://docs.google.com/spreadsheets/d/1xDM_t8dhPnV4JfD_cZgvNtg1R8iucEVE/edit?usp=drive_link&ouid=116757251093669453414&rtpof=true&sd=true)

---

## ✅ 주요 기능

### 1) 회원/권한 관리
- 회원가입
![회원가입](https://github.com/user-attachments/assets/20817845-f1ed-4821-928c-d6045c11425c)

- 로그인, 마이페이지, 정보수정
![로그인_-마이페이지_-정보수정](https://github.com/user-attachments/assets/c0ddc98c-8f8d-4e18-9034-073a1e6771dc)

- 탈퇴 요청 → 관리자 승인 → 탈퇴 완료(2단계)
- 관리자/일반회원 권한 분리
![탈퇴신청](https://github.com/user-attachments/assets/4384f065-b44d-41b6-b78d-41efb0d46670)
![탈퇴승인](https://github.com/user-attachments/assets/161f58de-116d-4cef-ad19-9d99a964d6d7)



### 2) 예약/결제 플로우
- 메인(지도)에서 병원 선택 → 진료과 선택 → 의사 선택
- 의사 스케줄 확인(가용/예약됨 표시)
- 시간 선택 후 예약 생성(관리자/사용자)
- 결제 진행(카드/현금) → 상태: 대기 → 완료/취소
- 이메일 알림 발송(예약완료/결제완료/취소 시 사용자·관리자)
- 마이페이지에서 예약·결제 내역 확인 및 취소
![예약](https://github.com/user-attachments/assets/36f57f7f-966f-4f00-bd69-c0a9fc55a6f3)
![예약취소](https://github.com/user-attachments/assets/dac730e7-f944-4708-80cd-6defdbcbdca9)

   
### 3) 병원/진료과/의사 관리
- 병원 등록/수정/삭제, 전화번호 자동 포맷팅
![병원등록](https://github.com/user-attachments/assets/3d645434-7c1d-4aa2-9a5b-666fa06c7703)

- 진료과 등록/수정/삭제
![진료과등록](https://github.com/user-attachments/assets/1fca8b8e-f9e2-4999-977c-7efb8c043541)

- 의사 관리 및 스케줄 연계
![의사추가](https://github.com/user-attachments/assets/525e465f-2109-44f7-be8f-0a66e02db5ac)


### 4) 예약 관리
- 의사별 스케줄 조회, 가능한/예약된 시간 표시
- 관리자 대리 예약(환자 모달에서 선택)
![예약관리](https://github.com/user-attachments/assets/1afd9f83-75f1-4100-9bf6-dc477a545517)


### 5) 결제 관리
- 결제 완료/취소 처리
- 취소 시 사용자/관리자 각각에 이메일 알림
- 통계에서 “완료된 결제 금액만” 집계
![결제관리](https://github.com/user-attachments/assets/45239ba9-ce79-48f9-8301-70c4e52bfd5d)


### 6) 통계 관리
- 월별/일별 예약 현황, 진료과별 예약 건수
- 결제 요약(총액=완료 금액), 완료/대기/취소 건수
- “전체 병원/특정 병원” 필터링
![통계관리](https://github.com/user-attachments/assets/41c2391d-cc67-4279-bce1-bc3632ec8d8a)


### 7) 알림 관리
- 이메일 알림 12종(회원/관리자)
- 발송 성공/실패 로그 및 재발송
- 실패만 보기/삭제/상세 조회, 페이징
![알림관리](https://github.com/user-attachments/assets/cfaf873d-b18d-4594-ae81-91ccc6898702)


### 8) 이메일
- 유저 이메일 발송
- 회원가입, 예약완료, 예약취소, 결제완료, 결제취소, 탈퇴신청, 탈퇴추소
![유저 이메일](https://github.com/user-attachments/assets/aa0cb073-be8c-4a4b-8089-6bad5171d205)

- 관리자 이메일 발송
- 예약취소, 결제취소, 탈퇴신청, 탈퇴취소
![관리자 이메일](https://github.com/user-attachments/assets/532a217f-73a4-4ad5-a174-1201921e5ed4)

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

## 💭프로젝트 후기
### 프로젝트 후기

ClinicMate 프로젝트를 14일이라는 단기간 내에 성공적으로 완료했습니다. 이는 AI 도구를 설계부터 완성까지 적극적으로 활용한 결과입니다. 혼자 진행했다면 최소 한 달 이상 소요되었을 프로젝트를 절반의 시간으로 단축하며, AI 도구의 막대한 영향력과 생산성 향상 잠재력을 직접 체감할 수 있었습니다.

다음 개인 프로젝트는 7일 이내 완성을 목표로, 더욱 집중적인 개발을 계획하고 있습니다.
현재 AI 도구가 작성해 준 코드를 검토할 때, 단순히 '작동'하는 수준을 넘어 코드의 패턴, 잠재적 에러, 기능적 결함 등을 신속하게 파악하고 수정하는 능력은 아직 부족합니다. AI에게 '어떤 질문을 해야' 양질의 답변과 코드를 얻을 수 있는지에 대한 체계적인 접근법도 지속적으로 모색해야 합니다.

이러한 단기 프로젝트 반복을 통해 AI 도구 활용법을 체계화해 나갈 것입니다.
궁극적으로는 AI 도구를 더욱 효과적으로 활용하여 개발 생산성을 극대화하고, AI가 생성한 코드의 오류나 비효율적인 부분을 정확하게 캐치하고 수정할 수 있는 역량을 갖춘 개발자로 지속 성장하고자 합니다.
