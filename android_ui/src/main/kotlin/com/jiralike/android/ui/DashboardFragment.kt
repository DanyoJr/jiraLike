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
        val userId = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val repo = FirestoreRepository()
                
                // 1. Buscamos o usuário no banco pelo ID para pegar o nome
                val userResult = repo.getUserById(userId)
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()!!
                    binding.welcomeText.text = "Olá, ${user.name}!"
                    
                    // 2. Buscamos os projetos desse usuário e depois contamos as tarefas
                    val projResult = repo.getProjects(user.email)
                    if (projResult.isSuccess) {
                        val projects = projResult.getOrNull()!!
                        
                        var totalIssues = 0
                        var inProgressIssues = 0
                        var todoIssues = 0
                        val allIssues = mutableListOf<com.jiralike.android.data.model.IssueDto>()
                        
                        for (p in projects) {
                            val issueResult = repo.getIssues(p.id)
                            if (issueResult.isSuccess) {
                                val issues = issueResult.getOrNull()!!
                                allIssues.addAll(issues)
                                totalIssues += issues.size
                                todoIssues += issues.count { it.status == "TODO" }
                                inProgressIssues += issues.count { it.status == "IN_PROGRESS" }
                            }
                        }
                        
                        binding.taskCountText.text = "$totalIssues Tarefas"
                        binding.taskSummaryText.text = "$todoIssues novas hoje, $inProgressIssues em andamento."

                        // Atualiza as listas abaixo dos números
                        val deadlinesAdapter = com.jiralike.android.ui.board.IssueAdapter(
                            allIssues.filter { it.dueDate != null }, 
                            {}, {} // Empty callbacks since it's just view mode here
                        )
                        binding.deadlinesRecyclerView.adapter = deadlinesAdapter

                        val activitiesAdapter = com.jiralike.android.ui.board.IssueAdapter(
                            allIssues,
                            {}, {}
                        )
                        binding.activitiesRecyclerView.adapter = activitiesAdapter
                    } else {
                        binding.taskCountText.text = "0 Tarefas"
                        binding.taskSummaryText.text = "Nenhum projeto encontrado."
                    }
                } else {
                    binding.welcomeText.text = "Olá!"
                    binding.taskCountText.text = "0 Tarefas"
                    binding.taskSummaryText.text = "Não foi possível carregar os dados."
                }
            } catch (e: Exception) {
                binding.taskCountText.text = "Erro ao carregar"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
