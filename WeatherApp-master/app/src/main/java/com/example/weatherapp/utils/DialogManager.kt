package com.example.weatherapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color.BLACK
import com.example.weatherapp.R


object DialogManager {
    //Alert dialog для вимкненого місця розташування
    fun locationDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.dialog_manager_error.toString())
        dialog.setMessage(R.string.dialog_manager_location_disabled.toString())
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, R.string.dialog_manager_yes.toString()) { _, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, R.string.dialog_manager_no.toString()) { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(BLACK)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BLACK)
    }

    //Alert dialog для некоректної назви міста (Volley error)
    fun incorrectCityName(context: Context) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.dialog_manager_error.toString())
        dialog.setMessage(R.string.dialog_manager_incorrect_city_name.toString())
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, R.string.dialog_manager_ok.toString()) { _, _ ->
            dialog.dismiss()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BLACK)
    }

    //Alert dialog для відсутнього інтернет з'єднання (Volley error)
    fun noConnection(context: Context) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.dialog_manager_error.toString())
        dialog.setMessage(R.string.dialog_manager_сant_connect_to_network.toString())
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, R.string.dialog_manager_ok.toString()) { _, _ ->
            dialog.dismiss()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BLACK)
    }

    interface Listener {
        fun onClick(name: String?)
    }

}