package com.jiralike.android.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jiralike.android.data.model.ProjectDto
import com.jiralike.android.databinding.ItemProjectBinding

class ProjectAdapter(
    private var projects: List<ProjectDto>,
    private val onClick: (ProjectDto) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.binding.projectName.text = project.name
        holder.binding.projectDescription.text = project.description ?: "No description"
        holder.itemView.setOnClickListener { onClick(project) }
    }

    override fun getItemCount(): Int = projects.size

    fun updateData(newProjects: List<ProjectDto>) {
        projects = newProjects
        notifyDataSetChanged()
    }
}
