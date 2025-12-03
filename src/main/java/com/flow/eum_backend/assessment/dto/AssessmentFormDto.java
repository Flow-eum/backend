package com.flow.eum_backend.assessment.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentFormDto {

    // ========== 상단 메타 영역 ==========
    private ClientBasicDto clientBasic;          // 클라이언트 일반사항
    private List<MeetingDto> meeting;           // 만남 일시/방식/면접자
    private String benefitType;                 // 보호 구분 (맞춤형급여 등)
    private String familyType;                  // 가구 유형 (1인가구, 부모자녀 등)

    private DisabilityDto disability;           // 장애 여부
    private LongTermCareDto longTermCare;       // 장기요양 여부
    private HousingDto housing;                 // 주거 현황/소유

    private List<FamilyMemberDto> familyMembers; // 가족사항 테이블

    // 필요하면 기타 자유 필드를 위한 공간
    private Map<String, Object> extra;          // 확장용
}

/** 클라이언트 기본 정보 영역 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ClientBasicDto {
    private LocalDate birthDate;
    private Integer age;
    private String gender;
    private String address;
    private String phone;
}

/** 만남 일시/방식/면접자 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class MeetingDto {
    private Integer round;          // 1차, 2차 ...
    private LocalDate date;         // 일시
    private String method;          // 전화, 방문, 내방 등
    private String interviewer;     // 면접자
}

/** 장애 여부 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class DisabilityDto {
    private Boolean hasDisability;
    private String type;            // 유형
    private String grade;           // 등급
}

/** 장기요양 여부 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class LongTermCareDto {
    private Boolean hasLtc;
    private String grade;           // 등급이 있다면
}

/** 주거 현황/소유 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class HousingDto {
    private String type;            // 아파트, 주택, 원룸 등
    private String ownership;       // 자가, 전세, 월세 등
    private Boolean hasLoan;        // 대출 여부
}

/** 가족 구성원 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class FamilyMemberDto {
    private String relation;        // 관계 (본인, 모, 부 등)
    private String name;            // 이름
    private String gender;          // 성별
    private Integer age;            // 나이
    private LocalDate birthDate;    // 선택: 생년월일
    private String job;             // 직업
    private Boolean liveTogether;   // 동거여부
    private String note;            // 특이사항
}
