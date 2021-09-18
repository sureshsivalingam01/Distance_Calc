package com.mrright.distancecalc.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task
import com.mrright.distancecalc.R
import com.mrright.distancecalc.databinding.ActivityMainBinding
import com.mrright.distancecalc.presentation.states_and_events.ViewState
import com.mrright.distancecalc.utils.helpers.isGpsTurnedOn
import com.mrright.distancecalc.utils.helpers.isPermissionGranted
import com.mrright.distancecalc.utils.helpers.locationSettingsRequest
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var bind: ActivityMainBinding

	private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

	private val resolutionForResult =
		registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
			if (activityResult.resultCode == RESULT_OK) {
				toast("GPS Turned On")
			} else {
				enableGps()
			}
		}

	private val viewModel: MainViewModel by viewModels()


	@SuppressLint("VisibleForTests")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bind = ActivityMainBinding.inflate(layoutInflater)
		setContentView(bind.root)

		val navController = findNavController(R.id.fragmentContainerView)

		bind.btnNav.setupWithNavController(navController)


		collectViewState()

	}


	private fun collectViewState() {
		lifecycleScope.launchWhenCreated {
			viewModel.viewState.collect {
				when (it) {
					ViewState.Init -> {
						viewModel.changeViewState(ViewState.None)
						registerActions()
						checkPermission()
					}
					ViewState.None -> Unit
				}
			}
		}
	}


	private fun registerActions() {
		requestPermissionLauncher =
			this.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
				if (it) {
					checkGpsStatus()
				} else {
					requestPermission()
				}
			}
	}


	@SuppressLint("MissingPermission")
	private fun checkGpsStatus() {
		if (!isGpsTurnedOn(this)) {
			enableGps()
		}
	}


	private fun enableGps() {

		val result: Task<LocationSettingsResponse> =
			LocationServices.getSettingsClient(applicationContext)
				.checkLocationSettings(locationSettingsRequest)

		result.addOnSuccessListener {
			toast("GPS Turned On")
		}.addOnFailureListener { exception ->
			val ex = exception as ApiException
			when (ex.statusCode) {
				LocationSettingsStatusCodes.SUCCESS -> {
					toast("GPS Turned On")
				}
				LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
					//re launch dialog to allow turn on gps
					val resolvableApiException = ex as ResolvableApiException
					val intentSenderRequest =
						IntentSenderRequest.Builder(resolvableApiException.resolution).build()
					resolutionForResult.launch(intentSenderRequest)
				}
				LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
					Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
				}
			}
		}

	}


	private fun requestPermission() {
		requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
	}


	private fun checkPermission() {
		if (isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			enableGps()
		} else {
			requestPermission()
		}
	}

}