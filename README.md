2025 멋쟁이사자처럼 13기 데모데이 화리보 백엔드 파트 리팩토링

<Backend Core>
Language: Java 21
Framework: Spring Boot 
Build Tool: Gradle

<Data & Database>
RDBMS: PostgreSQL
NoSQL/Cache: Redis (Docker)
Persistence: Spring Data JPA
Query Optimization: QueryDSL (Jakarta 지원 버전 5.0.0) && Fetch Join을 활용한 N+1 문제 해결 및 동적 쿼리 최적화

<Security & Auth>
Authentication: Spring Security
Social Login: OAuth2 Client (Naver)
Token: JWT (jjwt 0.12.5)

<AI & Cloud>
AI Engine: Spring AI (OpenAI API 연동) — 리뷰 요약 및 사진 검열
Cloud Storage: AWS S3 (Spring Cloud AWS 3.1.1)

<API & Documentation>
API Specs: Swagger / SpringDoc OpenAPI 2.4.0
Validation: Hibernate Validation
