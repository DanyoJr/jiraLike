package com.jiralike.android.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.jiralike.android.R
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.data.model.CreateIssueRequest
import com.jiralike.android.data.model.UserDto
import com.jiralike.android.data.network.RetrofitClient
import com.jiralike.android.databinding.DialogTaskDetailBinding
import kotlinx.coroutines.launch

class TaskDetailDialogFragment : DialogFragment() {

    private var _binding: DialogTaskDetailBinding? = null
    private val binding get() = _binding!!

    private var projectId: Long = -1L
    private var projectMembers: List<UserDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_JiraGest)
        projectId = arguments?.getLong("PROJECT_ID") ?: -1L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mock status adapter
        val statuses = listOf("TODO", "IN_PROGRESS", "DONE")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter

        binding.btnBack.setOnClickListener { dismiss() }
        
        binding.btnSave.setOnClickListener { saveTask() }

        binding.btnUpload.setOnClickListener {
            Toast.makeText(requireContext(), "Upload mockado", Toast.LENGTH_SHORT).show()
        }

        loadProjectMembers()
    }

    private fun loadProjectMembers() {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProjectDetails(token, projectId)
                if (response.isSuccessful && response.body() != null) {
                    projectMembers = response.body()!!.members
                    val memberNames = projectMembers.map { it.name }
                    val memberAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, memberNames)
                    memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAssignee.adapter = memberAdapter
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun saveTask() {
        val title = binding.taskTitleInput.text.toString().trim()
        val desc = binding.taskDescInput.text.toString().trim()
        
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Título é obrigatório", Toast.LENGTH_SHORT).show()
            return
        }

        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                // Simplificação: criando sempre. Na prática, checaríamos se é edição ou criação.
                val req = CreateIssueRequest(title, desc)
                val response = RetrofitClient.api.createIssue(token, projectId, req)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Tarefa salva", Toast.LENGTH_SHORT).show()
                    (activity as? BoardActivity)?.loadIssues()
                    (parentFragment as? com.jiralike.android.ui.GroupsFragment)?.loadIssues()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(projectId: Long): TaskDetailDialogFragment {
            val args = Bundle()
            args.putLong("PROJECT_ID", projectId)
            val fragment = TaskDetailDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
