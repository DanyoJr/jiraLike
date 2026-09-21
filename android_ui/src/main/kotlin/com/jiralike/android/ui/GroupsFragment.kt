package com.jiralike.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.data.model.CreateIssueRequest
import com.jiralike.android.data.model.IssueDto
import com.jiralike.android.data.model.UpdateStatusRequest
import com.jiralike.android.data.network.RetrofitClient
import com.jiralike.android.databinding.FragmentGroupsBinding
import com.jiralike.android.ui.board.IssueAdapter
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private var projectId: Long = -1L

    private lateinit var todoAdapter: IssueAdapter
    private lateinit var inProgressAdapter: IssueAdapter
    private lateinit var doneAdapter: IssueAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newTaskButton.setOnClickListener {
            showCreateTaskDialog()
        }

        setupRecyclerViews()
        loadProjects()
    }

    private fun showCreateTaskDialog() {
        if (projectId == -1L) {
            Toast.makeText(requireContext(), "Selecione um projeto primeiro", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = com.jiralike.android.ui.board.TaskDetailDialogFragment.newInstance(projectId)
        dialog.show(childFragmentManager, "TaskDetailDialog")
    }

    private fun createTask(title: String, description: String) {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val req = CreateIssueRequest(title, description)
                val response = RetrofitClient.api.createIssue(token, projectId, req)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Tarefa criada", Toast.LENGTH_SHORT).show()
                    loadIssues()
                } else {
                    Toast.makeText(requireContext(), "Erro ao criar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        todoAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "TODO") }, { issue -> moveIssue(issue, "IN_PROGRESS") })
        inProgressAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "TODO") }, { issue -> moveIssue(issue, "DONE") })
        doneAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "IN_PROGRESS") }, { issue -> moveIssue(issue, "DONE") })

        binding.recyclerTodo.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTodo.adapter = todoAdapter

        binding.recyclerInProgress.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerInProgress.adapter = inProgressAdapter

        binding.recyclerDone.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDone.adapter = doneAdapter
    }

    private fun loadProjects() {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProjects(token)
                if (response.isSuccessful && response.body() != null) {
                    val projects = response.body()!!
                    if (projects.isNotEmpty()) {
                        val adapter = android.widget.ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            projects.map { it.name }
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.projectSpinner.adapter = adapter
                        
                        binding.projectSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                                projectId = projects[pos].id
                                loadIssues()
                            }
                            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                        }
                        
                        // Garante que o primeiro grupo já fique selecionado na variável
                        projectId = projects[0].id
                        binding.projectSpinner.setSelection(0)
                        loadIssues()
                    } else {
                        Toast.makeText(requireContext(), "Nenhum grupo encontrado. Crie um na Área Adm.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun loadIssues() {
        val token = SessionManager.getToken(requireContext()) ?: return
        if (projectId == -1L) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getIssues(token, projectId)
                if (response.isSuccessful && response.body() != null) {
                    val issues = response.body()!!
                    val todoList = issues.filter { it.status == "TODO" }
                    val inProgressList = issues.filter { it.status == "IN_PROGRESS" || it.status == "IN_REVIEW" }
                    val doneList = issues.filter { it.status == "DONE" }

                    todoAdapter.updateData(todoList)
                    inProgressAdapter.updateData(inProgressList)
                    doneAdapter.updateData(doneList)
                } else {
                    Toast.makeText(requireContext(), "Failed to load issues", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun moveIssue(issue: IssueDto, targetStatus: String) {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.updateIssueStatus(token, issue.id, UpdateStatusRequest(targetStatus))
                if (response.isSuccessful) {
                    loadIssues()
                } else {
                    Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
