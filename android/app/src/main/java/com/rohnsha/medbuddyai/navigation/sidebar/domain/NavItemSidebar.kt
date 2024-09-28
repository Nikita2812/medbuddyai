package com.rohnsha.medbuddyai.navigation.sidebar.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItemSidebar(
    val title: String,
    val imageVector: ImageVector,
    val onclick: () -> Unit
) {

    Home(
        title = "Home",
        imageVector = Icons.Outlined.Home,
        onclick = {}
    ),
    Search(
        title = "Search",
        imageVector = Icons.Outlined.Search,
        onclick = {}
    ),
    Library(
        title = "Library",
        imageVector = Icons.Outlined.Book,
        onclick = {}
    ),


}