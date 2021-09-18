package com.mrright.distancecalc.utils.helpers

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog

object DialogHelpers {


    /**
     * Selectable dialog
     *
     * @param context [Context]
     * @param list [Array]
     * @param checkedItem [Int]
     * @param title [String]
     * @param negativeButton [String]
     * @param onSelect [DialogInterface] [Int]
     * @receiver
     * @return
     */
    fun selectableDialog(
        context: Context,
        list: Array<String>,
        checkedItem: Int,
        title: String,
        negativeButton: String = "Close",
        onSelect: (dialog: DialogInterface, id: Int) -> Unit,
    ): AlertDialog.Builder {
        return AlertDialog.Builder(context).apply {
            setSingleChoiceItems(list, checkedItem, onSelect)
            setNegativeButton(negativeButton, null)
            setTitle(title)
        }
    }


    fun selectableDialogA(
        context: Context,
        list: Array<String>? = null,
        checkedItem: Int,
        title: String? = null,
        @DrawableRes
        icon: Int? = null,
        positiveButton: String? = null,
        onPositiveClick: ((dialog: DialogInterface, id: Int) -> Unit)? = null,
        negativeButton: String? = null,
        onNegativeClick: ((dialog: DialogInterface, id: Int) -> Unit)? = null,
        onSelect: ((dialog: DialogInterface, id: Int) -> Unit)? = null,
    ): AlertDialog.Builder {
        return AlertDialog.Builder(context).apply {
            setSingleChoiceItems(list, checkedItem, onSelect)
            setPositiveButton(positiveButton, onPositiveClick)
            setNegativeButton(negativeButton, onNegativeClick)
            icon?.let {
                setIcon(it)
            }
            setTitle(title)
        }
    }


}