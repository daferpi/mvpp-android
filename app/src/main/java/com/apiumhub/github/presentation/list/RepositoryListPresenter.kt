package com.apiumhub.github.presentation.list

import com.apiumhub.github.asyncAwait
import com.apiumhub.github.data.IGithubRepository
import com.apiumhub.github.data.Result
import com.apiumhub.github.domain.repository.list.RepositoryListInteractor
import com.apiumhub.github.domain.entity.Repository
import com.apiumhub.github.launchAsync

interface IRepositoryListView {
    fun loadItems(func: () -> Unit)
    fun searchItems(func: (query: String) -> Unit)
    fun itemsLoaded(items: List<Repository>)
    fun errorCall(error:Throwable)

    companion object {
        fun create() = RepositoryListFragment.newInstance()
    }
}

interface IRepositoryListService {
    suspend fun findAll():Result<List<Repository>>
    suspend fun search(query: String):Result<List<Repository>>

    companion object {
        fun create() = RepositoryListInteractor(IGithubRepository.create())
        fun create(repository: IGithubRepository) = RepositoryListInteractor(repository)
    }
}

class RepositoryListPresenter(view: IRepositoryListView, service: IRepositoryListService) {

    init {
        view.loadItems {
            launchAsync {
                val result = asyncAwait { service.findAll() }
                when(result) {
                    is Result.Success -> view.itemsLoaded(result.data)
                    is Result.Error -> view.errorCall(result.exception)
                }
            }

        }

        view.searchItems {
            launchAsync {
                val result = asyncAwait { service.search(it) }
                when(result) {
                    is Result.Success -> view.itemsLoaded(result.data)
                    is Result.Error -> view.errorCall(result.exception)
                }
            }
        }

    }
}