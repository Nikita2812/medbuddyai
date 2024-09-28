package com.rohnsha.medbuddyai.navigation.sidebar.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohnsha.medbuddyai.domain.viewmodels.sideStateVM
import com.rohnsha.medbuddyai.navigation.sidebar.domain.NavItemSidebar
import com.rohnsha.medbuddyai.ui.theme.ViewDash
import com.rohnsha.medbuddyai.ui.theme.fontFamily
import com.rohnsha.medbuddyai.ui.theme.formAccent
import kotlin.math.roundToInt


@Composable
fun CustomDrawer(
    selectedNavigationItem: NavItemSidebar,
    onNavigationItemClick: (NavItemSidebar) -> Unit,
    onCloseClick: () -> Unit,
    sideStateVM: sideStateVM
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(fraction = 0.6f)
            .padding(start = 34.dp, end = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(94.dp))
        Image(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(68.dp)
                .clip(CircleShape)
                .background(ViewDash)
                .padding(10.dp),
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black),
            alignment = Alignment.CenterStart,
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Rohan Shaw",
            fontSize = 19.sp,
            fontFamily = fontFamily,
            modifier = Modifier.padding(horizontal = 12.dp),
            color = Color.Black,//if (selected) MaterialTheme.colorScheme.primary
            //else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight(600),//if (selected) FontWeight.Bold else FontWeight.Normal,
        )
        Spacer(modifier = Modifier.height(72.dp)) //45dp if bottom text
        NavItemSidebar.values().forEach { navigationItem ->
            NavigationItemView(
                navigationItem = navigationItem,
                selected = navigationItem == selectedNavigationItem,
                onClick = { navigationItem.onclick() }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        NavItemSidebar.values().takeLast(1).forEach { navigationItem ->
            NavigationItemView(
                navigationItem = navigationItem,
                selected = false,
                onClick = {
                    when (navigationItem) {
                        NavItemSidebar.Home -> {
                            //onNavigationItemClick(NavigationItem.Settings)
                        }

                        else -> {}
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
fun NavigationItemView(
    navigationItem: NavItemSidebar,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 99.dp))
            .clickable { onClick() }
            //.background(
            //color = if (selected) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            //else Color.Unspecified,
            //shape = RoundedCornerShape(99.dp)
            //)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = navigationItem.imageVector,
            modifier = Modifier.size(28.dp),
            contentDescription = "Navigation Item Icon",
            tint = formAccent //if (selected) MaterialTheme.colorScheme.primary
            //else MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = navigationItem.title,
            fontSize = 19.sp,
            fontFamily = fontFamily,
            color = Color.Black,//if (selected) MaterialTheme.colorScheme.primary
            //else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight(400),//if (selected) FontWeight.Bold else FontWeight.Normal,
            lineHeight = 20.sp
        )
    }
}

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = composed {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    this.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparent
            frameworkPaint.setShadowLayer(
                shadowRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
        }
    }
}

@Composable
fun sideBarModifier(
    sideStateVM: sideStateVM,
    configuration: Configuration = LocalConfiguration.current,
    density: Float = LocalDensity.current.density
): Modifier {
    val screenWidth = remember {
        derivedStateOf { (configuration.screenWidthDp * density).roundToInt() }
    }

    val offsetValue by remember { derivedStateOf { -(screenWidth.value / 4.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (sideStateVM.isOpened()) offsetValue else 0.dp,
        label = "Animated Offset"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (sideStateVM.isOpened()) 0.75f else 1f,
        label = "Animated Scale"
    )

    return Modifier
        .offset(x = animatedOffset)
        .scale(scale = animatedScale)
        .coloredShadow(
            color = Color.Black,
            alpha = 0.1f,
            shadowRadius = 50.dp
        )
}
