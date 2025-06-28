package co.wawand.mobile.park_solution

import androidx.compose.ui.window.ComposeUIViewController
import co.wawand.mobile.park_solution.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) { App() }