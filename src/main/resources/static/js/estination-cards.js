document.addEventListener('DOMContentLoaded', function() {
    // Функция проверки видимости элемента в окне просмотра
    function isElementInViewport(el) {
        const rect = el.getBoundingClientRect();
        return (
            rect.top <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.bottom >= 0
        );
    }

    // Получаем все карточки направлений
    const destinationCards = document.querySelectorAll('.destination-card');

    // Функция для добавления анимации
    function animateOnScroll() {
        destinationCards.forEach((card, index) => {
            if (isElementInViewport(card)) {
                // Добавляем задержку для каждой карточки для эффекта каскада
                setTimeout(() => {
                    card.classList.add('animate-in');
                }, 100 * index);
            }
        });
    }

    // Запускаем анимацию при прокрутке
    window.addEventListener('scroll', animateOnScroll);
    // Также проверяем при загрузке страницы
    animateOnScroll();
});