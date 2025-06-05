## 📌 프로젝트 관리 보드

> 📋 [Notion 페이지 바로가기](https://www.notion.so/Team6-1f5aac2f706f8068ad8dfb5fe7ce981e)  
> 팀 작업 상황을 확인하고 팀 링크와 문서를 관리하는 노션 페이지입니다.

## ✅ 프로젝트 실행 매뉴얼

아래 단계에 따라 백엔드와 프론트엔드를 실행하고 서로 연동할 수 있습니다.

### 1. 백엔드(Spring Boot) 실행 방법

1. JDK 17과 MySQL이 설치되어 있어야 합니다.
2. `Backend/recommend-diet` 디렉터리로 이동합니다.
3. 필요한 경우 `src/main/resources/application.yml`의 데이터베이스 정보와 OAuth 설정을 수정합니다.
4. 다음 명령어로 서버를 실행합니다.

   ```bash
   ./gradlew bootRun
   ```

   기본 포트는 `8080`이며, 실행 후 `http://localhost:8080`에서 서버를 확인할 수 있습니다.

### 2. 프론트엔드(Android) 실행 방법

1. Android Studio에서 `Frontend` 디렉터리를 엽니다.
2. 백엔드 서버 주소는 `ApiConfig` 클래스에서 설정합니다.

   - `app/src/main/java/com/example/opensource_team6/network/ApiConfig.java`

   기본 값은 `http://10.0.2.2:8080`으로, 필요에 따라 실제 서버 주소로 변경합니다.
3. 에뮬레이터나 실제 기기에서 앱을 실행합니다.

### 3. 구글 로그인 연동

앱에서 구글 로그인 버튼을 누르면 브라우저가 열리면서 백엔드의 Google OAuth2 로그인 페이지로 이동합니다. 로그인 후 토큰이 JSON 형태로 표시되며, 추후 앱에서 토큰을 저장하도록 개선할 수 있습니다.

### 4. 백엔드와 프론트엔드 연동

백엔드 서버가 실행된 상태에서, 프론트엔드 코드에 설정한 URL을 통해 API 요청이 이루어집니다. 두 프로젝트가 동일한 네트워크 내에 있어야 하며, 개발 환경에서는 위의 예시처럼 `10.0.2.2` 주소를 사용하면 됩니다.
