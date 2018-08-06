package com.apiumhub.github.presentation.details

import com.apiumhub.github.asyncAwait
import com.apiumhub.github.data.IGithubRepository
import com.apiumhub.github.domain.entity.Repository
import com.apiumhub.github.domain.entity.RepositoryDetailsDto
import com.apiumhub.github.domain.repository.details.RepositoryDetailsInteractor
import com.apiumhub.github.launchAsync

interface IRepositoryDetailsService {

    suspend fun getRepositoryDetails(user: String, repositoryName: String): RepositoryDetailsDto
    suspend fun onReadmeLoaded(user: String, repositoryName: String): String

    companion object {
        fun create() = RepositoryDetailsInteractor(IGithubRepository.create())
        fun create(repository: IGithubRepository) = RepositoryDetailsInteractor(repository)
    }
}

interface IRepositoryDetailsView {

    fun loadRepositoryDetails(func: (user: String, repository: String) -> Unit)
    fun repositoryInformationLoaded(details: RepositoryDetailsDto)
    fun readmeLoaded(details:String)

    companion object {
        fun create(repository: Repository) = RepositoryDetailsFragment.newInstance(repository)
    }
}

class RepositoryDetailsPresenter(view: IRepositoryDetailsView, service: IRepositoryDetailsService) {
    init {
        view.loadRepositoryDetails { user, repository ->
            launchAsync {
                val repositoryDetail = asyncAwait {service.getRepositoryDetails(user, repository) }
                val value:String = asyncAwait {service.onReadmeLoaded(user,repository) }

                view.repositoryInformationLoaded(repositoryDetail)
                view.readmeLoaded(value)
            }

        }

    }
}