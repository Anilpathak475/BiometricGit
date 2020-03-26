package com.anilpathak475.staxter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.anilpathak475.staxter.R
import com.anilpathak475.staxter.base.BaseFragment
import com.anilpathak475.staxter.ui.adapter.GithubRepoListAdapter
import com.anilpathak475.staxter.viewmodel.GithubReposViewModel
import kotlinx.android.synthetic.main.github_repos_fragment.*
import javax.inject.Inject

class GithubReposFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var githubReposViewModel: GithubReposViewModel

    private val adapter by lazy {
        GithubRepoListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.github_repos_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        githubReposViewModel = ViewModelProviders.of(this, viewModelFactory).get()
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get()

        setupRecyclerView()
        setupDataSourceListener()
        githubErrorListener()
        networkErrorListener()
        githubReposViewModel.getReposByStars()
    }

    private fun retryToGetReposByStars() {
        if (adapter.itemCount == 0) {
            githubReposViewModel.getReposByStars()
        }
    }

    private fun networkErrorListener() {
        githubReposViewModel.networkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) {
                activityGeneralMessagesUtils.showSnackBarWithCloseButton(getString(R.string.internet_connection_problem))
            }
        })

        mainViewModel.internetState.observe(viewLifecycleOwner, Observer { isConnected ->
            if (isConnected) {
                retryToGetReposByStars()
            }
        })
    }

    private fun githubErrorListener() {
        githubReposViewModel.githubError.observe(viewLifecycleOwner, Observer { isGithubError ->
            if (isGithubError) {
                activityGeneralMessagesUtils.showSnackBarWithCloseButton(getString(R.string.github_error))
            }
        })
    }

    private fun setupRecyclerView() {
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recycler_view.addItemDecoration(decoration)
        recycler_view.adapter = adapter
        adapter.itemSelected = { githubRepoResponse ->
            val navigateToRepo =
                GithubReposFragmentDirections.actionOpenGithubRepo(
                    githubRepoResponse.repoId.toString()
                )
            findNavController().navigate(navigateToRepo)
        }
    }

    private fun setupDataSourceListener() {
        githubReposViewModel.repos.observe(viewLifecycleOwner, Observer {
            showEmptyList(it.size == 0)
            adapter.submitList(it)
        })
    }

    private fun showEmptyList(isEmpty: Boolean) {
        if (isEmpty) {
            emptyList.visibility = View.VISIBLE
            recycler_view.visibility = View.GONE
            progress_bar.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        }
    }
}
