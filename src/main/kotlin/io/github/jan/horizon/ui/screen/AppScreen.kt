package io.github.jan.horizon.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.jan.horizon.vm.AppViewModel

@Composable
fun AppScreen(viewModel: AppViewModel) {
    LaunchedEffect(Unit) {
     //   viewModel.loadDefault()
    }

    BodyScreen(viewModel)
}