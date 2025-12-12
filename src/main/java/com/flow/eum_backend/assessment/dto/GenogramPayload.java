package com.flow.eum_backend.assessment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenogramPayload {

    @Builder.Default
    private List<Person> persons = new ArrayList<>();

    @Builder.Default
    private List<Couple> couples = new ArrayList<>();

    @JsonProperty("parent_child")
    @Builder.Default
    private List<ParentChild> parentChild = new ArrayList<>();

    @Builder.Default
    private List<Interaction> interactions = new ArrayList<>();

    @JsonProperty("cohabitation_groups")
    @Builder.Default
    private List<CohabitationGroup> cohabitationGroups = new ArrayList<>();

    @JsonProperty("external_systems")
    @Builder.Default
    private List<ExternalSystem> externalSystems = new ArrayList<>();

    @JsonProperty("external_links")
    @Builder.Default
    private List<ExternalLink> externalLinks = new ArrayList<>();

    @JsonProperty("chart_year")
    private Integer chartYear;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Person {
        private String id;                 // 필수
        private String name;               // 기본 ""
        private String gender;             // "M"|"F"|"O" (필수)
        @JsonProperty("birth_year")
        private Integer birthYear;
        @JsonProperty("death_year")
        private Integer deathYear;
        @JsonProperty("is_client")
        @Builder.Default
        private boolean isClient = false;
        @JsonProperty("show_age_inside")
        @Builder.Default
        private boolean showAgeInside = false;
        private String residence;
        private String income;
        @JsonProperty("sexual_orientation")
        private String sexualOrientation;  // hetero | gay | ...
        @JsonProperty("is_transgender")
        @Builder.Default
        private boolean isTransgender = false;
        @JsonProperty("original_gender")
        private String originalGender;
        @JsonProperty("multicultural_waves")
        @Builder.Default
        private Integer multiculturalWaves = 0;
        @JsonProperty("fill_pattern")
        @Builder.Default
        private String fillPattern = "none";
        @Builder.Default
        private boolean smoker = false;
        @Builder.Default
        private boolean obese = false;
        @JsonProperty("language_issue")
        @Builder.Default
        private boolean languageIssue = false;
        @JsonProperty("is_pet")
        @Builder.Default
        private boolean isPet = false;
        private String notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Couple {
        private String id;
        @JsonProperty("person1_id")
        private String person1Id;
        @JsonProperty("person2_id")
        private String person2Id;
        @Builder.Default
        private String status = "married";  // married | cohabiting | ...
        @JsonProperty("married_year")
        private Integer marriedYear;
        @JsonProperty("separated_or_divorced_year")
        private Integer separatedOrDivorcedYear;
        @JsonProperty("is_secret")
        @Builder.Default
        private boolean isSecret = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParentChild {
        private String id;
        @JsonProperty("parents_couple_id")
        private String parentsCoupleId;
        @JsonProperty("child_id")
        private String childId;            // null 이면 임신/유산류
        @JsonProperty("relation_type")
        @Builder.Default
        private String relationType = "biological";
        @JsonProperty("twin_group_id")
        private String twinGroupId;
        @Builder.Default
        private boolean identical = false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Interaction {
        private String id1;
        private String id2;
        private String type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CohabitationGroup {
        private String id;
        private String label;
        @JsonProperty("member_ids")
        @Builder.Default
        private List<String> memberIds = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExternalSystem {
        private String id;
        private String label;
        private String kind;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExternalLink {
        @JsonProperty("person_id")
        private String personId;
        @JsonProperty("external_system_id")
        private String externalSystemId;
    }
}
