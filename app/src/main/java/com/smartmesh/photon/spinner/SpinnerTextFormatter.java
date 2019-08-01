package com.smartmesh.photon.spinner;

import android.text.Spannable;

public interface SpinnerTextFormatter {
    Spannable format(String text);

    Spannable format(Object item);
}
