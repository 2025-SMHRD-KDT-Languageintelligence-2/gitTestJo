// ⭐ 페이지 로딩 시 실행되는 함수
document.addEventListener('DOMContentLoaded', function () {
    // 로컬스토리지에서 값 불러오기
    const rating = localStorage.getItem('reviewRating') || '5'; // 기본값 5점
    const text = localStorage.getItem('reviewText') || '리뷰 내용이 없습니다.';
  
    // HTML에 값 출력
    document.getElementById('display-rating').textContent = rating;
    document.getElementById('display-text').textContent = text;
  });