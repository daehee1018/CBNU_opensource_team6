## 프론트엔드 실행 가이드

이 디렉터리는 Android Studio 프로젝트입니다. 에뮬레이터나 실제 기기에서 앱을 실행하여 백엔드와 통신합니다.

### 필수 환경

- Android Studio Hedgehog 이상
- Android SDK 35

### 실행 방법

1. Android Studio에서 `Frontend` 폴더를 열어 프로젝트를 로드합니다.
2. 백엔드 주소는 `ApiConfig` 클래스의 `BASE_URL` 값을 수정해 설정합니다.

   - `app/src/main/java/com/example/opensource_team6/network/ApiConfig.java`

   기본 값은 `http://10.0.2.2:8080`이며, 실제 배포 서버 주소로 변경할 수 있습니다.
3. 에뮬레이터 또는 연결된 기기에서 **Run**을 눌러 앱을 빌드하고 실행합니다.
