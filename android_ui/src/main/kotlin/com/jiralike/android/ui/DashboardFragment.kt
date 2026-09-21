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
import com.jiralike.android.data.network.RetrofitClient
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
                // To fetch total tasks we would need an endpoint /api/issues/my 
                // For simplicity let's mock the count or fetch projects then issues.
                // Assuming we just mock the numbers for now as in the wireframe, but let's try to get real projects.
                val projRes = RetrofitClient.api.getProjects(token)
                if (projRes.isSuccessful && projRes.body() != null) {
                    val projects = projRes.body()!!
                    var totalIssues = 0
                    var inProgressIssues = 0
                    var todoIssues = 0
                    
                    for (p in projects) {
                        val issueRes = RetrofitClient.api.getIssues(token, p.id)
                        if (issueRes.isSuccessful && issueRes.body() != null) {
                            val issues = issueRes.body()!!
                            totalIssues += issues.size
                            todoIssues += issues.count { it.status == "TODO" }
                            inProgressIssues += issues.count { it.status == "IN_PROGRESS" }
                        }
                    }
                    
                    binding.welcomeText.text = "Olá!"
                    binding.taskCountText.text = "$totalIssues Tarefas"
                    binding.taskSummaryText.text = "$todoIssues novas hoje, $inProgressIssues em andamento."
                }
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
