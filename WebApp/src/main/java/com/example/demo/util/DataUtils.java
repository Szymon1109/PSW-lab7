package com.example.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataUtils {

    public static boolean checkPassword(String password) {
        final String patternString = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
