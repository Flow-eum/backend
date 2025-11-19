package com.flow.eum_backend.api;

import com.flow.eum_backend.infra.DbHealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final DbHealthService dbHealthService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    } // 어플리케이션이 살아있는지만 확인함.

    @GetMapping("/db")
    public ResponseEntity<String> dbHealth() {
        try {
            boolean up = dbHealthService.isDatabaseUp();
            if (up) {
                return ResponseEntity.ok("DB ok (select 1 성공)");
            } else {
                return ResponseEntity.status(500).body("DB ERROR (unexpected result)");
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("DB ERROR (exception: " + e.getMessage() + ")");
        }
    } // supabase db에 "select 1"을 날리고 응답이 오는지 안오는지 확인함.


}
