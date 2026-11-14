package com.jiralike.android.ui.board

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.data.model.CreateIssueRequest
import com.jiralike.android.data.model.IssueDto
import com.jiralike.android.data.model.UpdateStatusRequest
import com.jiralike.android.data.network.FirestoreRepository
import com.jiralike.android.databinding.ActivityBoardBinding
import kotlinx.coroutines.launch

class BoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardBinding
    private var projectId: String = ""
    private var projectName: String = "Project Board"

    private lateinit var todoAdapter: IssueAdapter
    private lateinit var inProgressAdapter: IssueAdapter
    private lateinit var doneAdapter: IssueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        projectId = intent.getStringExtra("PROJECT_ID") ?: ""
        projectName = intent.getStringExtra("PROJECT_NAME") ?: "Project Board"

        binding.newTaskButton.setOnClickListener {
            showCreateTaskDialog()
        }

        setupRecyclerViews()
        loadProjects()
    }

    private fun showCreateTaskDialog() {
        if (projectId.isEmpty()) {
            Toast.makeText(this, "Selecione um projeto primeiro", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = TaskDetailDialogFragment.newInstance(projectId)
        dialog.show(supportFragmentManager, "TaskDetailDialog")
    }

    private fun createTask(title: String, description: String) {
        val repo = FirestoreRepository()
        lifecycleScope.launch {
            try {
                val req = CreateIssueRequest(title, description)
                val result = repo.createIssue(projectId, req)
                if (result.isSuccess) {
                    Toast.makeText(this@BoardActivity, "Tarefa criada", Toast.LENGTH_SHORT).show()
                    loadIssues()
                } else {
                    Toast.makeText(this@BoardActivity, "Erro ao criar: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BoardActivity, "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        todoAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "TODO") }, { issue -> moveIssue(issue, "IN_PROGRESS") })
        inProgressAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "TODO") }, { issue -> moveIssue(issue, "DONE") })
        doneAdapter = IssueAdapter(emptyList(), { issue -> moveIssue(issue, "IN_PROGRESS") }, { issue -> moveIssue(issue, "DONE") })

        binding.recyclerTodo.layoutManager = LinearLayoutManager(this)
        binding.recyclerTodo.adapter = todoAdapter

        binding.recyclerInProgress.layoutManager = LinearLayoutManager(this)
        binding.recyclerInProgress.adapter = inProgressAdapter

        binding.recyclerDone.layoutManager = LinearLayoutManager(this)
        binding.recyclerDone.adapter = doneAdapter
    }

    private fun loadProjects() {
        val repo = FirestoreRepository()
        val email = "admin@jiralike.com" // mockup since email isn't globally available easily
        lifecycleScope.launch {
            try {
                val result = repo.getProjects(email)
                if (result.isSuccess) {
                    val projects = result.getOrNull()!!
                    if (projects.isNotEmpty()) {
                        val adapter = android.widget.ArrayAdapter(
                            this@BoardActivity,
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
                        
                        val selectedIndex = projects.indexOfFirst { it.id == projectId }
                        if (selectedIndex >= 0) {
                            binding.projectSpinner.setSelection(selectedIndex)
                        } else {
                            projectId = projects[0].id
                            loadIssues()
                        }
                    } else {
                        Toast.makeText(this@BoardActivity, "Nenhum grupo encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun loadIssues() {
        if (projectId.isEmpty()) return
        val repo = FirestoreRepository()

        lifecycleScope.launch {
            try {
                val result = repo.getIssues(projectId)
                if (result.isSuccess) {
                    val issues = result.getOrNull()!!
                    val todoList = issues.filter { it.status == "TODO" }
                    val inProgressList = issues.filter { it.status == "IN_PROGRESS" || it.status == "IN_REVIEW" }
                    val doneList = issues.filter { it.status == "DONE" }

                    todoAdapter.updateData(todoList)
                    inProgressAdapter.updateData(inProgressList)
                    doneAdapter.updateData(doneList)
                } else {
                    Toast.makeText(this@BoardActivity, "Failed to load issues", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BoardActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun moveIssue(issue: IssueDto, targetStatus: String) {
        val repo = FirestoreRepository()
        lifecycleScope.launch {
            try {
                val result = repo.updateIssueStatus(issue.id, UpdateStatusRequest(targetStatus))
                if (result.isSuccess) {
                    loadIssues()
                } else {
                    Toast.makeText(this@BoardActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BoardActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
