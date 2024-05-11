package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

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
import androidx.compose.ui.res.stringResource
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption

@Composable
internal fun SyntheticVoiceView(
    dropDownExpanded: Boolean,
    syntheticVoiceOption: SyntheticVoiceOption,
    setSyntheticVoice: (SyntheticVoiceOption) -> Unit,
    useTrainedVoice: Boolean
) {
    Text(
        text = stringResource(id = R.string.synthetic_voice),
        color = MaterialTheme.colorScheme.primary
    )
    DropDownMenuView(
        dropDownExpanded = dropDownExpanded,
        syntheticVoiceOption = syntheticVoiceOption,
        setUseSyntheticVoice = setSyntheticVoice,
        useTrainedVoice = useTrainedVoice
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenuView(
    dropDownExpanded: Boolean,
    syntheticVoiceOption: SyntheticVoiceOption,
    setUseSyntheticVoice: (SyntheticVoiceOption) -> Unit,
    useTrainedVoice: Boolean
) {
    var expanded by remember { mutableStateOf(dropDownExpanded) }
    val defaultDropDownMenu = stringResource(id = syntheticVoiceOption.nameValue)
    var selectedOptionText by remember { mutableStateOf(defaultDropDownMenu) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it && !useTrainedVoice },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = !useTrainedVoice
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SyntheticVoiceOption.entries.forEach { selectionOption ->
                val text = stringResource(id = selectionOption.nameValue)
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        selectedOptionText = text
                        expanded = false
                        setUseSyntheticVoice.invoke(
                            SyntheticVoiceOption.getSyntheticVoiceEnum(
                                text = text
                            )
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    enabled = !useTrainedVoice
                )
            }
        }
    }
}
