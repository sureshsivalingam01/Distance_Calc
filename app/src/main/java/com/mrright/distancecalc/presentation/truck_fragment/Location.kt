package com.mrright.distancecalc.presentation.truck_fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrright.distancecalc.models.Location


@Composable
fun AddLocation(
	id : Int,
	location : Location,
	size : Int,
	remove : (Int) -> Unit = {},
	onLocationChange : (Int, String) -> Unit = { _, _ -> },
	onAllowanceChange : (Int, String) -> Unit = { _, _ -> },
) {


	Surface(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp, vertical = 5.dp)
	) {
		Column {

			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.padding(top = 5.dp),
			) {
				Text(
					text = "Location ${id + 1}",
					modifier = Modifier
						.fillMaxWidth(.9f)
						.padding(start = 5.dp),
				)
				if (size > 1) {
					Icon(
						imageVector = Icons.Filled.Close,
						contentDescription = "",
						modifier = Modifier
							.fillMaxWidth()
							.clickable {
								remove(id)
							},
					)
				}

			}
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 5.dp)
					.padding(top = 2.dp),
			) {
				OutlinedTextField(
					value = location.locationName.value,
					onValueChange = {
						onLocationChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(.5f),
					label = { Text(text = "Location") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
					),
					maxLines = 1,
				)
				Spacer(modifier = Modifier.padding(2.dp))
				OutlinedTextField(
					value = location.allowance.value,
					onValueChange = {
						onAllowanceChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(),
					label = { Text(text = "Allowance") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
					),
					maxLines = 1,
				)
			}
		}
	}

}


@Preview
@Composable
fun LocationPreview() {
	AddLocation(id = 1, location = Location(), size = 2)
}