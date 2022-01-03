package kim.bifrost.coldrain.composechatroom.widgets

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * kim.bifrost.coldrain.composechatroom.widgets.CustomWidgets
 * ComposeChatRoom
 *
 * @author 寒雨
 * @since 2021/12/27 21:12
 **/
@Composable
fun CommonOutlinedTextField(
    modifier: Modifier,
    value: String,
    label: String,
    singleLine: Boolean = false,
    maxLines: Int = 4,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        label = {
            Text(text = label)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled)
        )
    )
}