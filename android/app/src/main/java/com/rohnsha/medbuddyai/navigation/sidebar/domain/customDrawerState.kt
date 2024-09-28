package com.rohnsha.medbuddyai.navigation.sidebar.domain

enum class customDrawerState {
    Opened, Closed
}

fun customDrawerState.isOpened(): Boolean {
    return this.name == "Opened"
}

fun customDrawerState.toggle(): customDrawerState {
    return if (this==customDrawerState.Opened) customDrawerState.Closed else customDrawerState.Opened
}