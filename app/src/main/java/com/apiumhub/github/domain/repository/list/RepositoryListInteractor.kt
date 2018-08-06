package com.apiumhub.github.domain.repository.list

import com.apiumhub.github.data.IGithubRepository
import com.apiumhub.github.data.Result
import com.apiumhub.github.domain.entity.Repository
import com.apiumhub.github.presentation.list.IRepositoryListService

class RepositoryListInteractor(private val repository: IGithubRepository) : IRepositoryListService {

    override suspend fun findAll(): Result<List<Repository>> {
        return repository.findAllRepositories()
    }

    override suspend fun search(query: String):Result<List<Repository>> {
        val result = repository.searchRepositories(query)
        return when (result) {
            is Result.Success -> Result.Success(if (result.data.items != null) result.data.items else listOf<Repository>())
            is Result.Error -> Result.Error(result.exception)
        }

    }
}