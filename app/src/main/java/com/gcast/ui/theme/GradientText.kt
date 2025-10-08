package com.gcast.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp

/** Simple gradient text composable used for the app title */
@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    brush: Brush = Brush.linearGradient(listOf(AccentStart, AccentMiddle, AccentEnd)),
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: androidx.compose.ui.unit.TextUnit = 28.sp,
    textAlign: TextAlign = TextAlign.Start
) {
    val annotated: AnnotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(brush = brush, fontWeight = fontWeight)) {
            append(text)
        }
    }

    BasicText(
        text = annotated,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium.copy(fontSize = fontSize),
        maxLines = 1
    )
}
