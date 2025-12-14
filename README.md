# EUM Backend

상담 사례 관리 시스템의 백엔드 애플리케이션입니다. Spring Boot 기반으로 구축되었으며, Supabase를 인증/데이터베이스/스토리지로 사용하고, FastAPI와 연동하여 AI 기능을 제공합니다.

## 📋 목차

- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [환경 변수](#환경-변수)
- [API 문서](#api-문서)
- [주요 엔드포인트](#주요-엔드포인트)

## 🛠 기술 스택

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security** - JWT 기반 인증
- **Spring Data JPA** - 데이터베이스 접근
- **PostgreSQL** - Supabase PostgreSQL 사용
- **WebFlux** - 비동기 HTTP 클라이언트 (FastAPI 통신)
- **SpringDoc OpenAPI** - Swagger UI
- **Lombok** - 보일러플레이트 코드 제거
- **JJWT** - JWT 토큰 처리

## ✨ 주요 기능

### 1. 인증 및 보안
- Supabase JWT 기반 인증
- Stateless 세션 관리
- 역할 기반 접근 제어

### 2. 사례 관리
- 사례 생성 및 관리
- 사례별 멤버 관리 (primary_counselor 등)
- 사례 코드 자동 생성

### 3. 상담 회차 관리
- 상담 회차 메타데이터 관리
- 오디오 파일 업로드 및 STT (음성-텍스트 변환)
- 세션별 AI 출력 저장/조회

### 4. 사정 및 평가
- 구조화된 사정기록 저장/조회
- 욕구사정 관리 (items, scales)
- 가계도(Genogram) 생성 및 렌더링
- 생태도(Ecomap) 생성 및 렌더링

### 5. 개입 계획
- 버전 관리가 가능한 개입 계획
- 상태 관리 (draft → active)
- 파일 메타데이터 관리

### 6. 문서 관리
- 사례별 문서 메타데이터 관리
- 문서 타입별 분류 및 필터링
- 버전 관리

### 7. 슈퍼비전
- 슈퍼비전 열람 요청/승인/거절
- 기간 제한이 있는 열람 권한 관리
- 슈퍼바이저 검색

### 8. AI 통합
- FastAPI와 연동
- STT (음성 인식)
- 가계도/생태도 자동 렌더링
- 유사 사례 검색 (k-NN)
- 개인정보 동기화

### 9. 감사 로그
- 사용자 행위 추적
- 리소스별 액션 로깅

## 📁 프로젝트 구조

```
src/main/java/com/flow/eum_backend/
├── ai/                    # AI 통합 (FastAPI 클라이언트)
│   ├── FastApiClient.java
│   ├── AiSttClient.java
│   ├── CasePersonalInfoService.java
│   └── SimilarCaseService.java
├── api/                   # 공통 API 컨트롤러
│   ├── HealthController.java
│   └── MeController.java
├── assessment/            # 사정 및 평가
│   ├── AssessmentController.java
│   ├── NeedAssessmentController.java
│   ├── GenogramController.java
│   └── EcomapController.java
├── audit/                 # 감사 로그
│   ├── AuditLog.java
│   └── AuditLogService.java
├── auth/                  # 인증 및 보안
│   ├── SupabaseJwtFilter.java
│   ├── SupabaseJwtService.java
│   └── CurrentUser.java
├── cases/                 # 사례 관리
│   ├── CaseController.java
│   └── CaseService.java
├── config/                # 설정
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── AiApiConfig.java
├── documents/             # 문서 관리
│   └── CaseDocumentController.java
├── infra/                 # 인프라 (Storage, DB Health)
│   ├── SupabaseStorageClient.java
│   └── DbHealthService.java
├── plans/                 # 개입 계획
│   └── IndividualPlanController.java
├── profile/               # 프로필 관리
│   ├── ProfileController.java
│   └── SupervisorSearchController.java
├── sessions/              # 상담 회차
│   ├── SessionRecordController.java
│   ├── SessionSttController.java
│   └── SessionAiOutputController.java
└── supervision/           # 슈퍼비전
    └── SupervisionController.java
```

## 🚀 시작하기

### 사전 요구사항

- Java 21 이상
- Gradle 7.x 이상
- PostgreSQL (Supabase 사용)
- FastAPI 서버 (AI 기능 사용 시)

### 설치 및 실행

1. **저장소 클론**
```bash
git clone <repository-url>
cd eum-backend
```

2. **환경 변수 설정**

`.env` 파일을 생성하고 필요한 환경 변수를 설정합니다. (자세한 내용은 [환경 변수](#환경-변수) 섹션 참조)

3. **애플리케이션 실행**

```bash
# Gradle Wrapper 사용
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/eum-backend-0.0.1-SNAPSHOT.jar
```

4. **애플리케이션 접속**

- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/health/ping

## 🔧 환경 변수

다음 환경 변수들을 설정해야 합니다:

### Supabase 설정
```bash
SUPABASE_DB_HOST=your-supabase-db-host
SUPABASE_DB_PORT=5432
SUPABASE_DB_NAME=postgres
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=your-password
SUPABASE_JWT_SECRET=your-jwt-secret
SUPABASE_BASE_URL=https://your-project.supabase.co
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key
SUPABASE_AUDIO_BUCKET=session-audio  # 선택사항 (기본값: session-audio)
SUPABASE_GENOGRAM_BUCKET=genogram    # 선택사항 (기본값: genogram)
```

### AI API 설정
```bash
AI_API_BASE_URL=http://localhost:8000  # 선택사항 (기본값: http://localhost:8000)
```

### 서버 설정
```bash
PORT=8080  # 선택사항 (기본값: 8080)
```

## 📚 API 문서

애플리케이션 실행 후 Swagger UI를 통해 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

대부분의 API는 JWT 인증이 필요합니다. Swagger UI에서 "Authorize" 버튼을 클릭하여 Bearer 토큰을 입력하세요.

## 🔌 주요 엔드포인트

### 인증 테스트
- `GET /api/me` - 현재 로그인한 사용자 ID 조회

### 사례 관리
- `POST /api/cases` - 새 사례 생성
- `GET /api/cases` - 내 사례 목록 조회
- `GET /api/cases/{caseId}` - 사례 상세 조회

### 상담 회차
- `POST /api/cases/{caseId}/sessions` - 새 상담 회차 생성
- `GET /api/cases/{caseId}/sessions` - 상담 회차 목록 조회
- `GET /api/cases/{caseId}/sessions/{sessionId}` - 상담 회차 상세 조회
- `POST /api/cases/{caseId}/sessions/{sessionId}/stt/transcribe` - STT (음성 인식)

### 사정 및 평가
- `POST /api/cases/{caseId}/assessments` - 사정기록 저장
- `GET /api/cases/{caseId}/assessments/latest` - 최신 사정기록 조회
- `POST /api/cases/{caseId}/need-assessments` - 욕구사정 생성
- `GET /api/cases/{caseId}/need-assessments/latest` - 최신 욕구사정 조회
- `POST /api/cases/{caseId}/genogram/render` - 가계도 생성
- `POST /api/cases/{caseId}/ecomap/render` - 생태도 생성

### 개입 계획
- `POST /api/cases/{caseId}/plans` - 새 개입 계획 생성
- `GET /api/cases/{caseId}/plans` - 개입 계획 목록 조회
- `GET /api/cases/{caseId}/plans/active` - 활성 개입 계획 조회

### 문서 관리
- `POST /api/cases/{caseId}/documents` - 문서 등록
- `GET /api/cases/{caseId}/documents` - 문서 목록 조회

### 슈퍼비전
- `POST /api/supervision/requests` - 슈퍼비전 요청
- `GET /api/supervision/requests/incoming` - 들어온 요청 목록
- `GET /api/supervision/requests/mine` - 내가 보낸 요청 목록
- `POST /api/supervision/requests/{requestId}/approve` - 요청 승인
- `POST /api/supervision/requests/{requestId}/reject` - 요청 거절
- `GET /api/supervision/supervisors` - 슈퍼바이저 검색

### 프로필
- `GET /api/me/profile` - 내 프로필 조회
- `PUT /api/me/profile` - 내 프로필 수정

### AI 기능
- `GET /api/cases/{caseId}/similar` - 유사 사례 검색
- `POST /api/cases/{caseId}/sessions/{sessionId}/ai-outputs` - AI 결과 저장
- `GET /api/cases/{caseId}/sessions/{sessionId}/ai-outputs` - AI 결과 조회

### 감사 로그
- `GET /api/audit/me` - 내 감사 로그 조회

### Health Check
- `GET /health/ping` - 애플리케이션 상태 확인
- `GET /health/db` - 데이터베이스 연결 상태 확인

## 🔐 권한 관리

시스템은 두 가지 방식의 접근 권한을 지원합니다:

1. **Case Member**: 사례의 멤버로 등록된 사용자
2. **Supervision**: 승인된 슈퍼비전 요청을 통해 접근 권한을 받은 사용자

대부분의 API는 이 두 가지 권한 중 하나를 만족해야 접근할 수 있습니다.

## 📝 주요 특징

### 파일 관리
- 실제 파일은 클라이언트에서 암호화 후 Supabase Storage에 업로드
- 백엔드는 파일 메타데이터만 관리 (S3 키, SHA256 해시, 파일 크기)

### 버전 관리
- 개입 계획: `version_no` 필드로 버전 관리
- 문서/세션: `version` 필드로 버전 추적

### AI 통합
- FastAPI와 WebClient를 통한 비동기 통신
- STT, 가계도/생태도 렌더링, 유사 사례 검색 등 다양한 AI 기능 제공

### 감사 추적
- 주요 액션(사례 생성, 조회 등)이 자동으로 감사 로그에 기록됨

## 🧪 테스트

```bash
./gradlew test
```

## 📄 라이선스

이 프로젝트의 라이선스 정보는 프로젝트 루트의 LICENSE 파일을 참조하세요.

## 🤝 기여

이슈나 개선 사항이 있으면 이슈를 생성하거나 Pull Request를 제출해주세요.

