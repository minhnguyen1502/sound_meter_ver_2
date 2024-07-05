package com.example.exe01.ui.sound.metal_detector;

import android.text.InputFilter;
import android.text.Spanned;

public class InputThreshold implements InputFilter {
    private final int digitsBeforeDecimal;
    private final int digitsAfterDecimal;

    public InputThreshold(int digitsBeforeDecimal, int digitsAfterDecimal) {
        this.digitsBeforeDecimal = digitsBeforeDecimal;
        this.digitsAfterDecimal = digitsAfterDecimal;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder(dest);
        builder.replace(dstart, dend, source.subSequence(start, end).toString());
        String temp = builder.toString();

        if (temp.matches("\\d{0," + digitsBeforeDecimal + "}(\\.\\d{0," + digitsAfterDecimal + "})?")) {
            return null;
        }

        return "";
    }
}
