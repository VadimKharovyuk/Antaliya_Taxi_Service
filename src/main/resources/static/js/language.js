// // Интеграция с навигацией для переводов туров
// document.addEventListener('DOMContentLoaded', function() {
//     // Получаем все селекторы языка из навигации (может быть несколько на странице)
//     const languageSelectors = document.querySelectorAll('.language-selector');
//
//     // Получаем текущий ID тура и язык из Thymeleaf
//     const currentTourId = [[${tour.id}]];
//     const currentLang = '[[${currentLang}]]' || 'original';
//
//     console.log('Текущий тур ID:', currentTourId);
//     console.log('Текущий язык:', currentLang);
//
//     // Карта соответствия языков между селектором навигации и системой переводов
//     const langMap = {
//         'ru': 'ru',      // Русский
//         'tr': 'tr',      // Турецкий
//         'en': 'en',      // Английский
//         'original': 'ru' // Оригинальный (по умолчанию русский)
//     };
//
//     // Обратная карта для установки значения в селекторе
//     const reverseLangMap = {
//         'ru': 'ru',
//         'tr': 'tr',
//         'en': 'en',
//         'original': 'ru'
//     };
//
//     // Инициализация селекторов языка
//     languageSelectors.forEach(selector => {
//         // Устанавливаем текущий выбранный язык в селекторе
//         selector.value = reverseLangMap[currentLang] || 'ru';
//
//         console.log('Установлен язык в селекторе:', selector.value);
//
//         // Добавляем обработчик события изменения языка
//         selector.addEventListener('change', function(event) {
//             const selectedLang = this.value;
//
//             console.log('Выбран новый язык:', selectedLang);
//
//             // Показываем индикатор загрузки
//             showLoadingIndicator();
//
//             // Плавно скрываем текущий контент
//             fadeOutContent();
//
//             // Переводим тур на выбранный язык
//             translateTourToLanguage(selectedLang);
//         });
//     });
//
//     // Функция показа индикатора загрузки
//     function showLoadingIndicator() {
//         const loadingIndicator = document.getElementById('loadingIndicator');
//         const tourDetailContainer = document.getElementById('tourDetailContainer');
//
//         if (loadingIndicator) {
//             loadingIndicator.style.display = 'flex';
//             loadingIndicator.style.opacity = '0';
//
//             // Плавное появление индикатора
//             setTimeout(() => {
//                 loadingIndicator.style.transition = 'opacity 0.3s ease';
//                 loadingIndicator.style.opacity = '1';
//             }, 10);
//         }
//
//         if (tourDetailContainer) {
//             tourDetailContainer.style.transition = 'opacity 0.3s ease';
//             tourDetailContainer.style.opacity = '0.4';
//         }
//     }
//
//     // Функция плавного скрытия контента
//     function fadeOutContent() {
//         const tourContent = document.querySelector('.tour-content');
//         if (tourContent) {
//             tourContent.style.transition = 'opacity 0.2s ease, transform 0.2s ease';
//             tourContent.style.opacity = '0.7';
//             tourContent.style.transform = 'translateY(10px)';
//         }
//     }
//
//     // Основная функция перевода тура
//     function translateTourToLanguage(language) {
//         console.log('Переводим тур на язык:', language);
//
//         // Формируем URL для перехода
//         let newUrl = `/tours/${currentTourId}`;
//
//         // Добавляем параметр языка, если это не русский (оригинал)
//         if (language !== 'ru') {
//             newUrl += `?lang=${language}`;
//         }
//
//         console.log('Переходим на URL:', newUrl);
//
//         // Выполняем переход
//         window.location.href = newUrl;
//     }
//
//     // Дополнительные обработчики для кнопок перевода (если они есть на странице)
//     const translationButtons = document.querySelectorAll('.translation-buttons a');
//     translationButtons.forEach(button => {
//         button.addEventListener('click', function(e) {
//             console.log('Клик по кнопке перевода:', this.href);
//
//             // Показываем индикатор загрузки при клике на кнопки
//             showLoadingIndicator();
//
//             // Обновляем селекторы языка в навигации
//             const urlParams = new URL(this.href).searchParams;
//             const langParam = urlParams.get('lang') || 'ru';
//
//             languageSelectors.forEach(selector => {
//                 selector.value = langParam;
//             });
//         });
//     });
//
//     // Анимация появления контента при первой загрузке
//     initializePageAnimations();
// });
//
// // Функция инициализации анимаций страницы
// function initializePageAnimations() {
//     const tourDetail = document.querySelector('.tour-detail');
//     if (tourDetail) {
//         // Начальное состояние
//         tourDetail.style.opacity = '0';
//         tourDetail.style.transform = 'translateY(20px)';
//
//         // Анимированное появление
//         setTimeout(() => {
//             tourDetail.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
//             tourDetail.style.opacity = '1';
//             tourDetail.style.transform = 'translateY(0)';
//         }, 100);
//     }
//
//     // Анимация для карточек похожих туров
//     const relatedTourCards = document.querySelectorAll('.related-tour-grid .tour-card');
//     const observer = new IntersectionObserver((entries) => {
//         entries.forEach((entry, index) => {
//             if (entry.isIntersecting) {
//                 setTimeout(() => {
//                     entry.target.style.opacity = '1';
//                     entry.target.style.transform = 'translateY(0)';
//                 }, index * 100);
//             }
//         });
//     });
//
//     relatedTourCards.forEach(card => {
//         card.style.opacity = '0';
//         card.style.transform = 'translateY(30px)';
//         card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
//         observer.observe(card);
//     });
// }
//
// // Глобальные функции для программного управления (доступны из консоли или других скриптов)
// window.translateTour = function(language) {
//     const languageSelectors = document.querySelectorAll('.language-selector');
//     languageSelectors.forEach(selector => {
//         selector.value = language;
//         selector.dispatchEvent(new Event('change'));
//     });
// };
//
// // Функция для получения текущего языка
// window.getCurrentTourLanguage = function() {
//     const currentLang = '[[${currentLang}]]' || 'original';
//     return currentLang;
// };
//
// // Обработчик ошибок при переводе
// window.addEventListener('error', function(e) {
//     console.error('Ошибка при переводе тура:', e);
//
//     // Скрываем индикатор загрузки в случае ошибки
//     const loadingIndicator = document.getElementById('loadingIndicator');
//     if (loadingIndicator) {
//         loadingIndicator.style.display = 'none';
//     }
//
//     // Восстанавливаем видимость контента
//     const tourDetailContainer = document.getElementById('tourDetailContainer');
//     if (tourDetailContainer) {
//         tourDetailContainer.style.opacity = '1';
//     }
// });
//
// // Отладочная информация (можно убрать в продакшене)
// console.log('🌐 Система переводов туров инициализирована');
// console.log('📍 Поддерживаемые языки:', ['ru', 'tr', 'en']);
// console.log('🔧 Доступные функции:', ['translateTour()', 'getCurrentTourLanguage()']);