package com.mrright.distancecalc.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrright.distancecalc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var bind : ActivityMainBinding

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		bind = ActivityMainBinding.inflate(layoutInflater)
		setContentView(bind.root)



	}
}