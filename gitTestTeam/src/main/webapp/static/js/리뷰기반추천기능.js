// 리뷰 결과를 localStorage에서 불러오기
const rating = localStorage.getItem("reviewRating");
const text = localStorage.getItem("reviewText");

// 화면에 표시
document.getElementById("display-rating").textContent = rating ?? '5';
document.getElementById("display-text").textContent = text ?? '기본 리뷰 내용입니다.';
