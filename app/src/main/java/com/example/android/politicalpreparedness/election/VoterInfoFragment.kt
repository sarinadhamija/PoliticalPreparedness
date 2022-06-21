package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.PoliticalPreparednessApplication
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.bindImage

class VoterInfoFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentVoterInfoBinding
    private val args: VoterInfoFragmentArgs by navArgs()

    private val voterInfoViewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory((requireContext().applicationContext as PoliticalPreparednessApplication).electionRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = FragmentVoterInfoBinding.inflate(inflater, container, false).apply {
            viewmodel = voterInfoViewModel
        }
        return viewDataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voterInfoViewModel.initializeElection(args.argElectionId, args.argDivision)
        voterInfoViewModel.toastMessage.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })

        voterInfoViewModel.isAvailableInDb.observe(viewLifecycleOwner, Observer {
            viewDataBinding.toggleFollowElection.isChecked = it
        })

        voterInfoViewModel.dataLoading.observe(viewLifecycleOwner, Observer {
            viewDataBinding.statusLoadingWheel.visibility = if (it) View.VISIBLE else View.GONE
        })

        voterInfoViewModel.voterInfo.observe(viewLifecycleOwner, Observer {
            viewDataBinding.electionName.title = it.election.name
            viewDataBinding.electionDate.text = it.election.electionDay.toString()

            val address =
                it.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
            if (address.isNullOrBlank()) {
                viewDataBinding.addressGroup.visibility = View.GONE
            } else {
                viewDataBinding.addressGroup.visibility = View.VISIBLE
                viewDataBinding.address.text = address
            }
        })

        voterInfoViewModel.openWebViewEvent.observe(viewLifecycleOwner, Observer {
            it?.apply {
                openWebView(it)
            }
        })
    }

    private fun openWebView(url: String) {
        val action = VoterInfoFragmentDirections.actionVoterInfoFragmentToWebViewFragment(
            url
        )
        findNavController().navigate(action)
        voterInfoViewModel.doneNavigating()
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}