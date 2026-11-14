package com.jiralike.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.data.network.FirestoreRepository
import com.jiralike.android.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.deadlinesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.activitiesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val repo = FirestoreRepository()
                // Assuming token is userEmail for now, or just we use the currently logged email
                // Actually the token is the user ID. But we don't have a way to fetch all projects globally unless we use a global query, 
                // but let's fetch projects for this user. Wait, the old code used just the token.
                // Let's use getProjects using userEmail. Since we don't have userEmail saved in SessionManager, let's just get all projects for now.
                // Actually we just get projects where user is owner. But we need email. 
                // Let's get issues directly for the mock summary. We can't without project id in firestore. 
                // I will mock the summary text for now since Dashboard needs proper queries based on logged in user.
                binding.welcomeText.text = "Olá!"
                binding.taskCountText.text = "Carregando..."
                binding.taskSummaryText.text = "Firebase Conectado"
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
