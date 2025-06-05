## 백엔드 실행 가이드

이 디렉터리에는 Spring Boot 기반의 서버 코드가 들어 있습니다. 개발용 데이터베이스로 MySQL을 사용하며, 실행 전에 `application.yml` 파일의 설정을 확인하세요.

### 필수 환경

- JDK 17 이상
- MySQL 8.x

### 실행 방법

1. 프로젝트 루트에서 다음 명령어를 실행합니다.

   ```bash
   ./gradlew bootRun
   ```

2. 서버가 8080 포트에서 실행되며, 필요 시 `application.yml`에서 포트를 변경할 수 있습니다.

### 주요 API

- `POST /api/auth/signup` 회원가입
- `POST /api/auth/login` 로그인
- `GET /api/food/search?keyword=` 음식 검색
- `GET /api/food/{id}` 음식 상세 조회
- `GET /api/food/today` 오늘의 추천 식단
- `/oauth2/authorization/google` 구글 로그인 시작
   - 로그인 성공 시 `opensource-team6://oauth` 스킴으로 토큰을 전달합니다.
