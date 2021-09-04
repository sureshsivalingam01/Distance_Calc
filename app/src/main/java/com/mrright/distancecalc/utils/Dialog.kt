package com.mrright.distancecalc.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object Dialog {


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
		context : Context,
		list : Array<String>,
		checkedItem : Int,
		title : String,
		negativeButton : String = "Cancel",
		onSelect : (dialog : DialogInterface, id : Int) -> Unit,
	) : AlertDialog.Builder {
		return AlertDialog.Builder(context)
			.apply {
				setSingleChoiceItems(list, checkedItem, onSelect)
				setNegativeButton(negativeButton, null)
				setTitle(title)
			}
	}

}