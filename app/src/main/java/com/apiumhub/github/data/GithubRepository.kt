package com.apiumhub.github.data

import com.apiumhub.github.domain.entity.BranchDto
import com.apiumhub.github.domain.entity.CommitsDto
import com.apiumhub.github.domain.entity.Repository
import com.apiumhub.github.domain.entity.RepositorySearchDto
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import java.util.concurrent.TimeUnit

interface IGithubRepository {
    suspend fun findAllRepositories(): Result<List<Repository>>
    suspend fun searchRepositories(query: String): Result<RepositorySearchDto>
    suspend fun getCommitsForRepository(user: String, repository: String): Result<List<CommitsDto>>
    suspend fun getBranchesForRepository(user: String, repository: String): Result<List<BranchDto>>
    suspend fun getReadmeForRepository(user: String, repository: String): Result<String>

    companion object {
        fun create(): IGithubRepository {
            return GithubRepository(GithubApi.create(), errorsStream)
        }

        fun create(api: GithubApi, errorsStream: PublishSubject<Throwable>): IGithubRepository {
            return GithubRepository(api, errorsStream)
        }

        val errorsStream: PublishSubject<Throwable> = PublishSubject.create()
    }
}

class GithubRepository(private val api: GithubApi, private val errorsStream: PublishSubject<Throwable>) : IGithubRepository {

    override suspend fun findAllRepositories(): Result<List<Repository>> = executeRequest(api.findAllRepositories())

    override suspend  fun searchRepositories(query: String): Result<RepositorySearchDto> =
            executeRequest(api.searchRepositories(query))

    override suspend fun getCommitsForRepository(user: String, repository: String): Result<List<CommitsDto>> =
            executeRequest(api.getCommitsForRepository(user, repository))


    override suspend fun getBranchesForRepository(user: String, repository: String): Result<List<BranchDto>> =
            executeRequest(api.getBranchesForRepository(user, repository))

    override suspend fun getReadmeForRepository(user: String, repository: String): Result<String> =
            executeRequest(api.getReadmeForRepository(user, repository))

    private suspend fun <T: Any> executeRequest(request: Deferred<Response<T>>):Result<T> {

        var result:Result<T> = Result.Error(Exception())
        try {

            val response = request.await()

            if (response.isSuccessful) {
                if (response.body() != null) {
                    result = Result.Success(response.body()!!)
                }
            } else {
                result = Result.Error(Exception())
            }
        } catch (exception:Exception) {
            System.out.println(exception.localizedMessage)
            result = Result.Error(Exception())
        }
        return result
    }
}