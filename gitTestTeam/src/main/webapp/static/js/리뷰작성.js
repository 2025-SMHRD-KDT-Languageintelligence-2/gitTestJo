const stars = document.querySelectorAll('.star');
const emoji = document.getElementById('emoji');

// ⭐ 별점에 따라 바뀔 이모지들
const emojis = ['😡', '😐', '🙂', '😄', '🤩'];

stars.forEach((star, idx) => {
  star.addEventListener('click', () => {
    // 별 색상 채우기
    stars.forEach((s, i) => {
      s.classList.toggle('selected', i <= idx);
    });

    // 이모지 변경
    emoji.textContent = emojis[idx];
  });
});

