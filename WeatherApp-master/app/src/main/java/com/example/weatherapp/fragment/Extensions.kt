package com.example.weatherapp.fragment

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


//перевірка на уже наданий дозвіл в відстеженні


fun Fragment.permissionGranted(p: String): Boolean {
    return ContextCompat.checkSelfPermission(
        activity as AppCompatActivity,
        p
    ) == PackageManager.PERMISSION_GRANTED
}