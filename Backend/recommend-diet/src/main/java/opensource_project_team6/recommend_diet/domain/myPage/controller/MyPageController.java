package opensource_project_team6.recommend_diet.domain.myPage.controller;

import lombok.RequiredArgsConstructor;
import opensource_project_team6.recommend_diet.domain.myPage.dto.MyPageResponse;
import opensource_project_team6.recommend_diet.domain.myPage.service.MyPageService;
import opensource_project_team6.recommend_diet.global.util.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MyPageController {
    private final MyPageService myPageService;

    @Value("${profile.image.dir:uploads/profile/}")
    private String uploadDir;

    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> getMyPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(myPageService.getMyPage(userPrincipal.getId()));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<Void> uploadProfileImage(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestParam("image") MultipartFile file) throws IOException {
        // 현재 프로젝트 디렉토리를 기준으로 상대 경로 지정
        String basePath = System.getProperty("user.dir");
        String uploadPath = basePath + File.separator + "uploads" + File.separator + "profile";

        // 디렉토리 없으면 생성
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        // 파일 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File saveFile = new File(dir, fileName);
        file.transferTo(saveFile);

        // 서비스로 파일명 전달
        myPageService.uploadProfileImage(userPrincipal.getId(), fileName);
        return ResponseEntity.ok().build();
    }
}
