package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.LanguageOption

@Composable
internal fun LanguageView(
    modifier: Modifier,
    dropDownExpanded: Boolean,
    languageOption: LanguageOption,
    setLanguage: (LanguageOption) -> Unit
) {
    Text(
        text = stringResource(id = R.string.language),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.size(8.dp))
    DropDownMenuView(
        modifier = modifier,
        dropDownExpanded = dropDownExpanded,
        languageOption = languageOption,
        setLanguage = setLanguage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuView(
    modifier: Modifier,
    dropDownExpanded: Boolean,
    languageOption: LanguageOption,
    setLanguage: (LanguageOption) -> Unit
) {
    // TODO : Create generic DropDown component for Voice, Theme and Language
    var expanded by remember { mutableStateOf(dropDownExpanded) }
    val defaultDropDownMenu = stringResource(id = languageOption.nameValue)
    var selectedOptionText by remember { mutableStateOf(defaultDropDownMenu) }
    val context = LocalContext.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            modifier = modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            LanguageOption.entries.forEach { selectionOption ->
                val text = stringResource(id = selectionOption.nameValue)
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        selectedOptionText = text
                        expanded = false
                        setLanguage.invoke(
                            LanguageOption.getLanguageEnum(
                                context = context,
                                text = text
                            )
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}