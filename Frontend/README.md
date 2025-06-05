## 프론트엔드 실행 가이드

이 디렉터리는 Android Studio 프로젝트입니다. 에뮬레이터나 실제 기기에서 앱을 실행하여 백엔드와 통신합니다.

### 필수 환경

- Android Studio Hedgehog 이상
- Android SDK 35

### 실행 방법

1. Android Studio에서 `Frontend` 폴더를 열어 프로젝트를 로드합니다.
2. 코드 내에 있는 백엔드 API 주소를 실제 서버 주소로 수정합니다. 수정이 필요한 주요 파일은 다음과 같습니다.

   - `app/src/main/java/com/example/opensource_team6/register/SignupStep2Activity.java`
   - `app/src/main/java/com/example/opensource_team6/today/TodayFragment.java`

   로컬 백엔드에 접속할 경우 `http://10.0.2.2:8080`과 같이 작성하면 됩니다.
3. 에뮬레이터 또는 연결된 기기에서 **Run**을 눌러 앱을 빌드하고 실행합니다.
