package co.wawand.mobile.park_solution.shared.domain.repository

import co.wawand.mobile.park_solution.shared.domain.model.CompanyConfig
import kotlinx.coroutines.flow.Flow

interface CompanyConfigRepository {

    fun getCompaniesConfig(): Flow<List<CompanyConfig>>
    fun getCompanyConfigById(id: String): Flow<CompanyConfig?>
    fun getCompanyByOwnerId(ownerId: String): Flow<CompanyConfig?>
    fun getCompanyByAccessCode(accessCode: String): Flow<CompanyConfig?>
    suspend fun saveCompanyConfig(companyConfig: CompanyConfig)
    suspend fun updateCompanyConfig(companyConfig: CompanyConfig)
    suspend fun deleteCompanyConfig(companyConfig: CompanyConfig)
}