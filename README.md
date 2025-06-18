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

### 3. 음식 데이터베이스 초기화

`scripts/import_nutrition.py` 스크립트를 사용하면 안드로이드 앱에서 사용하던 `nutrition_data.db` 파일의 내용을
MySQL의 `food` 테이블로 옮길 수 있습니다.

```bash
pip install -r scripts/requirements.txt
python scripts/import_nutrition.py \
    --db-host <MYSQL_HOST> --db-user <USER> --db-password <PASSWORD>
```

환경 변수 `NUTRITION_DB` 를 지정하면 다른 위치의 SQLite 파일을 사용할 수 있습니다.

### 4. 구글 로그인 연동

구글 로그인 버튼을 누르면 브라우저가 열리고 OAuth2 인증을 거친 뒤
`opensource-team6://oauth` 주소로 리다이렉트됩니다. 앱은 이 주소를
통해 전달된 JWT 토큰을 자동 저장하여 로그인 과정을 완료합니다.

### 5. 백엔드와 프론트엔드 연동

앱에서 구글 로그인 버튼을 누르면 브라우저가 열리면서 백엔드의 Google OAuth2 로그인 페이지로 이동합니다. 로그인 후 토큰이 JSON 형태로 표시되며, 추후 앱에서 토큰을 저장하도록 개선할 수 있습니다.