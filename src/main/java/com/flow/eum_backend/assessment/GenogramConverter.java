package com.flow.eum_backend.assessment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.assessment.dto.GenogramPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenogramConverter {

    private final ObjectMapper objectMapper;

    public GenogramPayload fromAssessmentStructured(AssessmentStructured assessment) {
        try {
            // form 컬럼 이름에 맞게 수정 (예: getFormJson(), getForm() 등)
            String formJson = assessment.getAssessmentJson();
            JsonNode root = objectMapper.readTree(formJson);

            GenogramPayload payload = new GenogramPayload();

            // 1) clientBasic → 본인(Person, is_client = true)
            JsonNode clientBasic = root.path("clientBasic");
            String clientId = "p_client";

            payload.getPersons().add(
                    GenogramPayload.Person.builder()
                            .id(clientId)
                            .name(null) // 이름 필드가 있으면 가져와도 됨
                            .gender(mapGender(clientBasic.path("gender").asText()))
                            .birthYear(parseYear(clientBasic.path("birthDate").asText()))
                            .isClient(true)
                            .build()
            );

            // 2) familyMembers → 부모/가족 Person + 관계
            JsonNode familyMembers = root.path("familyMembers");

            String fatherId = null;
            String motherId = null;

            if (familyMembers.isArray()) {
                for (JsonNode member : familyMembers) {
                    String memberId = "p_" + UUID.randomUUID().toString().substring(0, 8);
                    String relation = member.path("relation").asText();
                    String gender = mapGender(member.path("gender").asText());

                    payload.getPersons().add(
                            GenogramPayload.Person.builder()
                                    .id(memberId)
                                    .name(member.path("name").asText(null))
                                    .gender(gender)
                                    .birthYear(parseYear(member.path("birthDate").asText()))
                                    .notes(relation)
                                    .build()
                    );

                    if ("부".equals(relation) || "아버지".equals(relation)) {
                        fatherId = memberId;
                    } else if ("모".equals(relation) || "어머니".equals(relation)) {
                        motherId = memberId;
                    }
                }
            }

            // 3) parent_child – FastAPI 스펙상 부모는 Couple 이어야 하므로 커플 생성
            if (fatherId != null || motherId != null) {
                if (fatherId == null) {
                    fatherId = createDummyPerson(payload, "M");
                }
                if (motherId == null) {
                    motherId = createDummyPerson(payload, "F");
                }

                String coupleId = "c_parents";

                payload.getCouples().add(
                        GenogramPayload.Couple.builder()
                                .id(coupleId)
                                .person1Id(fatherId)
                                .person2Id(motherId)
                                .status("married")
                                .build()
                );

                payload.getParentChild().add(
                        GenogramPayload.ParentChild.builder()
                                .id("pc_" + UUID.randomUUID().toString().substring(0, 8))
                                .parentsCoupleId(coupleId)
                                .childId(clientId)
                                .build()
                );
            }

            // 나중에 interactions / cohabitation_groups / external_* 필요하면 여기서 채우면 됨

            return payload;

        } catch (Exception e) {
            throw new RuntimeException("사정기록 → 가계도 변환 중 오류", e);
        }
    }

    private Integer parseYear(String dateStr) {
        if (dateStr == null || dateStr.length() < 4) return null;
        try {
            return Integer.parseInt(dateStr.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String mapGender(String raw) {
        // form 에 "남/여" 이런 식으로 들어오면 변환
        if (raw == null) return "O";
        return switch (raw) {
            case "M", "남", "male" -> "M";
            case "F", "여", "female" -> "F";
            default -> "O";
        };
    }

    private String createDummyPerson(GenogramPayload payload, String gender) {
        String id = "p_unknown_" + gender;
        payload.getPersons().add(
                GenogramPayload.Person.builder()
                        .id(id)
                        .name("미상")
                        .gender(gender)
                        .build()
        );
        return id;
    }
}
