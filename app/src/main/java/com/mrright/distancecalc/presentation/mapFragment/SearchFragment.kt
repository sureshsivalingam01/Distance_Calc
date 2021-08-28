package com.mrright.distancecalc.presentation.mapFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.mrright.distancecalc.databinding.FragmentSearchBinding
import com.mrright.distancecalc.presentation.PlacesAdapter
import com.mrright.distancecalc.utils.constants.Search
import com.mrright.distancecalc.utils.helpers.gone


class SearchFragment : DialogFragment() {

	private var _bind : FragmentSearchBinding? = null
	private val bind get() = _bind!!

	private lateinit var search : Search
	private lateinit var searchText : String

	private var autoCompleteSessionToken = AutocompleteSessionToken.newInstance()

	private lateinit var placesAdapter : PlacesAdapter

	private val viewModel by activityViewModels<MapsViewModel>()

	companion object {

		private const val SEARCH_TYPE = "SEARCH_TYPE"
		private const val SEARCH_TEXT = "SEARCH_TEXT"

		fun newInstance(
			search : Search,
			text : String,
		) : SearchFragment {
			val args = Bundle()
			args.putString(SEARCH_TYPE, search.value)
			args.putString(SEARCH_TEXT, text)
			val fragment = SearchFragment()
			fragment.arguments = args
			return fragment
		}

	}


	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View? {
		_bind = FragmentSearchBinding.inflate(inflater, container, false)
		return _bind?.root
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		init()
		editText()
		collectPlaces()


	}

	private fun editText() {

		bind.etSearch.addTextChangedListener {
			val predictionsRequest = FindAutocompletePredictionsRequest.builder()
				.apply {
					sessionToken = autoCompleteSessionToken
					query = it.toString()
				}
				.build()

			viewModel.placesAutoCompletePredicts(predictionsRequest)

		}
	}

	private fun init() {
		search = if (arguments?.getString(SEARCH_TYPE) == Search.FROM.value) Search.FROM else Search.TO
		bind.etSearchInput.hint = search.value
		searchText = arguments?.getString(SEARCH_TEXT)!!
		if (searchText.isNotEmpty()) {

			val predictionsRequest = FindAutocompletePredictionsRequest.builder()
				.apply {
					sessionToken = autoCompleteSessionToken
					query = searchText
				}
				.build()

			viewModel.placesAutoCompletePredicts(predictionsRequest)

		}

		bind.etSearch.apply {
			setText(searchText)
			requestFocus()
			val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
		}

	}

	private fun collectPlaces() {
		lifecycleScope.launchWhenStarted {
			viewModel.autoCompletePredict.observe(viewLifecycleOwner) {
				if (it.isNullOrEmpty()) {
					bind.rvSearchResults.gone()
				}
				else {

					placesAdapter = PlacesAdapter(it) { placeId, placeText ->
						viewModel.fetchPlace(search, placeId, placeText)
						this@SearchFragment.dismiss()
					}

					bind.rvSearchResults.run {
						visibility = View.VISIBLE
						layoutManager = LinearLayoutManager(requireContext())
						adapter = placesAdapter
					}
				}
			}
		}
	}

	override fun onStart() {
		super.onStart()
		dialog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT,
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		viewModel.setAutoPredictToNull()
		_bind = null
	}


}