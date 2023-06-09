package com.example.invetorylock

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.invetorylock.databinding.FragmentContainersListBinding
import com.example.invetorylock.retrofit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContainersListFragment : Fragment(), ContainersInteraction {

    lateinit var binding: FragmentContainersListBinding
    private val viewModel: TokenViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContainersListBinding.inflate(inflater)
        binding.root.layoutManager = LinearLayoutManager(requireContext())

        viewModel.token.observe(viewLifecycleOwner){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val containers = NetHandler.mainApi.getAllContainers(it).containers

                    requireActivity().runOnUiThread {
                        binding.root.adapter = ContainerAdapter(containers as List<Container>, this@ContainersListFragment)
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onContainerChosen(container: Container) {
        val bundle = Bundle()
        bundle.putInt("container_id", container.id)
        bundle.putInt("container_status", container.status)
        Navigation.findNavController(requireView())
            .navigate(R.id.action_containersListFragment_to_openedContainerFragment2, bundle)

    }


    companion object {
        @JvmStatic
        fun newInstance(args:Bundle?): ContainersListFragment{
            val containersListFragment = ContainersListFragment()
            containersListFragment.arguments = args
            return containersListFragment
        }

        const val TAG = "ContainerListFragment"
    }
}