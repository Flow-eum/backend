package com.flow.eum_backend.profile;

import com.flow.eum_backend.profile.dto.SupervisorSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervision/supervisors")
@Tag(name = "Supervision", description = "슈퍼비전 요청 및 키 공유 API")
@SecurityRequirement(name = "bearerAuth")
public class SupervisorSearchController {

    private final SupervisorSearchService supervisorSearchService;

    public SupervisorSearchController(SupervisorSearchService supervisorSearchService) {
        this.supervisorSearchService = supervisorSearchService;
    }

    @GetMapping
    @Operation(
            summary = "슈퍼바이저 검색",
            description = "이름으로 슈퍼비전 가능한 상담사 목록을 검색합니다. " +
                    "프론트의 '상담사 이름 검색' 화면에서 사용."
    )
    public ResponseEntity<List<SupervisorSummaryDto>> search(
            @RequestParam(name = "query", required = false) String query
    ) {
        List<SupervisorSummaryDto> result = supervisorSearchService.searchSupervisors(query);
        return ResponseEntity.ok(result);
    }
}
