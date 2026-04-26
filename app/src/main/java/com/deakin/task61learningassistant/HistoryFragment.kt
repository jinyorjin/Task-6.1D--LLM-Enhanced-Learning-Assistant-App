package com.deakin.task61learningassistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val listContainer = view.findViewById<LinearLayout>(R.id.historyListContainer)
        val tvEmpty = view.findViewById<TextView>(R.id.tvHistoryEmpty)
        val btnBack = view.findViewById<Button>(R.id.btnBackFromHistory)

        val storage = HistoryStorage(requireContext())
        val items = storage.getAllHistory()

        if (items.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            HistoryAdapter().render(listContainer, items) { selected ->
                val bundle = Bundle().apply {
                    putString("prompt", selected.prompt)
                    putString("response", selected.response)
                    putString("featureType", selected.featureType)
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShareFragment().apply { arguments = bundle })
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
