package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.px
import org.product.inventory.shared.MR
import org.product.inventory.web.toCssColor

private val ZERO_BREAKPOINT_TITLE_SIZE = 25.px
private val SM_BREAKPOINT_TITLE_SIZE = 30.px
private val MD_BREAKPOINT_TITLE_SIZE = 35.px

val titleWithPathStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier.padding(
            left = 30.px,
            right = 30.px,
            top = 12.px,
            bottom = 15.px,
        )
    }

    Breakpoint.SM {
        Modifier.padding(
            left = 30.px,
            right = 30.px,
            top = 14.px,
            bottom = 20.px,
        )
    }

    Breakpoint.MD {
        Modifier.padding(
            left = 30.px,
            right = 30.px,
            top = 14.px,
            bottom = 30.px,
        )
    }

    Breakpoint.LG {
        Modifier.padding(30.px)
    }
}

val breadcrumbStyle by ComponentStyle {
    Breakpoint.ZERO {
        invisibleModifier
    }

    Breakpoint.LG {
        Modifier.display(DisplayStyle.Flex)
    }
}

val titleStyle by ComponentStyle {
    Breakpoint.ZERO {
        Modifier
            .fontSize(ZERO_BREAKPOINT_TITLE_SIZE)
            .lineHeight(ZERO_BREAKPOINT_TITLE_SIZE)
            .height(ZERO_BREAKPOINT_TITLE_SIZE)
    }

    Breakpoint.SM {
        Modifier
            .fontSize(SM_BREAKPOINT_TITLE_SIZE)
            .lineHeight(SM_BREAKPOINT_TITLE_SIZE)
            .height(SM_BREAKPOINT_TITLE_SIZE)
    }

    Breakpoint.MD {
        Modifier
            .fontSize(MD_BREAKPOINT_TITLE_SIZE)
            .lineHeight(MD_BREAKPOINT_TITLE_SIZE)
            .height(MD_BREAKPOINT_TITLE_SIZE)
    }

    Breakpoint.LG {
        Modifier.fontSize(40.px)
    }
}

@Composable
fun TitleWithPath(
    items: List<BreadcrumbItem>,
    title: String,
    onPathClick: (String) -> Unit,
) {
    Column(
        modifier = titleWithPathStyle
            .toModifier()
            .display(DisplayStyle.Block),
    ) {
        Breadcrumb(
            items = items,
            onClick = onPathClick,
            modifier = breadcrumbStyle.toModifier(),
        )

        Text(
            value = title,
            modifier = titleStyle
                .toModifier()
                .color(MR.colors.textBlack.toCssColor()),
        )
    }
}
