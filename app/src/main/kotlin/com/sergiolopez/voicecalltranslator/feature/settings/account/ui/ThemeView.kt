package com.sergiolopez.voicecalltranslator.feature.settings.account.ui

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
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption

@Composable
internal fun ThemeView(
    modifier: Modifier,
    dropDownExpanded: Boolean,
    themeOption: ThemeOption,
    setTheme: (ThemeOption) -> Unit
) {
    Text(
        text = stringResource(id = R.string.theme),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary
    )
    DropDownMenuView(
        modifier = modifier,
        dropDownExpanded = dropDownExpanded,
        themeOption = themeOption,
        setTheme = setTheme,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuView(
    modifier: Modifier,
    dropDownExpanded: Boolean,
    themeOption: ThemeOption,
    setTheme: (ThemeOption) -> Unit
) {
    // TODO : Create generic DropDown for Voice, Theme and Language
    var expanded by remember { mutableStateOf(dropDownExpanded) }
    val defaultDropDownMenu = stringResource(id = themeOption.nameValue)
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
            ThemeOption.entries.forEach { selectionOption ->
                val text = stringResource(id = selectionOption.nameValue)
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        selectedOptionText = text
                        expanded = false
                        setTheme.invoke(
                            ThemeOption.getThemeEnum(
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