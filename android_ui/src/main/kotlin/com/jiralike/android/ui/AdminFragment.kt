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
import com.jiralike.android.data.model.CreateProjectRequest
import com.jiralike.android.data.network.RetrofitClient
import com.jiralike.android.databinding.FragmentAdminBinding
import com.jiralike.android.ui.dashboard.ProjectAdapter
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var groupAdapter: ProjectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        groupAdapter = ProjectAdapter(emptyList()) { project ->
            Toast.makeText(requireContext(), "Grupo: ${project.name}", Toast.LENGTH_SHORT).show()
        }
        
        binding.groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.groupsRecyclerView.adapter = groupAdapter
        
        binding.membersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        binding.createGroupButton.setOnClickListener {
            showCreateGroupDialog()
        }
        
        loadGroups()
    }

    private fun showCreateGroupDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
        }
        val nameInput = EditText(context).apply { hint = "Nome do Grupo" }
        val keyInput = EditText(context).apply { 
            hint = "Chave (Apenas MAIÚSCULAS)" 
            filters = arrayOf(android.text.InputFilter.AllCaps())
        }
        layout.addView(nameInput)
        layout.addView(keyInput)

        AlertDialog.Builder(context)
            .setTitle("Criar novo grupo")
            .setView(layout)
            .setPositiveButton("Criar") { _, _ ->
                val name = nameInput.text.toString().trim()
                val key = keyInput.text.toString().trim().uppercase()
                
                if (name.isEmpty() || key.isEmpty()) {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (!key.matches(Regex("^[A-Z]{2,10}$"))) {
                    Toast.makeText(context, "A Chave deve ter 2 a 10 letras", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                
                createGroup(name, key)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createGroup(name: String, key: String) {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val request = CreateProjectRequest(name, key, "Descrição do grupo")
                val response = RetrofitClient.api.createProject(token, request)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                    loadGroups()
                } else {
                    Toast.makeText(context, "Erro ao criar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun loadGroups() {
        val token = SessionManager.getToken(requireContext()) ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProjects(token)
                if (response.isSuccessful && response.body() != null) {
                    groupAdapter.updateData(response.body()!!)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
