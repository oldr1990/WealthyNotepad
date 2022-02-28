package com.example.wealthynotepad.data

object Constants {

    const val FIRESTORE_FIELD_NOTE_TABLE = "notes"
    const val FIRESTORE_FIELD_USER_ID = "userUID"
    const val FIRESTORE_FIELD_DATE = "date"
    const val FIRESTORE_FIELD_IMG_URL = "img"
    const val FIRESTORE_FIEL_TEXT = "text"
    const val FIRESTORE_IMAGE_DIRECTORY = "image/"

    const val IMAGE_SEARCH_TYPE = "image/*"

    const val ERROR_NOTE_CANT_FIND_NOTE = "Запись не найдена!"
    const val ERROR_YOU_ARE_NOT_AUTHORIZED = "Вы не вошли в свой акаунт!"
    const val ERROR_INVALID_EMAIL = "Не верный электронный адресс!"
    const val ERROR_IMAGE_UPLOADING = "Ошибка при попытке загрузки фото!"
    const val ERROR_DATE = "Ошибка при определении времени!"

    const val SHARED_PREF_NAME = "Login Data!"
    const val EMAIL_LABEL = "Электронная почта"
    const val PASSWORD_LABEL = "Пароль"
    const val LOGIN_LABEL = "Войти"
    const val REGISTRATION_LABEL = "Регистрация!"
    const val LOGIN_REGISTER_LABEL = "Войти / Зарегистрироваться"
    const val EMPTY_STRING = ""
    const val ENTER_YOUR_NOTE_LABEL = "Введите текст вашей заметки"
    const val ADD_NOTE_LABEL = "Добавить"
    const val CHOSE_YOUR_IMAGE_LABEL = "Добавить фото"
    const val CHANGE_YOUR_IMAGE_LABEL = "Выбрать другое фото"
    const val YOUR_IMAGE_LABEL = "Ваше фото"
    const val DELETED_LABEL = "Вы вышли из акаута!"
    const val BACK_PRESSED_MESSAGE = "Для выхода нажмите назад еще раз"


    const val DATE_FORMAT_PATTERN = "HH : mm : ss \t\t dd / MM / yyyy "

    const val NOTE_ADDED_LABEL = "Запись успушно добавлена!"
    const val NOTE_DELETED_LABEL = "Запись успушно удалена!"


}