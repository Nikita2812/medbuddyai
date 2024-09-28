package com.rohnsha.medbuddyai.domain.dataclass

data class moreActions(
    val title: String,
    val onClick: () -> Unit
)

data class moreActionsWithSubheader(
    val title: String,
    val subheader: String,
    val onClick: () -> Unit
)
