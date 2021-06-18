package com.example.walthynotepad.data

object Constants {
    const val firestoreFieldNoteTable = "notes"
    const val firestoreFieldUserID = "userUID"
    const val firestoreFieldDate = "date"
    const val firestoreFieldImgURL = "img"
    const val firestoreFieldText = "text"

    const val errorNoteDidntFinded = "Запись не найдена!"
    const val errorYouAreNotAuthorized = "Вы не вошли в свой акаунт!"
    const val errorInvalidEmail = "Не верный электронный адресс!"

    const val sharedPreferencesName = "Login Data!"
    const val email = "Электронная почта"
    const val password = "Пароль"
    const val loginLabel = "Войти"
    const val registrationLabel = "Регистрация!"
    const val loginRegisterLabel = "Войти / Зарегистрироваться"
    const val emptyString = ""

    const val dataFormatPattern = "HH : mm : ss \t\t dd / MM / yyyy "

    const val addedLabel = "Запись успушно добавлена!"
    const val deletedLabel = "Запись успушно удалена!"
}