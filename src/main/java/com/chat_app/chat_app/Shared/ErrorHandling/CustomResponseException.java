package com.chat_app.chat_app.Shared.ErrorHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomResponseException extends RuntimeException {

    private String message;
    private int code;

    public static CustomResponseException idIsNotFound(UUID id) {
        return new CustomResponseException("Error: ID (" + id + ") is not found!", 404);
    }

    public static CustomResponseException publicError (String message, int code) {
        return new CustomResponseException(message, code);
    }

    public static CustomResponseException unExpectedErrorOccurred() {
        return new CustomResponseException(
                "حدث خطأ ما. يرجى المحاولة مرة أخرى لاحقًا.",
                500
        );
    }

    public static CustomResponseException badCredentials() {
        return new CustomResponseException(
                "اسم المستخدم أو كلمة المرور غير صحيحة.",
                401
        );
    }

    public static CustomResponseException duplicateItem(String item) {
        return new CustomResponseException(
                "هذا " + item + " مستخدم بالفعل.",
                409
        );
    }

    public static CustomResponseException userIsNotParticipant() {
        return new CustomResponseException(
                "المستخدم ليس عضوًا في هذه المحادثة.",
                403
        );
    }

    public static CustomResponseException noSearchResults() {
        return new CustomResponseException(
                "لم يتم العثور على نتائج.",
                404
        );
    }

    public static CustomResponseException incorrectCode () {
        return new CustomResponseException(
                "الكود المدخل غير صحيح!",
                404
        );
    }

    public static CustomResponseException expiredCode () {
        return new CustomResponseException(
                "لقد انتهت صلاحية الكود. قم بانشاء كود جديد.",
                404
        );
    }
}
