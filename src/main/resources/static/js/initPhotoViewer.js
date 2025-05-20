
    // Функция создания и управления модальным окном
    function initPhotoViewer() {
    // Находим элементы модального окна
    const modal = document.querySelector('.modal-overlay');
    const modalImage = modal.querySelector('.modal-image');
    const modalTitle = modal.querySelector('.modal-title');
    const modalClose = modal.querySelector('.modal-close');
    const modalPrev = modal.querySelector('.modal-prev');
    const modalNext = modal.querySelector('.modal-next');

    // Текущие данные
    let currentIndex = 0;
    const photos = [];

    // Собираем все фотографии со страницы
    document.querySelectorAll('.photo-card').forEach((card, index) => {
    const link = card.querySelector('.photo-link');
    const img = card.querySelector('img');
    const title = card.querySelector('h3').textContent;

    // Сохраняем данные о фото
    photos.push({
    src: img.src,
    title: title,
    originalHref: link.href
});

    // Меняем поведение при клике
    link.addEventListener('click', function(e) {
    e.preventDefault();
    openModal(index);
});
});

    // Функция открытия модального окна
    function openModal(index) {
    currentIndex = index;
    updateModalContent();
    modal.classList.add('active');
    document.body.style.overflow = 'hidden'; // Блокируем прокрутку страницы
}

    // Функция закрытия модального окна
    function closeModal() {
    modal.classList.remove('active');
    document.body.style.overflow = ''; // Возвращаем прокрутку страницы
}

    // Обновление содержимого модального окна
    function updateModalContent() {
    if (photos.length === 0) return;

    const photo = photos[currentIndex];

    // Анимация смены контента
    modalImage.style.opacity = 0;

    setTimeout(() => {
    modalImage.src = photo.src;
    modalImage.alt = photo.title;
    modalTitle.textContent = photo.title;
    modalImage.style.opacity = 1;
}, 200);

    // Показываем/скрываем стрелки навигации в зависимости от позиции
    modalPrev.style.display = currentIndex > 0 ? 'flex' : 'none';
    modalNext.style.display = currentIndex < photos.length - 1 ? 'flex' : 'none';
}

    // Переход к следующему фото
    function nextPhoto() {
    if (currentIndex < photos.length - 1) {
    currentIndex++;
    updateModalContent();
}
}

    // Переход к предыдущему фото
    function prevPhoto() {
    if (currentIndex > 0) {
    currentIndex--;
    updateModalContent();
}
}

    // Обработчики событий
    modalClose.addEventListener('click', closeModal);
    modalNext.addEventListener('click', nextPhoto);
    modalPrev.addEventListener('click', prevPhoto);

    // Закрытие по клику на оверлей
    modal.addEventListener('click', function(e) {
    if (e.target === modal) {
    closeModal();
}
});

    // Обработка нажатий клавиш
    document.addEventListener('keydown', function(e) {
    if (!modal.classList.contains('active')) return;

    if (e.key === 'Escape') {
    closeModal();
} else if (e.key === 'ArrowRight') {
    nextPhoto();
} else if (e.key === 'ArrowLeft') {
    prevPhoto();
}
});

    // Предотвращаем всплытие события click с изображения на overlay
    modalImage.addEventListener('click', function(e) {
    e.stopPropagation();
});

    // Переход на страницу фотографии по двойному клику
    modalImage.addEventListener('dblclick', function() {
    window.location.href = photos[currentIndex].originalHref;
});

    // Предзагрузка изображений для более плавного просмотра
    function preloadImages() {
    photos.forEach(photo => {
    const img = new Image();
    img.src = photo.src;
});
}

    // Запускаем предзагрузку изображений
    preloadImages();
}

    // Запускаем инициализацию просмотрщика после загрузки страницы
    document.addEventListener('DOMContentLoaded', initPhotoViewer);
