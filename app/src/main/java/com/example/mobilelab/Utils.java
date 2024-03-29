package com.example.mobilelab;

class Utils {
    static boolean validateEmail(String email) {
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    static boolean validatePassword(String password) {
        return !password.isEmpty() && password.length() >= 8;
    }

    static boolean validatePhone(String phone) {
        return !phone.isEmpty() && !android.util.Patterns.PHONE.matcher(phone).matches();
    }

    static boolean validateName(String name) {
        return name.matches("^[A-Za-z]+$");
    }

}
