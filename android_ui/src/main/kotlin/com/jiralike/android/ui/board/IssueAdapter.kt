package com.jiralike.android.ui.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jiralike.android.data.model.IssueDto
import com.jiralike.android.databinding.ItemIssueBinding

class IssueAdapter(
    private var issues: List<IssueDto>,
    private val onMoveBack: (IssueDto) -> Unit,
    private val onMoveForward: (IssueDto) -> Unit
) : RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {

    inner class IssueViewHolder(val binding: ItemIssueBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val binding = ItemIssueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IssueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issues[position]
        holder.binding.issueTitle.text = issue.title
        
        // Formatar data provisório
        holder.binding.issueDueDate.text = issue.dueDate ?: "Sem prazo"
        holder.binding.issueAssignee.text = issue.assignee?.name ?: "Não atribuído"

        when (issue.status) {
            "TODO" -> {
                holder.binding.btnMoveBack.visibility = View.GONE
                holder.binding.btnMoveForward.visibility = View.VISIBLE
                holder.binding.btnMoveForward.text = "Em andamento →"
            }
            "IN_PROGRESS" -> {
                holder.binding.btnMoveBack.visibility = View.VISIBLE
                holder.binding.btnMoveBack.text = "← A fazer"
                holder.binding.btnMoveForward.visibility = View.VISIBLE
                holder.binding.btnMoveForward.text = "Concluído →"
            }
            "DONE" -> {
                holder.binding.btnMoveBack.visibility = View.VISIBLE
                holder.binding.btnMoveBack.text = "← Em andamento"
                holder.binding.btnMoveForward.visibility = View.GONE
            }
        }

        holder.binding.btnMoveBack.setOnClickListener { onMoveBack(issue) }
        holder.binding.btnMoveForward.setOnClickListener { onMoveForward(issue) }
    }

    override fun getItemCount(): Int = issues.size

    fun updateData(newIssues: List<IssueDto>) {
        issues = newIssues
        notifyDataSetChanged()
    }
}
