package co.wawand.mobile.park_solution.shared.data

import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.flow

class CompanyConfigRepositoryImpl : CompanyConfigRepository {
    override fun getCompaniesConfig() = flow {
        Firebase.firestore.collection("company_configs").snapshots.collect { querySnapshot ->
            val companies = querySnapshot.documents.map { documentSnapshot ->
                documentSnapshot.data<CompanyConfig>()
            }
            emit(companies)
        }
    }

    override fun getCompanyConfigById(id: String) = flow {
        Firebase.firestore.collection("company_configs")
            .document(id).snapshots.collect { documentSnapshot ->
                emit(documentSnapshot.data<CompanyConfig>())
            }
    }

    override fun getCompanyByOwnerId(ownerId: String) = flow {
        Firebase.firestore.collection("company_configs")
            .where { "ownerId" equalTo ownerId }
            .snapshots
            .collect { documentSnapshot ->
                documentSnapshot.documents.singleOrNull().let {
                    emit(it?.data<CompanyConfig>())
                }
            }
    }

    override suspend fun addCompanyConfig(companyConfig: CompanyConfig) {
        try {
            Firebase.firestore.collection("company_configs")
                .document(companyConfig.id)
                .set(companyConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateCompanyConfig(companyConfig: CompanyConfig) {
        Firebase.firestore.collection("company_configs")
            .document(companyConfig.id).set(companyConfig)

    }

    override suspend fun deleteCompanyConfig(companyConfig: CompanyConfig) {
        Firebase.firestore.collection("company_configs")
            .document(companyConfig.id)
            .delete()
    }
}
