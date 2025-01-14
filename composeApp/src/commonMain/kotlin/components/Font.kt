package components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.ColorResource
import org.product.inventory.shared.MR

val inventoryFont = FontFamily(
    Font(resId = MR.fonts.montserrat_regular.fontResourceId, weight = Normal),
    Font(resId = MR.fonts.montserrat_semibold.fontResourceId, weight = SemiBold),
    Font(resId = MR.fonts.montserrat_bold.fontResourceId, weight = Bold),
    Font(resId = MR.fonts.montserrat_medium.fontResourceId, weight = Medium),
)

val BodySmallTextStyle = TextStyle(
    fontFamily = inventoryFont,
    fontSize = 12.sp,
    lineHeight = 12.sp,
    fontWeight = Normal,
)

val BodyMediumTextStyle = TextStyle(
    fontFamily = inventoryFont,
    fontSize = 14.sp,
    lineHeight = 14.sp,
    fontWeight = Normal,
)

val BodyLargeTextStyle = TextStyle(
    fontFamily = inventoryFont,
    fontSize = 16.sp,
    lineHeight = 16.sp,
    fontWeight = Normal,
)

val TitleLargeTextStyle = TextStyle(
    fontFamily = inventoryFont,
    fontSize = 20.sp,
    lineHeight = 20.sp,
    fontWeight = SemiBold,
)

@Composable
fun BodySmallText(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorResource = MR.colors.textBlack,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = BodySmallTextStyle.lineHeight,
    fontWeight: FontWeight? = BodySmallTextStyle.fontWeight,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = text,
        color = colorResource(color.resourceId),
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        style = BodySmallTextStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun BodyMediumText(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorResource = MR.colors.textBlack,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = BodyMediumTextStyle.lineHeight,
    fontWeight: FontWeight? = BodyMediumTextStyle.fontWeight,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = text,
        color = colorResource(color.resourceId),
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        style = BodyMediumTextStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun BodyLargeText(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorResource = MR.colors.textBlack,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = BodyLargeTextStyle.lineHeight,
    fontWeight: FontWeight? = BodyLargeTextStyle.fontWeight,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = text,
        color = colorResource(color.resourceId),
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        style = BodyLargeTextStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun TitleLargeText(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorResource = MR.colors.textBlack,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TitleLargeTextStyle.lineHeight,
    fontWeight: FontWeight? = TitleLargeTextStyle.fontWeight,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = text,
        color = colorResource(color.resourceId),
        fontWeight = fontWeight,
        textAlign = textAlign,
        lineHeight = lineHeight,
        style = TitleLargeTextStyle,
        maxLines = maxLines,
        overflow = overflow,
    )
}
