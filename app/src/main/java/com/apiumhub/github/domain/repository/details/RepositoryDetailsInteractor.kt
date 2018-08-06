package com.apiumhub.github.domain.repository.details

import com.apiumhub.github.asyncAwait
import com.apiumhub.github.data.IGithubRepository
import com.apiumhub.github.data.Result
import com.apiumhub.github.data.StatsCachingException
import com.apiumhub.github.domain.entity.RepositoryDetailsDto
import com.apiumhub.github.presentation.details.IRepositoryDetailsService
import com.apiumhub.github.presentation.errors.IStatisticsCachingErrorInteractor

class RepositoryDetailsInteractor(private val repository: IGithubRepository) : IRepositoryDetailsService, IStatisticsCachingErrorInteractor {


    override suspend fun getRepositoryDetails(user: String, repositoryName: String): RepositoryDetailsDto {
        val countCommits = getCommitsInternal(user, repositoryName)
        val countBranches = getBranchesInternal(user, repositoryName)
        return RepositoryDetailsDto(countCommits, countBranches)
    }

    private suspend fun getCommitsInternal(user: String, repositoryName: String): Int {

        val result = asyncAwait { repository.getCommitsForRepository(user,repositoryName) }

        var count = 0
        if (result is Result.Success) {
            result.data?.forEach {
                count += it.total!!
            }
        }
        return count
    }

    private suspend fun getBranchesInternal(user: String, repositoryName: String): Int {

        val result = asyncAwait { repository.getBranchesForRepository(user,repositoryName) }

        var count = 0
        if (result is Result.Success) {
            count = result.data?.count()
        }
        return count

    }

    private suspend fun getReadmeInternal(user: String, repositoryName: String): String {

        val result = asyncAwait { repository.getReadmeForRepository(user,repositoryName) }

        if (result is Result.Success && result.data != null) {
            return result.data
        }

        return ""
    }

    override fun onStatisticsCachingError(func: (exception: StatsCachingException) -> Unit) {

    }

    override suspend fun onReadmeLoaded(user: String, repositoryName: String): String {
        return getReadmeInternal(user, repositoryName)
    }

}