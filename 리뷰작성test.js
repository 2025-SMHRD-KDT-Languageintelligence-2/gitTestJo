const stars = document.querySelectorAll('.star');
const emoji = document.getElementById('emoji');
const reviewInput = document.getElementById('review');
const submitBtn = document.querySelector('.submit-btn');

const emojis = ['😡','😕','😐','😊','🤩'];

// 별 클릭 시 선택 처리
stars.forEach((star, index) => {
  star.addEventListener('click', () => {
    stars.forEach(s => s.classList.remove('selected'));
    for (let i = 0; i <= index; i++) {
      stars[i].classList.add('selected');
    }
    emoji.textContent = emojis[index];
  });
});

// 등록 버튼 클릭 시 저장
submitBtn.addEventListener('click', () => {
  const selectedRating = document.querySelectorAll('.star.selected').length;
  const reviewText = reviewInput.value;

  localStorage.setItem('reviewRating', selectedRating.toString());
  localStorage.setItem('reviewText', reviewText);

  window.location.href = '리뷰기반test.html';
});
