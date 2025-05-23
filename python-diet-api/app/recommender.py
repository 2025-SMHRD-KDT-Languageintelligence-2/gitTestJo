import pandas as pd
import numpy as np
import random
import re
from sklearn.metrics.pairwise import cosine_similarity

# 데이터 로드 및 전처리 (최초 1회만)
df = pd.read_csv("app/fooddb.csv")

def clean_tags(tag_str):
    clean = re.sub(r"[\[\]'\" ]", "", str(tag_str))
    return [tag for tag in clean.split(',') if tag]

df['F_TAGS'] = df['F_TAGS'].apply(clean_tags)

# 음식 유형별 분리
rice_df = df[df['F_TYPE'] == '밥류']
soup_df = df[df['F_TYPE'] == '국류']
side_df = df[df['F_TYPE'] == '반찬']

# 벡터화 함수
def vectorize_tags(tag_list, all_tags):
    return np.array([1 if tag in tag_list else 0 for tag in all_tags])

def calculate_similarity(soup_tags, side_tags, user_vector, all_tags):
    combined_tags = list(set(soup_tags + side_tags))
    food_vector = vectorize_tags(combined_tags, all_tags)
    similarity = cosine_similarity([user_vector], [food_vector])[0][0]
    return similarity, combined_tags

# 메인 추천 함수
def recommend_meals(survey):
    user_tags = survey.userTags
    preferred_rice = survey.preferredRiceTypes
    disliked_tags = survey.dislikedTags
    daily_cal = survey.recomCal

    # 필터링
    def filter_disliked(df):
        return df[~df['F_TAGS'].apply(lambda tags: any(tag in tags for tag in disliked_tags))]

    filtered_soup = filter_disliked(soup_df)
    filtered_side = filter_disliked(side_df)

    all_tags = sorted(set(tag for tags in df['F_TAGS'] for tag in tags))
    user_vector = vectorize_tags(user_tags, all_tags)

    recommendations = []

    for _ in range(20):
        rice = rice_df[rice_df['F_NAME'].isin(preferred_rice)].sample(1).iloc[0]
        soup = filtered_soup.sample(1).iloc[0]
        side = filtered_side.sample(1).iloc[0]

        total_cal = rice['F_ENERGY'] + soup['F_ENERGY'] + side['F_ENERGY']
        if abs(total_cal - (daily_cal / 3)) <= 150:
            sim, combined = calculate_similarity(soup['F_TAGS'], side['F_TAGS'], user_vector, all_tags)
            recommendations.append({
                '밥': rice['F_NAME'],
                '국': soup['F_NAME'],
                '반찬': side['F_NAME'],
                '총칼로리': round(total_cal, 2),
                '유사도': round(sim, 4),
                '조합된태그': combined
            })

    sorted_recommendations = sorted(recommendations, key=lambda x: -x['유사도'])

    return sorted_recommendations
