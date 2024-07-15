package com.example.soundmeter2.util;

import android.content.Context;
import android.graphics.Color;


import java.util.ArrayList;

public class Utils {
    private static final String FIRST_OPEN_APP = "FIRST_OPEN_APP";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String CURRENT_THEME = "CURRENT_THEME";

    public static boolean isFirstOpenApp() {
        return SharePrefUtils.getBoolean(FIRST_OPEN_APP, true);
    }

    public static void setFirstOpenApp(boolean value) {
        SharePrefUtils.putBoolean(FIRST_OPEN_APP, value);
    }

    public static String getLanguageCode() {
        return SharePrefUtils.getString(LANGUAGE, "en");
    }

    public static void setLanguageCode(String value) {
        SharePrefUtils.putString(LANGUAGE, value);
        setFirstOpenApp(false);
    }

    public static int getCurrentTheme() {
        return SharePrefUtils.getInt(CURRENT_THEME, 1);

    }

    public static Integer fromColor(String code) {
        String cleanedCode = code.replace("#", "").replace(" ", "");
        return Color.parseColor("#" + cleanedCode);
    }

    public static void setCurrentTheme(int theme) {
        SharePrefUtils.putInt(CURRENT_THEME, theme);
    }

    public static boolean isLanguageSelected() {
        return SharePrefUtils.getBoolean("IS_LANGUAGE_SELECTED_DATA", false);
    }

    public static void setLanguageSelected(boolean value) {
        SharePrefUtils.putBoolean("IS_LANGUAGE_SELECTED_DATA", value);
    }

    public static boolean isChooseTheme() {
        return SharePrefUtils.getBoolean("IS_CHOOSE_THEME", false);
    }

    public static void setScreenAllwaysOn(boolean value) {
        SharePrefUtils.putBoolean("SCREEN_ALLWAYS_ON", value);
    }
    public  static boolean getScreenAllwaysOn(){
        return SharePrefUtils.getBoolean("SCREEN_ALLWAYS_ON",false);
    }

    public static void setChooseTheme(boolean value) {
        SharePrefUtils.putBoolean("IS_CHOOSE_THEME", value);
    }

    public static int getIndexAcceptable() {
        return SharePrefUtils.getInt("ACCEPTABLE", 1);
    }

    public static void putAcceptable(int value) {
        SharePrefUtils.putInt("ACCEPTABLE", value);
    }

//    public static Acceptable getAcceptableValue() {
//        Acceptable acceptable = null;
//        for (Acceptable acp : getAcceptableArr()) {
//            if (acp.getIndex() == getIndexAcceptable()) {
//                acceptable = acp;
//            }
//        }
//        return acceptable;
//    }
//
//    public static ArrayList<Acceptable> getAcceptableArr() {
//        ArrayList<Acceptable> acceptables = new ArrayList<>();
//        acceptables.add(new Acceptable(0, "= 0", false,0));
//        acceptables.add(new Acceptable(1, "< 0.1", false,0.1f));
//        acceptables.add(new Acceptable(2, "< 0.3", false,0.3f));
//        acceptables.add(new Acceptable(3, "< 0.4", false,0.4f));
//        acceptables.add(new Acceptable(4, "< 0.5", false,0.5f));
//        acceptables.add(new Acceptable(5, "< 1", false,1));
//
//        for (Acceptable sct : acceptables) {
//            if (sct.getIndex() == getIndexAcceptable()) {
//                sct.setIsSelect(true);
//            }
//        }
//        return acceptables;
//    }

    public static float spToPx(Context context, int sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

}
