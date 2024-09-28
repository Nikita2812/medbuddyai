package com.rohnsha.medbuddyai.navigation.bottombar

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Assistant
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

const val scanIndexKey= "indexScan"
const val scanMode= "scanMode"
const val scanQAMode= "scanQAMode"
const val scanResultMode= "scanResultMode"
const val scanResultKey= "scanResultKey"
const val scanResultIndex= "scanResultIndex"
const val chatID= "chatID"
const val postID= "postID"
const val chatMode= "chatMode"
const val domainSelection= "domianSelection"
const val authMode= "authMode"
const val domainID= "domainID"
const val userIndex= "userIndex"
const val viewMode= "viewMode"
const val docIndex="docIndex"
const val docURL= "docURL"
const val isLocalAccountEnabled= "isLocalAccountEnabled"

sealed class bottomNavItems(
    val title: String,
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
) {
    object Home: bottomNavItems(
        title = "Home",
        route = "home",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    object DiseaseCatelogue: bottomNavItems(
        title = "Diseases Catalogue",
        route = "disease_catalogue/{$domainSelection}",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ){
        fun returnDiseaseCatelogue(domainSelection: Int): String{
            return "disease_catalogue/$domainSelection"
        }
    }
    object Community: bottomNavItems(
        title = "Community",
        route = "community",
        unselectedIcon = Icons.Outlined.Forum,
        selectedIcon = Icons.Filled.Forum
    )

    object UserInputScreen: bottomNavItems(
        title = "UserInput",
        route = "user_input",
        unselectedIcon = Icons.Outlined.Forum,
        selectedIcon = Icons.Filled.Forum
    )

    object CommunityReply: bottomNavItems(
        title = "CommunityReply",
        route = "communityReply/{$postID}",
        unselectedIcon = Icons.Outlined.Forum,
        selectedIcon = Icons.Filled.Forum
    ){
        fun returnPostID(postID: String): String{
            return "communityReply/$postID"
        }
    }

    object Preferences: bottomNavItems(
        title = "Preferences",
        route = "Preferences",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    )

    object Scan: bottomNavItems(
        title = "scan",
        route = "scan/{$scanIndexKey}/{$scanMode}",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    ){
        fun returnScanIndex(index: Int, mode: Int= 0): String{
            return "scan/$index/$mode"
        }
    }

    object ScanQA: bottomNavItems(
        title = "scanQA",
        route = "scanQA/{$scanIndexKey}/{$scanQAMode}",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    ){
        fun returnScanIndex(index: Int, mode: Int=0): String{
            return "scanQA/$index/$mode"
        }
    }

    object ScanCategory: bottomNavItems(
        title = "scanCategory",
        route = "scanCategory",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    )

    object ScanResult: bottomNavItems(
        title = "ScanResults",
        route = "scan_result/{$scanResultKey}/{$scanResultIndex}/{$scanResultMode}",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    ){
        fun returnScanResIndex(level: Int, index: Int, mode: Int=0): String {
            return "scan_result/$level/$index/$mode"
        }
    }

    object Explore: bottomNavItems(
        title = "Explore",
        route = "explore",
        unselectedIcon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore
    )

    object BMI: bottomNavItems(
        title = "Bmi",
        route = "bmi",
        unselectedIcon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore
    )

    object Chatbot: bottomNavItems(
        title = "chat",
        route = "chat/{$chatMode}/{$chatID}",
        unselectedIcon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore
    ){
        fun returnChatID(chatID: Int, chatMode: Int): String{
            return "chat/$chatMode/$chatID"
        }
    }

    object mAI: bottomNavItems(
        title = "Chat",
        route = "m_ai",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    )

    object onBoardingScreen: bottomNavItems(
        title = "onBoardingScreen",
        route = "onBoardingScreen",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    )

    object LogoWelcome: bottomNavItems(
        title = "logoWelcome",
        route = "logoWelcome/{$isLocalAccountEnabled}",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    ){
        fun isLocalAccEnbld(isLocalAccountEnabled: Boolean = true): String {
            return "logoWelcome/$isLocalAccountEnabled"
        }
    }

    object webUIScreen: bottomNavItems(
        title = "webUIScreen",
        route = "webUIScreen/{$docURL}",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    ){
        fun returnDocURL(URL: String): String{
            val encodedUrl = Uri.encode(URL)
            return "webUIScreen/$encodedUrl"
        }
    }

    object userAuth: bottomNavItems(
        title = "userAuth",
        route = "userAuth/{$authMode}",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    ){
        fun returnAuthMode(authMode: Int): String{
            return "userAuth/$authMode"
        }
    }

    object DoctorScreen: bottomNavItems(
        title = "doctorScreen",
        route = "doctorScreen/{$domainID}",
        unselectedIcon = Icons.Outlined.AdminPanelSettings,
        selectedIcon = Icons.Filled.AdminPanelSettings
    ){
        fun returnDomainID(domainID: Int): String{
            return "doctorScreen/$domainID"
        }
    }
    object userStatScreen: bottomNavItems(
        title = "userStatScreen",
        route = "userStatScreen/{$userIndex}/{$viewMode}",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    ){
        fun returnUserIndexx(userIndex: Int, viewModeInt: Int): String{
            return "userStatScreen/$userIndex/$viewModeInt"
        }
    }

    object documentations: bottomNavItems(
        title = "documentations",
        route = "documentations/{$docIndex}",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    ){
        fun returnDoc(index: Int): String{
            return "documentations/$index"
        }
    }

    object ApiScreen: bottomNavItems(
        title = "apiScreen",
        route = "apiScreen",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    )

    object ProfileScreen: bottomNavItems(
        title = "profileScreen",
        route = "profileScreen",
        unselectedIcon = Icons.Outlined.Assistant,
        selectedIcon = Icons.Filled.Assistant
    )
}