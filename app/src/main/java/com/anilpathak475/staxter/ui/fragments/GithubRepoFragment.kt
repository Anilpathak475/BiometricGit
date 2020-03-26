package com.anilpathak475.staxter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.anilpathak475.staxter.R
import com.anilpathak475.staxter.base.BaseFragment
import com.anilpathak475.staxter.store.db.RepoEntity
import com.anilpathak475.staxter.viewmodel.GithubRepoViewModel
import kotlinx.android.synthetic.main.github_repo_fragment.*
import javax.inject.Inject


class GithubRepoFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var githubRepoId: String
    private lateinit var githubRepoViewModel: GithubRepoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.github_repo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        githubRepoViewModel = ViewModelProviders.of(this, viewModelFactory).get()

        githubRepoId =
            if (savedInstanceState != null && savedInstanceState.containsKey(GITHUB_REPO_ID)) {
                savedInstanceState.getString(GITHUB_REPO_ID, "")
            } else {
                val safeArgs =
                    GithubRepoFragmentArgs.fromBundle(
                        requireArguments()
                    )
                safeArgs.githubRepoId
            }

        observeRepo()
        githubRepoViewModel.getGithubRepositoryById(githubRepoId)
    }

    private fun observeRepo() {
        githubRepoViewModel.repo.observe(viewLifecycleOwner, Observer {
            showRepoInfo(it)
        })
    }

    private fun showRepoInfo(it: RepoEntity) {
        tv_title.text = it.name
        tv_description.text = it.description
        repo_stars.text = it.stars.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GITHUB_REPO_ID, githubRepoId)
    }

    companion object {
        const val GITHUB_REPO_ID = "GITHUB_REPO_ID"
    }
}
