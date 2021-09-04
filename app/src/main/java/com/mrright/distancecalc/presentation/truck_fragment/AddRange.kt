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
import com.mrright.distancecalc.models.Range

@Composable
fun AddRange(
	id : Int,
	range : Range,
	size : Int,
	remove : (id : Int) -> Unit = {},
	onFromRangeChange : (id : Int, fromRange : String) -> Unit = { _ : Int, _ : String -> },
	onToRangeChange : (id : Int, toRange : String) -> Unit = { _ : Int, _ : String -> },
	onAllowanceChange : (id : Int, allowance : String) -> Unit = { _ : Int, _ : String -> },
	onAdditionalChange : (id : Int, additional : String) -> Unit = { _ : Int, _ : String -> },
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
					text = "Range ${id + 1}",
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
					value = range.fromRange.value,
					onValueChange = {
						onFromRangeChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(.5f),
					label = { Text(text = "From Range") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
					),
					maxLines = 1,
				)
				Spacer(modifier = Modifier.padding(2.dp))
				OutlinedTextField(
					value = range.toRange.value,
					onValueChange = {
						onToRangeChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(),
					label = { Text(text = "To Range") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
					),
					maxLines = 1,
				)
			}



			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 5.dp)
					.padding(bottom = 2.dp),
			) {
				OutlinedTextField(
					value = range.allowance.value,
					onValueChange = {
						onAllowanceChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(.5f),
					label = { Text(text = "Allowance Amount") },
					keyboardOptions = KeyboardOptions(
						keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
					),
					maxLines = 1,
				)
				Spacer(modifier = Modifier.padding(2.dp))
				OutlinedTextField(
					value = range.additional.value,
					onValueChange = {
						onAdditionalChange(id, it)
					},
					modifier = Modifier.fillMaxWidth(),
					label = { Text(text = "Additional Amount") },
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
fun RangePreview() {
	AddRange(id = 1, range = Range(), size = 2)
}