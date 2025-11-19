package com.flow.eum_backend.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbHealthService {

    private final JdbcTemplate jdbcTemplate; // 자동으로 Supabase에 붙도록 함.

    // DB에 "select 1" 쿼리를 날려보고 1을 응답받으면 OK라고 판단
    public boolean isDatabaseUp() {
        Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);

        return result != null && result == 1;
    }
}
