package com.example.fragment.project.ui.user

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.R
import com.example.fragment.project.WanTheme
import com.example.fragment.project.components.ArticleCard
import com.example.fragment.project.components.SwipeRefresh
import com.example.miaow.base.utils.getScreenWidth
import com.example.miaow.base.utils.px2dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun UserScreen(
    userId: String,
    viewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(userId)
    ),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToSystem: (cid: String) -> Unit = {},
    onNavigateToWeb: (url: String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sw = context.getScreenWidth()
    val titleBarSize = 45.dp
    val titleBarSizePx = with(LocalDensity.current) { titleBarSize.roundToPx().toFloat() }
    val avatarOffsetXPx = (sw - titleBarSizePx) / 2
    val avatarOffsetX = Dp(context.px2dp(avatarOffsetXPx))
    val targetHeight = 100.dp
    val targetHeightPx = with(LocalDensity.current) { targetHeight.roundToPx().toFloat() }
    val targetPercent by remember { mutableStateOf(Animatable(1f)) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            var dyConsumed = 0f

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                dyConsumed += delta
                dyConsumed = dyConsumed.coerceAtMost(0f)
                val percent = dyConsumed / targetHeightPx
                scope.launch {
                    targetPercent.animateTo(1 - abs(percent.coerceIn(-1f, 0f)))
                }
                if (percent > -1 && percent < 0) {
                    return Offset(0f, delta)
                }
                return Offset.Zero
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            Box(
                modifier = Modifier
                    .background(colorResource(R.color.theme))
                    .fillMaxWidth()
                    .height(titleBarSize + targetHeight * targetPercent.value)
            ) {
                IconButton(
                    modifier = Modifier.height(titleBarSize),
                    onClick = onNavigateUp
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = colorResource(R.color.white)
                    )
                }
                Image(
                    painter = painterResource(id = uiState.coinResult.getAvatarId()),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .offset(x = -(avatarOffsetX - titleBarSize) * (1 - targetPercent.value))
                        .clip(CircleShape)
                        .size(titleBarSize * targetPercent.value.coerceAtLeast(0.75f))
                        .align(Alignment.Center)
                )
                Text(
                    text = uiState.coinResult.nickname,
                    modifier = Modifier
                        .offset(
                            x = -(avatarOffsetX - (titleBarSize * 2)) * (1 - targetPercent.value),
                            y = 35.dp * targetPercent.value
                        )
                        .align(Alignment.Center),
                    fontSize = 16.sp,
                    color = colorResource(R.color.text_fff),
                )
                Text(
                    text = "积分:${uiState.coinResult.coinCount}",
                    modifier = Modifier
                        .offset(x = 0.dp, y = 55.dp * targetPercent.value)
                        .graphicsLayer {
                            alpha = targetPercent.value
                        }
                        .align(Alignment.Center),
                    fontSize = 12.sp,
                    color = colorResource(R.color.text_fff),
                )
            }
        }
    ) { innerPadding ->
        SwipeRefresh(
            items = uiState.articleResult,
            refreshing = uiState.refreshing,
            loading = uiState.loading,
            finishing = uiState.finishing,
            onRefresh = { viewModel.getHome() },
            onLoad = { viewModel.getNext() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            key = { _, item -> item.id },
        ) { _, item ->
            ArticleCard(
                data = item,
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToSystem = onNavigateToSystem,
                onNavigateToUser = {},
                onNavigateToWeb = onNavigateToWeb,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun UserScreenPreview() {
    WanTheme { UserScreen("0") }
}