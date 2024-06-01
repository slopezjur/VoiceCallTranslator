package com.sergiolopez.voicecalltranslator.feature.call.ui.navigation

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import kotlinx.serialization.Serializable

@Serializable
data class NavigationCallExtra(
    val call: Call,
    val hasCallData: Boolean
)