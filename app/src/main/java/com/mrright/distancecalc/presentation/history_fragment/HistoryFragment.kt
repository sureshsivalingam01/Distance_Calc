package com.mrright.distancecalc.presentation.history_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.distancecalc.databinding.FragmentHistoryBinding
import com.mrright.distancecalc.presentation.adapters.HistoryFireStoreAdapter

class HistoryFragment : Fragment() {

    private var _bind: FragmentHistoryBinding? = null
    private val bind get() = _bind!!

    private lateinit var adapter: HistoryFireStoreAdapter

    private val viewModel: HistoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentHistoryBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryFireStoreAdapter(viewModel.options) {
            //requireContext().toast(it)
        }

        adapter.startListening()

        bind.rvHistory.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter.stopListening()
        _bind = null
    }


}