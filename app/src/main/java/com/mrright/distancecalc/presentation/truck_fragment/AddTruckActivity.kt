package com.mrright.distancecalc.presentation.truck_fragment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.presentation.ui.theme.DistanceCalcTheme
import com.mrright.distancecalc.utils.constants.View
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddTruckActivity : ComponentActivity() {

	private val viewModel : TruckViewModel by viewModels()


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			DistanceCalcTheme {
				Surface(color = MaterialTheme.colors.background) {
					Main()
				}
			}
		}


		collectMsgs()
		collectState()
		collectArgs()

	}

	private fun collectArgs() {
		lifecycleScope.launchWhenCreated {
			viewModel.uiState.collect {
				when (it) {
					View.NONE -> getArgs()
					else -> Unit
				}
			}
		}
	}

	private fun getArgs() {
		intent?.extras?.let {
			viewModel.setArgs(
				it.getString("truckId") ?: "", View.valueOf(it.getSerializable("view").toString())
			)
		}

	}

	private fun collectState() {
		lifecycleScope.launchWhenCreated {
			viewModel.truckState.collect {
				when (it) {
					is TruckState.Success -> {
						finish()
					}
					else -> Unit
				}
			}
		}
	}

	private fun collectMsgs() {
		lifecycleScope.launchWhenCreated {
			viewModel.msg.collect {
				when (it) {
					is MessageEvent.Toast -> applicationContext.toast(it.msg, it.duration)
					else -> Unit
				}
			}
		}
	}


	@Composable
	fun Main() {

		val ranges = viewModel.rangeList.value

		val locations = viewModel.locationList.value

		val viewState = viewModel.viewState.value


		when (viewModel.truckState.collectAsState().value) {
			is TruckState.Loading -> {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					CircularProgressIndicator()
				}
			}
			is TruckState.Got, is TruckState.Error, is TruckState.New -> {
				Column(
					modifier = Modifier.fillMaxWidth()
				) {

					LazyColumn {

						item {
							TopFields(
								truck = viewModel.truck.value,
								onTruckNameChange = viewModel::setTruckType,
								onAllowanceChange = viewModel::setAllowancePerKm,
							)

							Button(
								onClick = {
									viewModel.addRange()
								}, modifier = Modifier.padding(start = 16.dp, top = 4.dp)
							) {
								Text(text = "Add Range")
							}
						}

						itemsIndexed(ranges) { index, range ->
							AddRange(
								id = index,
								range = range,
								size = ranges.size,
								remove = viewModel::removeRange,
								onFromRangeChange = viewModel::onFromRangeChange,
								onToRangeChange = viewModel::onToRangeChange,
								onAllowanceChange = viewModel::onAllowanceChange,
								onAdditionalChange = viewModel::onAdditionalChange,
							)
						}


						item {
							Button(
								onClick = {
									viewModel.addLocation()
								}, modifier = Modifier.padding(start = 16.dp)
							) {
								Text(text = "Add Location")
							}
						}

						itemsIndexed(locations) { index, location ->
							AddLocation(
								id = index,
								location = location,
								size = locations.size,
								remove = viewModel::removeLocation,
								onLocationChange = viewModel::onLocationNameChange,
								onAllowanceChange = viewModel::onLocationAllowanceChange,
							)
						}

						item {
							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.Center,
							) {
								Button(
									onClick = {
										viewModel.addAll()
									}, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
								) {
									Text(text = if (viewState == View.NEW) "Add" else "Update")
								}
							}
						}
					}
				}
			}
		}


	}


}

