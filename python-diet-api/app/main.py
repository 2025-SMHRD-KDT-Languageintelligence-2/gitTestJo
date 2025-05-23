from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from .model import SurveyRequest
import pandas as pd
import numpy as np
import random
import re
from datetime import datetime, timedelta
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import mysql.connector
from ast import literal_eval

app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 정적 파일(css 등)
app.mount("/static", StaticFiles(directory="static"), name="static")

# 데이터 로딩
df = pd.read_csv("app/fooddb.csv")
df["F_TAGS"] = df["F_TAGS"].apply(lambda x: literal_eval(x) if pd.notnull(x) else [])
df["tag_str"] = df["F_TAGS"].apply(lambda tags: " ".join(tags))

# 카테고리별 분리
rice_df = df[df['F_TYPE'] == '밥류']
soup_df = df[df['F_TYPE'] == '국류']
side_df = df[df['F_TYPE'] == '반찬']

# 추천 API
@app.post("/recommend")
def recommend_meals(survey: SurveyRequest):
    user_tags = survey.userTags
    preferred_rice = survey.preferredRiceTypes
    daily_cal = survey.recomCal
    morning_ratio = survey.calRatioMorning if survey.calRatioMorning is not None else 30
    lunch_ratio = survey.calRatioLunch if survey.calRatioLunch is not None else 40
    dinner_ratio = survey.calRatioEvening if survey.calRatioEvening is not None else 30

    user_input = " ".join(user_tags)

    # 국 + 반찬 조합 만들기
    combos = []
    for _, soup in soup_df.iterrows():
        for _, side in side_df.iterrows():
            combined_tags = soup["tag_str"] + " " + side["tag_str"]
            combos.append({
                "soup": soup["F_NAME"],
                "side": side["F_NAME"],
                "soup_cal": soup["F_ENERGY"],
                "side_cal": side["F_ENERGY"],
                "tags": combined_tags
            })

    combo_df = pd.DataFrame(combos)

    # TF-IDF + 유사도 계산
    vectorizer = TfidfVectorizer()
    tfidf_matrix = vectorizer.fit_transform(combo_df["tags"])
    user_vec = vectorizer.transform([user_input])
    combo_df["similarity"] = cosine_similarity(user_vec, tfidf_matrix).flatten()

    # 유사도 필터링
    filtered = combo_df[combo_df["similarity"] >= 0.4].copy()
    filtered = filtered.sort_values(by="similarity", ascending=False).reset_index(drop=True)

    if len(filtered) < 21:
        return JSONResponse(content={"error": "유사도 0.4 이상 조합이 부족합니다."}, status_code=400)

    # 식사별 칼로리 타겟
    target_cals = {
        "morning": daily_cal * morning_ratio / 100,
        "lunch": daily_cal * lunch_ratio / 100,
        "dinner": daily_cal * dinner_ratio / 100,
    }

    result = []
    prev_soup = None
    prev_side = None
    idx = 0
    random.seed(42)
    selected_meals = random.sample(filtered.to_dict(orient="records"), min(len(filtered), 300))

    for day in range(1, 8):
        for time in ["morning", "lunch", "dinner"]:
            target = target_cals[time]

            if target <= 0:
                print(f"⏭ {day}일차 {time} 식사는 생략됨 (칼로리 비율 0%)")
                continue

            found = False
            for _ in range(len(selected_meals)):
                if idx >= len(selected_meals):
                    break
                meal = selected_meals[idx]
                idx += 1

                if meal["soup"] == prev_soup or meal["side"] == prev_side:
                    continue

                rice_name = random.choice(preferred_rice)
                rice_info = rice_df[rice_df["F_NAME"] == rice_name].iloc[0]
                rice_cal = rice_info["F_ENERGY"]
                total_cal = rice_cal + meal["soup_cal"] + meal["side_cal"]

                if total_cal > target + 150:
                    continue

                result.append({
                    "day": day,
                    "time": time,
                    "rice": rice_name,
                    "soup": meal["soup"],
                    "side": meal["side"],
                    "totalCal": round(total_cal, 2),
                    "similarity": round(meal["similarity"], 4)
                })

                prev_soup = meal["soup"]
                prev_side = meal["side"]
                found = True
                break

            if not found:
                print(f"⚠️ {day}일차 {time} 식사 추천 실패 (조건에 맞는 조합 없음)")

    return result
