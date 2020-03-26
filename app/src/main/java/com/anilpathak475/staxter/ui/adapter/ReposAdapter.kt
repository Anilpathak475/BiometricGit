package com.anilpathak475.staxter.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anilpathak475.staxter.R
import com.anilpathak475.staxter.store.db.RepoEntity
import kotlinx.android.synthetic.main.repo_item.view.*

class GithubRepoListAdapter : PagedListAdapter<RepoEntity, RepoListViewHolder>(
    DIFF_CALLBACK
) {

    var itemSelected: (householdItem: RepoEntity) -> Unit = {}

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RepoListViewHolder {
        return RepoListViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(@NonNull holder: RepoListViewHolder, position: Int) {
        val repoEntity: RepoEntity? = getItem(position)
        repoEntity?.let { holder.bindTo(it) }
        holder.itemView.setOnClickListener {
            repoEntity?.let { itemSelected.invoke(repoEntity) }
        }
    }
}

class RepoListViewHolder private constructor(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    fun bindTo(repoEntity: RepoEntity?) {
        if (repoEntity != null) {
            itemView.tv_title.text = repoEntity.name
            itemView.tv_description.text = repoEntity.description ?: ""
            itemView.repo_stars.text = repoEntity.stars.toString()
        } else {
            val resources = itemView.resources
            itemView.tv_title.text = resources.getString(R.string.loading)
            itemView.tv_description.text = resources.getString(R.string.loading)
            itemView.repo_stars.text = resources.getString(R.string.loading)
        }
    }

    companion object {
        fun create(parent: ViewGroup): RepoListViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
            return RepoListViewHolder(
                view
            )
        }
    }
}

val DIFF_CALLBACK: DiffUtil.ItemCallback<RepoEntity> =
    object : DiffUtil.ItemCallback<RepoEntity>() {

        override fun areItemsTheSame(
            oldItem: RepoEntity,
            newItem: RepoEntity
        ): Boolean {
            return oldItem.repoId == newItem.repoId
        }

        override fun areContentsTheSame(
            oldItem: RepoEntity,
            newItem: RepoEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

