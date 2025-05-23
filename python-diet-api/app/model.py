# model.py
from pydantic import BaseModel
from typing import List

class SurveyRequest(BaseModel):
    preferredRiceTypes: List[str]
    userTags: List[str]
    dislikedTags: List[str]
    recomCal: float
    calRatioMorning: int
    calRatioLunch: int
    calRatioEvening: int