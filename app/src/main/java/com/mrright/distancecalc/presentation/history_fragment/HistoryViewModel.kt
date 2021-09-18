package com.mrright.distancecalc.presentation.history_fragment

import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.mrright.distancecalc.data.firestore.models.HistoryDto
import com.mrright.distancecalc.di.Database
import com.mrright.distancecalc.utils.constants.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    @Database
    db: DocumentReference,
) : ViewModel() {


    private val query: Query =
        db.collection(Collection.HISTORY.value).orderBy("timestamp", Query.Direction.DESCENDING)

    val options = FirestoreRecyclerOptions.Builder<HistoryDto>()
        .setQuery(query, HistoryDto::class.java).build()


}






