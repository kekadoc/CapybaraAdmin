package com.kekadoc.project.capybara.admin.ui.form.groups

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.ui.form.Form

class AllGroupsViewState

class AllGroupsViewModel : ViewModel<AllGroupsViewState>(AllGroupsViewState())

@Composable
fun AllGroupsForm(viewModel: AllGroupsViewModel = viewModel()) = Form {
    val viewState by viewModel.container.stateFlow.collectAsState()
    Text("All Groups")
}