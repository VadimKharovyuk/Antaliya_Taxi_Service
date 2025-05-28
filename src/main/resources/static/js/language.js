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


// Интеграция с навигацией для переводов маршрутов
document.addEventListener('DOMContentLoaded', function() {
    // Получаем все селекторы языка из навигации
    const languageSelectors = document.querySelectorAll('.language-selector');

    // Получаем текущий язык из Thymeleaf (устанавливается контроллером)
    const currentLang = /*[[${currentLang}]]*/ 'original';

    // Получаем текущие параметры URL для сохранения состояния фильтров и пагинации
    const urlParams = new URLSearchParams(window.location.search);

    console.log('Текущий язык маршрутов:', currentLang);
    console.log('Текущие параметры URL:', urlParams.toString());

    // Карта соответствия языков между селектором навигации и системой переводов
    const langMap = {
        'ru': 'ru',           // Русский
        'tr': 'tr',           // Турецкий
        'en': 'en',           // Английский
        'original': 'ru'      // Оригинальный (по умолчанию русский)
    };

    // Обратная карта для установки значения в селекторе
    const reverseLangMap = {
        'ru': 'ru',
        'tr': 'tr',
        'en': 'en',
        'original': 'ru'
    };

    // Инициализация селекторов языка
    languageSelectors.forEach(selector => {
        // Устанавливаем текущий выбранный язык в селекторе
        const mappedLang = reverseLangMap[currentLang] || 'ru';
        selector.value = mappedLang;

        console.log('Установлен язык в селекторе:', selector.value);

        // Добавляем обработчик события изменения языка
        selector.addEventListener('change', function(event) {
            const selectedLang = this.value;

            console.log('Выбран новый язык для маршрутов:', selectedLang);

            // Показываем индикатор загрузки
            showLoadingIndicator();

            // Плавно скрываем текущий контент
            fadeOutContent();

            // Переводим маршруты на выбранный язык
            translateRoutesToLanguage(selectedLang);
        });
    });

    // Функция показа индикатора загрузки
    function showLoadingIndicator() {
        // Создаем индикатор загрузки, если его нет
        let loadingIndicator = document.getElementById('routesLoadingIndicator');
        if (!loadingIndicator) {
            loadingIndicator = createLoadingIndicator();
        }

        const routesContainer = document.querySelector('.routes-grid');
        const pageContent = document.querySelector('.container');

        if (loadingIndicator) {
            loadingIndicator.style.display = 'flex';
            loadingIndicator.style.opacity = '0';

            // Плавное появление индикатора
            setTimeout(() => {
                loadingIndicator.style.transition = 'opacity 0.3s ease';
                loadingIndicator.style.opacity = '1';
            }, 10);
        }

        if (routesContainer) {
            routesContainer.style.transition = 'opacity 0.3s ease';
            routesContainer.style.opacity = '0.4';
        }

        if (pageContent) {
            pageContent.style.pointerEvents = 'none';
        }
    }

    // Создание индикатора загрузки
    function createLoadingIndicator() {
        const indicator = document.createElement('div');
        indicator.id = 'routesLoadingIndicator';
        indicator.className = 'loading-indicator';
        indicator.innerHTML = `
            <div class="loading-spinner"></div>
            <div class="loading-text">Переводим маршруты...</div>
        `;

        // Добавляем стили
        indicator.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(255, 255, 255, 0.95);
            padding: 20px 30px;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            z-index: 1000;
            display: none;
            flex-direction: column;
            align-items: center;
            gap: 15px;
        `;

        document.body.appendChild(indicator);
        return indicator;
    }

    // Функция плавного скрытия контента маршрутов
    function fadeOutContent() {
        const routeCards = document.querySelectorAll('.route-card');
        routeCards.forEach((card, index) => {
            setTimeout(() => {
                card.style.transition = 'opacity 0.2s ease, transform 0.2s ease';
                card.style.opacity = '0.7';
                card.style.transform = 'translateY(5px)';
            }, index * 20);
        });
    }

    // Основная функция перевода маршрутов
    function translateRoutesToLanguage(language) {
        console.log('Переводим маршруты на язык:', language);

        // Получаем текущие параметры URL (пагинация, сортировка, валюта)
        const currentParams = new URLSearchParams(window.location.search);

        // Удаляем старый параметр lang, если есть
        currentParams.delete('lang');

        // Добавляем новый параметр языка, если это не русский (оригинал)
        if (language !== 'ru') {
            currentParams.set('lang', language);
        }

        // Формируем новый URL с сохранением всех параметров
        const newUrl = window.location.pathname +
            (currentParams.toString() ? '?' + currentParams.toString() : '');

        console.log('Переходим на URL:', newUrl);

        // Выполняем переход с сохранением состояния страницы
        window.location.href = newUrl;
    }

    // Обработчики для кнопок языка в карточках маршрутов (если они есть)
    const routeTranslationButtons = document.querySelectorAll('.route-card .translation-buttons a');
    routeTranslationButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            console.log('Клик по кнопке перевода маршрута:', this.href);

            // Показываем индикатор загрузки
            showLoadingIndicator();

            // Получаем язык из ссылки
            const url = new URL(this.href);
            const langParam = url.searchParams.get('lang') || 'ru';

            // Обновляем селекторы языка в навигации
            languageSelectors.forEach(selector => {
                selector.value = langParam;
            });

            // Переводим маршруты
            translateRoutesToLanguage(langParam);
        });
    });

    // Анимация появления контента при первой загрузке
    initializePageAnimations();

    // Обработка ошибок загрузки изображений маршрутов
    handleRouteImageErrors();
});

// Функция инициализации анимаций страницы маршрутов
function initializePageAnimations() {
    const routeCards = document.querySelectorAll('.route-card');

    // Анимация появления карточек маршрутов
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }, index * 50);
            }
        });
    });

    routeCards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
        observer.observe(card);
    });

    // Анимация для заголовка страницы
    const pageHeader = document.querySelector('.page-header h1');
    if (pageHeader) {
        pageHeader.style.opacity = '0';
        pageHeader.style.transform = 'translateY(-10px)';

        setTimeout(() => {
            pageHeader.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            pageHeader.style.opacity = '1';
            pageHeader.style.transform = 'translateY(0)';
        }, 200);
    }
}

// Обработка ошибок загрузки изображений маршрутов
function handleRouteImageErrors() {
    const routeImages = document.querySelectorAll('.route-image');
    routeImages.forEach(img => {
        img.addEventListener('error', function() {
            this.src = '/images/default-route.jpg';
            this.alt = 'Изображение маршрута недоступно';
        });
    });
}

// Глобальные функции для программного управления
window.translateRoutes = function(language) {
    const languageSelectors = document.querySelectorAll('.language-selector');
    languageSelectors.forEach(selector => {
        selector.value = language;
        selector.dispatchEvent(new Event('change'));
    });
};

// Функция для получения текущего языка маршрутов
window.getCurrentRoutesLanguage = function() {
    const currentLang = /*[[${currentLang}]]*/ 'original';
    return currentLang;
};

// Функция для получения текущих параметров фильтрации
window.getCurrentRouteFilters = function() {
    const params = new URLSearchParams(window.location.search);
    return {
        page: params.get('page') || '0',
        size: params.get('size') || '10',
        sortBy: params.get('sortBy') || 'id',
        sortDir: params.get('sortDir') || 'asc',
        displayCurrency: params.get('displayCurrency'),
        lang: params.get('lang')
    };
};

// Обработчик ошибок при переводе маршрутов
window.addEventListener('error', function(e) {
    console.error('Ошибка при переводе маршрутов:', e);

    // Скрываем индикатор загрузки
    const loadingIndicator = document.getElementById('routesLoadingIndicator');
    if (loadingIndicator) {
        loadingIndicator.style.display = 'none';
    }

    // Восстанавливаем видимость контента
    const routesContainer = document.querySelector('.routes-grid');
    const pageContent = document.querySelector('.container');

    if (routesContainer) {
        routesContainer.style.opacity = '1';
    }

    if (pageContent) {
        pageContent.style.pointerEvents = 'auto';
    }
});

// Отладочная информация
console.log('🚗 Система переводов маршрутов инициализирована');
console.log('📍 Поддерживаемые языки:', ['ru', 'tr', 'en']);
console.log('🔧 Доступные функции:', [
    'translateRoutes()',
    'getCurrentRoutesLanguage()',
    'getCurrentRouteFilters()'
]);