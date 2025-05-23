# app/database.py
from sqlalchemy import create_engine, Column, Integer, String, Float, Date, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import datetime

DB_URL = "mysql+pymysql://<username>:<password>@<host>:<port>/<dbname>?charset=utf8mb4"
# 예: DB_URL = "mysql+pymysql://campus_24K_LI2_p2_1:smhrd1.@project-db-campus.smhrd.com:3307/캠퍼스DB이름"

engine = create_engine(DB_URL, echo=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class RecommendedMeal(Base):
    __tablename__ = "RECOMMENDED_MEAL"
    
    ID = Column(Integer, primary_key=True, autoincrement=True)
    USER_ID = Column(String(50))
    TIME = Column(String(10))
    RICE = Column(String(100))
    SOUP = Column(String(100))
    SIDE = Column(String(100))
    TOTAL_CALORIES = Column(Float)
    MEAL_DATE = Column(Date)
    CREATED_AT = Column(DateTime, default=datetime.datetime.utcnow)
    WEEKDAY = Column(String(3))
