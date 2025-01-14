package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.CSSLengthOrPercentageNumericValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.background
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.modifiers.onMouseOver
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.core.Dictionary
import org.product.inventory.web.di.DiJs
import org.product.inventory.web.models.toInventoryAppRoleUi
import org.product.inventory.web.toCssColor
import user.model.InventoryAppUser

data class ProfileInfo(
    val icon: String = "",
    val email: String = "",
    val role: String = "",
) {
    val username = email.substringBefore('@')
}

fun InventoryAppUser.toProfileInfo(
    dictionary: Dictionary = DiJs.get(),
) = ProfileInfo(
    icon = profileImageUrl,
    email = email,
    role = role.toInventoryAppRoleUi(dictionary).text,
)

@Composable
fun ProfileInfo(
    profileInfo: ProfileInfo,
    modifier: Modifier = Modifier,
    showEmail: Boolean = false,
    showRole: Boolean = true,
    imageSize: CSSLengthOrPercentageNumericValue = 60.px,
    hoverColor: Color.Rgb? = null,
    backgroundColor: Color.Rgb = Colors.Transparent,
    onClick: (() -> Unit)? = null,
) {
    var appliedColor by remember { mutableStateOf(Colors.Transparent) }

    Row(
        modifier = modifier
            .thenIfNotNull(hoverColor) { color ->
                onMouseOver { appliedColor = color }
                    .onMouseLeave { appliedColor = backgroundColor }
                    .background(color = appliedColor)
            }
            .thenIfNotNull(onClick) { click ->
                clickable(click)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileImage(
            image = profileInfo.icon,
            imageSize = imageSize,
        )

        Column {
            TextWithOverflow(
                value = if (showEmail) profileInfo.email else profileInfo.username,
                modifier = Modifier.fontSize(16.px),
            )

            if (showRole) {
                Text(
                    value = profileInfo.role,
                    modifier = Modifier
                        .fontSize(16.px)
                        .fontWeight(FontWeight.SemiBold)
                        .color(Color.rgb(0x0099CC)),
                )
            }
        }
    }
}

@Composable
fun ProfileImage(
    image: String,
    imageSize: CSSLengthOrPercentageNumericValue,
    modifier: Modifier = Modifier,
) {
    CircularImage(
        path = image,
        modifier = modifier
            .borderRadius(r = imageSize)
            .border {
                color(MR.colors.secondary.toCssColor())
                style(LineStyle.Solid)
                width(4.px)
            }
            .margin(right = 10.px),
        size = imageSize,
    )
}
