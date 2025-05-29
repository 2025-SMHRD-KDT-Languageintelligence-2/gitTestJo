# 🥗 식단 자동 생성 & 추천 시스템

![GitHub repo size](https://img.shields.io/github/repo-size/2025-SMHRD-KDT-Languageintelligence-2/gitTestJo?color=blue)
![Last Commit](https://img.shields.io/github/last-commit/2025-SMHRD-KDT-Languageintelligence-2/gitTestJo?color=green)
![Contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg)

> **FastAPI + Spring Boot 기반 식단 자동 추천 시스템**  
> 사용자 선호 기반으로 식단을 자동 생성 및 추천해주는 AI 기반 프로젝트입니다.  
> 프론트와 백엔드를 분리하여 Python과 Java(Spring Boot)를 병행 개발하였습니다.

---

## 📌 프로젝트 개요

- **프로젝트명**: 식단 자동 추천 시스템
- **목표**: 사용자 입력 선호도 및 알레르기, 목표 등 건강 정보를 기반으로 식단을 자동 생성 및 추천
- **주요 기능**
  - 사용자 입력 UI (Spring + Thymeleaf)
  - FastAPI 기반 추천 API 서버
  - TF-IDF 기반 유사도 모델
  - MySQL 연동을 통한 데이터 저장 및 관리
  - CSS 적용된 웹 인터페이스

---

## ⚙️ 사용 기술 스택

| 분야 | 기술 |
|------|------|
| 🧠 **AI 모델** | TF-IDF 기반 유사도 분석 |
| 🐍 **추천 서버** | ![Python](https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white) ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white) |
| ☕ **웹 서버** | ![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) |
| 💽 **데이터베이스** | ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) |
| 🎨 **프론트엔드** | Thymeleaf, HTML, CSS |
| 🛠 **개발 도구** | VSCode, IntelliJ, GitHub |

---

## 👥 조원 명단

| 이름     | 역할              |
|----------|-------------------|
| 조민환   | 팀장, 산출문서 관리 |
| 최재웅   | 백엔드 설계 및 코드 작성, github관리 |
| 주재건   | 프론트엔드 설계 및 코드 작성 |
| 김건도   | DB 설계, 산출문서 관리 |

---

## 🗂 디렉토리 구조

gitTestJo/<br/>
├── python-diet-api/ # FastAPI 기반 추천 API<br/>
│ ├── app/<br/>
│ │ ├── database.py<br/>
│ │ ├── fooddb.csv<br/>
│ │ ├── main.py<br/>
│ │ ├── recommender.py<br/>
│ │ └── model.py<br/>
│ ├── static/css/<br/>
│ │ └── preference-form.css<br/>
│ └── requirements.txt<br/>
│<br/>
├── teamjo/ # Spring Boot 기반 프론트엔드 서버<br/>
│ └── src/<br/>
│ └── main/<br/>
│ ├── java/com/smhrd/teamjo/<br/>
│ │ ├── config, controller, service, etc<br/>
│ └── resources/<br/>
│ ├── static/<br/>
│ ├── templates/<br/>
│ └── application.properties<br/>
│<br/>
├── upload/ # 파일 업로드 디렉토리<br/>
└── README.md<br/>

---

## 🚀 실행 방법

### 1. FastAPI 백엔드 실행

```bash
cd python-diet-api
pip install -r requirements.txt
uvicorn app.main:app --reload
```

### 2. Spring 백엔드 실행

```bash
cd teamjo
./gradlew bootRun
```

---
