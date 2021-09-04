package com.mrright.distancecalc.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mrright.distancecalc.R
import com.mrright.distancecalc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var bind : ActivityMainBinding

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		bind = ActivityMainBinding.inflate(layoutInflater)
		setContentView(bind.root)

		val navController = findNavController(R.id.fragmentContainerView)

		bind.btnNav.setupWithNavController(navController)

	}

}