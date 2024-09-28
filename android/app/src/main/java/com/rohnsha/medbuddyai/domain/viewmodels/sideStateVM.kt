package com.rohnsha.medbuddyai.domain.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rohnsha.medbuddyai.navigation.sidebar.domain.customDrawerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class sideStateVM: ViewModel() {

    private val _sidebarState= MutableStateFlow(customDrawerState.Closed)
    val sidebarState = _sidebarState.asStateFlow()

    fun toggleState() {
        _sidebarState.value = if (_sidebarState.value==customDrawerState.Opened) customDrawerState.Closed else customDrawerState.Opened
        Log.d("drawerStateMaVM", _sidebarState.value.toString())
    }

    fun isOpened(): Boolean {
        return _sidebarState.value== customDrawerState.Opened
    }

}