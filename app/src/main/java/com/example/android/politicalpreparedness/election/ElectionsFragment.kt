package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalPreparednessApplication
import com.example.android.politicalpreparedness.data.remote.models.Division
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

private val TAG = ElectionsFragment::class.java.canonicalName

class ElectionsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentElectionBinding
    private lateinit var listAdapter: ElectionListAdapter

    private val electionsViewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory((requireContext().applicationContext as PoliticalPreparednessApplication).electionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentElectionBinding.inflate(inflater, container, false).apply {
            viewmodel = electionsViewModel
        }
        return viewDataBinding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupUpcomingElectionListAdapter()
        setupSavedElectionListAdapter()

        electionsViewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
    }

    private fun openVoterInfoDetails(electionId: Int, division: Division) {
        val action = ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
            electionId,
            division
        )
        findNavController().navigate(action)
    }

    private fun setupUpcomingElectionListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = ElectionListAdapter(viewModel)
            viewDataBinding.rvUpcomingElections.adapter = listAdapter
        } else {
            Log.v(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupNavigation() {
        electionsViewModel.openVoterInfoEvent.observe(viewLifecycleOwner, Observer {
            openVoterInfoDetails(it.id, it.division)
        })
    }

    private fun setupSavedElectionListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = ElectionListAdapter(viewModel)
            viewDataBinding.rvUpcomingElections.adapter = listAdapter
        } else {
            Log.v(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }
}