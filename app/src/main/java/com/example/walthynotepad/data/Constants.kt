package com.example.walthynotepad.data

object Constants {
    const val firestoreFieldNoteTable = "notes"
    const val firestoreFieldUserID = "userUID"
    const val firestoreFieldDate = "date"
    const val firestoreFieldImgURL = "img"
    const val firestoreFieldText = "text"
    const val firestoreImageDirectory = "image/"

    const val imageSearchType = "image/*"

    const val errorNoteDidntFinded = "Запись не найдена!"
    const val errorYouAreNotAuthorized = "Вы не вошли в свой акаунт!"
    const val errorInvalidEmail = "Не верный электронный адресс!"
    const val errorImageUpload = "Ошибка при попытке загрузки фото!"

    const val sharedPreferencesName = "Login Data!"
    const val email = "Электронная почта"
    const val password = "Пароль"
    const val loginLabel = "Войти"
    const val registrationLabel = "Регистрация!"
    const val loginRegisterLabel = "Войти / Зарегистрироваться"
    const val emptyString = ""
    const val enterYourNote = "Введите текст вашей заметки"
    const val addNoteLabel = "Добавить"
    const val choseYourImage = "Добавить фото"
    const val yourImage = "Ваше фото"

    const val dataFormatPattern = "HH : mm : ss \t\t dd / MM / yyyy "

    const val addedLabel = "Запись успушно добавлена!"
    const val deletedLabel = "Запись успушно удалена!"

    const val maxImageDownloadSize = 5L*1024*1024
}