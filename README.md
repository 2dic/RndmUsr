Приложение для генерации случайных пользователей через API RandomUser.me с возможностью сохранения и просмотра в оффлайн-режиме.

Экраны:
- Генерация
- Список сохраненных пользователей
- Детальная информация о каждом пользоателе

Архитектура:
- Clean Architecture с разделением на Data, Domain, Presentation слои
- MVVM паттерн
- Repository pattern для абстракции источников данных

Технологический стек:
- DI: Dagger Hilt
- Локальная БД: Room
- Сетевые запросы: Retrofit + Gson
- Асинхронность: Kotlin Coroutines + Flow
- Загрузка изображений: Glide
- UI: XML + ViewBinding
- Day/Night theme
- Поддержка портретной и альбомной ориентации
- XML

Тестирование:
- Unit tests с MockK 
- Coroutines testing с TestDispatcher
- Hilt testing для dependency injection
- ViewModel testing с StateFlow
