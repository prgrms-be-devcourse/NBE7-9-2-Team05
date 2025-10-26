# 🏋️ Together Motionit

> 혼자서는 포기하기 쉬운 홈트레이닝을
같은 영상으로 동료들과 함께 운동하며 도전하고,
진행 상황을 공유하고 서로 응원하며
운동 습관을 형성해 나가는 AI & 소셜 운동 챌린지 서비스
> 
---

## 📌 프로젝트 개요

* **프로젝트 이름:** Together Motionit
* **한줄 요약:**  AI와 소셜 기능을 결합한 홈트 챌린지 플랫폼
  
---
## 🧑‍💻 팀 구성

| 이름   | 역할       | GitHub |
| ---- | -------- | ------ |
| [정한영] | 팀장 / JPA Entity 초기 구현 / 유저 운동방 참여 탈퇴 / 운동방 미션영상 게시/ Youtube API 연동 | https://github.com/gksdud1109  |
| [김나현] | 좋아요 기능 구현  | https://github.com/BE9-KNH   |
| [김현수] | 댓글 CRUD /욕설 필터링 구현| https://github.com/lambsteak-dev   |
| [박민형] | 공통 ResultCode / 에러 응답코드 / 내정보페이지/ 로그인 인증| https://github.com/minibr   |
| [이민우] | 운동방 CRUD / Web Socket 처리 구현 / 프로젝트 발표  | https://github.com/LeeMinwoo115   |
| [이혜지] | 로그인/인증 | https://github.com/heygeeji   |


---

## 📝 유저 스토리
### 👤 고객(유저)

- **R-1 [운동방 개설]**
    
    유저는 운동방을 개설할 수 있다.
  
      -유튜브 운동 영상 첨부
  
      -참여 인원 제한
  
      -카테고리 설정(홈트, 요가 등)
  
      -제목, 설명
  
      -운동 기간 설정
  
- **R-2 [운동방 삭제]**
    
   유저는 자신이 개설한 운동방을 삭제할 수 있다.
  
- **R-3 [운동방 조회]**
    
    유저는 현재 개설된 모든 운동방을 조회할 수 있다.
  
- **R-4 [운동방 참여]**
    
    유저는 운동방 정원이 남아 있을 때 운동방에 참여할 수 있다.
    
- **R-5 [운동방 참가자 목록 조회]**
    
    방 참여자는 운동방 내 참가자 목록을 조회할 수 있다.
- **R-6 [유튜브 영상 첨부]**
    
    방 참여자는 일일미션(유튜브 영상)을 게시할 수 있다.
- **R-7 [일일 미션 완료]**
    
    방 참여자는 일일미션 완료 표시를 할 수 있다.
- **R-8 [미션 완료 여부 조회]**
    
    방 참여자는 다른 참여자들의 일일 미션 완료 여부를 확인할 수 있다.
  
- **R-9 [미션 완료 여부 조회]**
    
    방 참여자는 다른 참여자들의 일일 미션 완료 여부를 확인할 수 있다.
  
- **R-10 [운동방 탈퇴]**
- 
    방 참여자는 운동방을 탈퇴할 수 있다.
   
- **M-1 [댓글 조회]**
    
   방 참여자는 운동방에 적힌 댓글을 조회할 수 있다.
- **M-2 [댓글 작성]**
    
   방 참여자는 운동방에 댓글을 작성할 수 있다.
- **M-3 [댓글 수정/삭제]**
    
  방 참여자는 자신이 작성한 댓글을 수정, 삭제할 수 있다.
- **L-1 [댓글 좋아요]**
    
  방 참여자는 댓글에 좋아요를 할 수 있다.
- **U-1 [로그인/회원가입]**
    
  유저는 로그인/회원가입을 통해 서비스 이용 자격을 얻을 수 있다.
- **U-2 [내 정보 조회]**
    
  유저는 로그인 후 자신의 정보를 조회할 수 있다.
- **U-3 [정보 수정]**
- 
  유저는 자신의 정보를 수정할 수 있다.
  
      -프로필 이미지 업로드
  
      -닉네임, 비밀번호 변경
---

## 🧩 주요 기능

*  운동방 생성 및 관리
*  YouTube Data API 연동
*  방 참여 및 목록/검색 기능
*  챌린지 참여자 관리 로직
*  댓글 작성/조회/수정/삭제(욕설 필터링 포함)
*  댓글 좋아요 기능
*  JWT 인증 및 권한 분리, OAuth2 소셜 로그인, Access Token 재발급 로직
*  AI 응원 메시지 자동 생성(OpenAI API 연동)
*  WebSocket 실시간 기능

---

## 🛠 아키텍처 &기술 스택
<img width="1159" height="462" alt="image" src="https://github.com/user-attachments/assets/c9d133bb-8839-4410-9b7d-8430f7a250f4" />
<img width="1157" height="472" alt="image" src="https://github.com/user-attachments/assets/0ba51297-8099-44b3-a493-e5355a417efa" />

---

## 🔐 인증 / 보안

* 일반 로그인 / 소셜 로그인(KAKAO)
* Spring Security
* JWT 기반 인증 시스템
* OAuth2 소셜 로그인
Access Token 재발급 로직

---

## 🗄️ ERD

<img width="1656" height="1177" alt="image" src="https://github.com/user-attachments/assets/b46ebdbd-512b-4213-ac16-a1a6d0b99bd2" />

---

## 📘 API 명세

https://www.notion.so/API-28a9d0051b998056bccecd0cfd988b24?source=copy_link

---

## 🧭 브랜치 전략

| 브랜치         | 설명       |
| ----------- | -------- |
| `main`   | 개발 통합    |
| `feature/*` | 기능 단위 작업 |

### 커밋 컨벤션

`feat:` 기능 추가 · `fix:` 버그 수정 · `refactor:` 리팩토링 · `docs:` 문서 수정

### 코드 컨벤션
Naver Checkstyle rule
---

## 📚 참고 자료

* [Notion WBS 링크] : https://www.notion.so/WBS-2809d0051b99816db712e15c0a344bcf?source=copy_link
* [ERD Diagram 링크] : https://www.notion.so/ERD-2809d0051b99816f84b7f395afdb6508?source=copy_link

---

