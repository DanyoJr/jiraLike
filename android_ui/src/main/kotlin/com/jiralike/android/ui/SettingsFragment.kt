package com.jiralike.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jiralike.android.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.avatarUploadCard.setOnClickListener {
            Toast.makeText(context, "Upload de imagem clicado", Toast.LENGTH_SHORT).show()
        }
        
        binding.saveProfileButton.setOnClickListener {
            Toast.makeText(context, "Salvando perfil...", Toast.LENGTH_SHORT).show()
        }
        
        binding.notifyTaskButton.setOnClickListener {
            toggleNotificationButton(binding.notifyTaskButton)
        }
        binding.notifyCommentButton.setOnClickListener {
            toggleNotificationButton(binding.notifyCommentButton)
        }
        binding.notifyMentionButton.setOnClickListener {
            toggleNotificationButton(binding.notifyMentionButton)
        }
    }
    
    private fun toggleNotificationButton(button: com.google.android.material.button.MaterialButton) {
        if (button.text.toString().equals("ativado", ignoreCase = true)) {
            button.text = "Desativado"
            button.setBackgroundColor(android.graphics.Color.parseColor("#888888"))
        } else {
            button.text = "ativado"
            button.setBackgroundColor(android.graphics.Color.parseColor("#0022AA"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
