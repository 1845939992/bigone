package com.example.campushub.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.campushub.data.model.User
import com.example.campushub.ui.screen.home.HomeViewModel

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("首页", Icons.Filled.Home, Icons.Outlined.Home, "home"),
    BottomNavItem("发帖", Icons.Filled.Add, Icons.Filled.Add, "post"),
    BottomNavItem("我的", Icons.Filled.Person, Icons.Outlined.Person, "profile")
)

@Composable
fun MainScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToSearch: () -> Unit = {},
    currentUser: User? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isShowingSearchResults by remember { mutableStateOf(false) }
    var profileHasEntered by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var searchHistory by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        searchHistory = loadSearchHistoryStatic(context)
    }
    val focusRequester = remember { FocusRequester() }
    val homeViewModel: HomeViewModel = viewModel()
    val density = androidx.compose.ui.platform.LocalDensity.current
    var navBarWidthPx by remember { mutableFloatStateOf(0f) }

    val bubbleTargetX = if (navBarWidthPx > 0f) (navBarWidthPx / 3f) * selectedTab else 0f
    val bubbleOffset by animateFloatAsState(
        targetValue = bubbleTargetX,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "bubbleOffset"
    )
    val bubbleWidth = if (navBarWidthPx > 0f) navBarWidthPx / 3f else 0f

    LaunchedEffect(searchExpanded) {
        if (searchExpanded) {
            searchHistory = loadSearchHistoryStatic(context)
            focusRequester.requestFocus()
        }
    }

    val headerHeight by animateDpAsState(
        targetValue = if (searchExpanded) 120.dp else 90.dp,
        animationSpec = tween(300),
        label = "headerHeight"
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.onSizeChanged { size ->
                    navBarWidthPx = size.width.toFloat()
                }
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    bottomNavItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (index == 0) {
                                    homeViewModel.loadPosts()
                                }
                                selectedTab = index
                            },
                            icon = {
                                Icon(
                                    if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                if (navBarWidthPx > 0f) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) {
                                    (bubbleOffset + bubbleWidth * 0.15f).toDp()
                                },
                                y = 8.dp
                            )
                            .size(
                                width = with(density) { (bubbleWidth * 0.7f).toDp() },
                                height = 32.dp
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (selectedTab == 2) Modifier.height(headerHeight)
                        else Modifier.animateContentSize()
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = if (selectedTab == 2) 0.dp else 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (selectedTab == 2) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            selectedTab = 0
                            homeViewModel.loadPosts()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回首页",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "用户中心",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                } else if (searchExpanded) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                searchExpanded = false
                                searchQuery = ""
                                isShowingSearchResults = false
                                homeViewModel.loadPosts()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回首页",
                                    tint = Color.White
                                )
                            }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("搜索帖子...", color = Color.White.copy(alpha = 0.6f)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = Color.White,
                                    focusedBorderColor = Color.White.copy(alpha = 0.6f),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    saveSearchHistory(searchQuery, context)
                                    searchHistory = loadSearchHistoryStatic(context)
                                    homeViewModel.onSearchQueryChange(searchQuery)
                                    isShowingSearchResults = true
                                    searchExpanded = false
                                }) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "搜索",
                                        tint = Color.White
                                    )
                                }
                            }
                            TextButton(
                                onClick = {
                                    searchExpanded = false
                                    searchQuery = ""
                                    isShowingSearchResults = false
                                    homeViewModel.loadPosts()
                                },
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    "取消",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = searchHistory.isNotEmpty() && searchQuery.isEmpty(),
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        "搜索历史",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                    searchHistory.take(5).forEach { historyItem ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                searchQuery = historyItem
                                                homeViewModel.onSearchQueryChange(historyItem)
                                                isShowingSearchResults = true
                                                searchExpanded = false
                                            }
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                historyItem,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    removeSearchHistory(historyItem, context)
                                                    searchHistory = loadSearchHistoryStatic(context)
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "删除",
                                                    tint = Color.White.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (isShowingSearchResults) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            isShowingSearchResults = false
                            homeViewModel.loadPosts()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回首页",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "搜索结果",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTab = 2 }
                        ) {
                            val painter = rememberVectorPainter(Icons.Default.AccountCircle)
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(currentUser?.avatarUrl ?: "")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                placeholder = painter,
                                error = painter,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentUser?.nickname ?: "CampusHub",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "浏览校园新鲜事",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {
                            searchExpanded = true
                        }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "搜索",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(250),
                    label = "pageTransition"
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreenContent(
                            onNavigateToPostDetail = onNavigateToPostDetail,
                            viewModel = homeViewModel
                        )
                        1 -> PostScreenContent(
                            onNavigateBack = { selectedTab = 0 }
                        )
                        2 -> ProfileScreenContent(
                            onLogoutSuccess = onLogoutSuccess,
                            onNavigateBack = { selectedTab = 0 },
                            isFirstEnter = !profileHasEntered,
                            onEnterComplete = { profileHasEntered = true }
                        )
                    }
                }
            }
        }
    }
}

private fun saveSearchHistory(query: String, context: android.content.Context) {
    val prefs = context.getSharedPreferences("search_history", android.content.Context.MODE_PRIVATE)
    val existing = loadSearchHistoryStatic(context).toMutableList()
    existing.removeAll { it.equals(query, ignoreCase = true) }
    existing.add(0, query)
    if (existing.size > 10) existing.removeAt(existing.size - 1)
    prefs.edit().putString("history", existing.joinToString(";;")).apply()
}

private fun loadSearchHistoryStatic(context: android.content.Context): List<String> {
    return try {
        val prefs = context.getSharedPreferences("search_history", android.content.Context.MODE_PRIVATE)
        prefs.getString("history", "")?.split(";;")?.filter { it.isNotBlank() } ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}

private fun removeSearchHistory(query: String, context: android.content.Context) {
    val prefs = context.getSharedPreferences("search_history", android.content.Context.MODE_PRIVATE)
    val existing = loadSearchHistoryStatic(context).toMutableList()
    existing.removeAll { it.equals(query, ignoreCase = true) }
    prefs.edit().putString("history", existing.joinToString(";;")).apply()
}

@Composable
fun HomeScreenContent(
    onNavigateToPostDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    var itemsVisible by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(viewModel.posts.size) {
        if (viewModel.posts.isNotEmpty()) {
            kotlinx.coroutines.delay(100)
            itemsVisible = true
        }
    }

    AnimatedVisibility(
        visible = viewModel.isLoading && viewModel.posts.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) {
                com.example.campushub.ui.component.SkeletonPostCard()
            }
        }
    }

    AnimatedVisibility(
        visible = !viewModel.isLoading || viewModel.posts.isNotEmpty(),
        enter = fadeIn()
    ) {
        if (viewModel.posts.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "还没有帖子，快来发布第一条吧",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    viewModel.posts,
                    key = { _, post -> post.id }
                ) { index, post ->
                    com.example.campushub.ui.component.ScaleInItem(
                        visible = itemsVisible,
                        index = index
                    ) {
                        com.example.campushub.ui.component.PostCard(
                            post = post,
                            onLikeClick = { viewModel.toggleLike(post.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(post.id) },
                            onCommentClick = { onNavigateToPostDetail(post.id) },
                            onCardClick = { onNavigateToPostDetail(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostScreenContent(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    com.example.campushub.ui.screen.post.PostScreen(
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    onLogoutSuccess: () -> Unit,
    onNavigateBack: (() -> Unit)?,
    isFirstEnter: Boolean = true,
    onEnterComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    com.example.campushub.ui.screen.profile.ProfileScreen(
        onLogoutSuccess = onLogoutSuccess,
        onNavigateBack = onNavigateBack ?: {},
        isFirstEnter = isFirstEnter,
        onEnterComplete = onEnterComplete,
        modifier = modifier
    )
}
