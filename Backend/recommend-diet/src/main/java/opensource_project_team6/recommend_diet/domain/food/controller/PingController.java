package opensource_project_team6.recommend_diet.domain.food.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}