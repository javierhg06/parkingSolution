package co.wawand.mobile.park_solution.di

import co.wawand.mobile.park_solution.AppViewModel
import co.wawand.mobile.park_solution.shared.data.CompanyConfigRepositoryImpl
import co.wawand.mobile.park_solution.shared.data.ParkingSpaceRepositoryImpl
import co.wawand.mobile.park_solution.shared.data.UserRepositoryImpl
import co.wawand.mobile.park_solution.shared.domain.repository.CompanyConfigRepository
import co.wawand.mobile.park_solution.shared.domain.repository.ParkingSpaceRepository
import co.wawand.mobile.park_solution.shared.domain.repository.UserRepository
import co.wawand.mobile.park_solution.ui.auth.SignInViewModel
import co.wawand.mobile.park_solution.ui.main.MainViewModel
import co.wawand.mobile.park_solution.ui.companySetUpSettings.CompanySetUpSettingsViewModel
import co.wawand.mobile.park_solution.ui.companySettings.CompanyConfigViewModel
import co.wawand.mobile.park_solution.ui.parking.ParkingViewModel
import co.wawand.mobile.park_solution.ui.profileContent.newDesign.UserProfileViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<CompanyConfigRepository> { CompanyConfigRepositoryImpl() }
    single<ParkingSpaceRepository> { ParkingSpaceRepositoryImpl() }

    viewModelOf(::SignInViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::UserProfileViewModel)
    viewModelOf(::ParkingViewModel)
    viewModelOf(::CompanyConfigViewModel)
    viewModelOf(::CompanySetUpSettingsViewModel)
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