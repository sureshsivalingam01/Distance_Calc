package com.mrright.distancecalc.presentation

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object Dialog {

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