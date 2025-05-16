package com.example.weatherapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color.BLACK
import com.example.weatherapp.R


object DialogManager {
    //Alert dialog для вимкненого місця розташування
    fun locationDialog(context: Context, listener: Listener, onNoClick: () -> Unit = {}) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.dialog_manager_error))
        dialog.setMessage(context.getString(R.string.dialog_manager_location_disabled))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_manager_yes)) { _, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dialog_manager_no)) { _, _ ->
            onNoClick()
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
        dialog.setTitle(context.getString(R.string.dialog_manager_error))
        dialog.setMessage(context.getString(R.string.dialog_manager_incorrect_city_name))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_manager_ok)) { _, _ ->
            dialog.dismiss()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BLACK)
    }

    //Alert dialog для відсутнього інтернет з'єднання (Volley error)
    fun noConnection(context: Context, onOkClick: () -> Unit = {}) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.dialog_manager_error))
        dialog.setMessage(context.getString(R.string.dialog_manager_сant_connect_to_network))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.dialog_manager_ok)) { _, _ ->
            onOkClick()
            dialog.dismiss()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(BLACK)
    }

    interface Listener {
        fun onClick(name: String?)
    }

}