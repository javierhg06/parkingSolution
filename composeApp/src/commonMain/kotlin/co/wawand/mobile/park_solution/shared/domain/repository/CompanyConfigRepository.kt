package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import co.wawand.mobile.park_solution.shared.util.RequestState
import kotlinx.coroutines.flow.Flow

interface CompanyConfigRepository {

    fun readCompanyConfig(): Flow<RequestState<CompanyConfig>?>

    fun getCompaniesConfig(): Flow<List<CompanyConfig>>
    fun getCompanyConfigById(id: String): Flow<CompanyConfig?>
    fun getCompanyByOwnerId(ownerId: String): Flow<CompanyConfig?>
    fun getCompanyByAccessCode(accessCode: String): Flow<CompanyConfig?>
    suspend fun saveCompanyConfigFromSetUp(companyConfig: CompanyConfig)
    suspend fun saveCompanyConfig(companyConfig: CompanyConfig): RequestState<Unit>
    suspend fun deleteCompanyConfig(companyConfig: CompanyConfig)
}