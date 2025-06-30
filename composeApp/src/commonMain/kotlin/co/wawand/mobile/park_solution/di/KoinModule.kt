package co.wawand.mobile.park_solution.di

import co.wawand.mobile.park_solution.AppViewModel
import co.wawand.mobile.park_solution.shared.data.CompanyConfigRepositoryImpl
import co.wawand.mobile.park_solution.shared.data.UserRepositoryImpl
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.ui.auth.SignInViewModel
import co.wawand.mobile.park_solution.ui.MainViewModel
import co.wawand.mobile.park_solution.ui.companySettings.CompanySettingsViewModel
import co.wawand.mobile.park_solution.ui.profileContent.ProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<CompanyConfigRepository>{ CompanyConfigRepositoryImpl() }

    viewModelOf(::SignInViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::CompanySettingsViewModel)
    viewModelOf(::AppViewModel)
}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule
        )
    }
}