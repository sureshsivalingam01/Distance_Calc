package com.mrright.distancecalc.presentation.truck_fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrright.distancecalc.models.Truck


@Composable
fun TopFields(
	truck : Truck,
	onTruckNameChange : (String) -> Unit = {},
	onAllowanceChange : (String) -> Unit = {},
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 4.dp)
	) {

		OutlinedTextField(
			value = truck.truckType.value,
			onValueChange = onTruckNameChange,
			modifier = Modifier.fillMaxWidth(), label = { Text("Truck Type") },
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
			),
			maxLines = 1,
		)
		Spacer(modifier = Modifier.padding(2.dp))
		OutlinedTextField(
			value = truck.allowancePerKm.value,
			onValueChange = onAllowanceChange,
			modifier = Modifier.fillMaxWidth(), label = { Text("Allowance Per Km") },
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
			),
			maxLines = 1,
		)
	}
}

@Preview(showBackground = true)
@Composable
fun TopFieldsPreview() {
	TopFields(truck = Truck())
}