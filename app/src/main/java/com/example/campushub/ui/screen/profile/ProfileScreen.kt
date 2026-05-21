package com.example.campushub.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.campushub.data.model.Post
import com.example.campushub.ui.component.PostCard
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 个人中心界面实现 - 增强了稳定性并修复了图标引用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("我的帖子", "我的收藏")

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditPostDialog by remember { mutableStateOf<Post?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Post?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("个人中心") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showChangePasswordDialog = true }) {
                        Icon(Icons.Default.Lock, contentDescription = "修改密码")
                    }
                    IconButton(onClick = { viewModel.logout(onLogoutSuccess) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "退出登录",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 用户信息头部
            UserProfileHeader(
                userNickname = viewModel.currentUser?.nickname ?: "加载中...",
                userSchool = viewModel.currentUser?.school ?: "未知学校",
                userSignature = viewModel.currentUser?.signature ?: "这个人很懒，什么都没留下",
                avatarUrl = viewModel.currentUser?.avatarUrl,
                onEditClick = { showEditProfileDialog = true }
            )

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (viewModel.isLoading && viewModel.myPosts.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    val displayPosts = if (selectedTab == 0) viewModel.myPosts else viewModel.myFavorites

                    if (displayPosts.isEmpty()) {
                        Text(
                            text = "暂无内容",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(displayPosts, key = { it.id }) { post ->
                                if (selectedTab == 0) {
                                    EditablePostCard(
                                        post = post,
                                        onEditClick = { showEditPostDialog = post },
                                        onDeleteClick = { showDeleteConfirmDialog = post }
                                    )
                                } else {
                                    PostCard(
                                        post = post,
                                        onLikeClick = { }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 弹窗逻辑
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentNickname = viewModel.currentUser?.nickname ?: "",
            currentSignature = viewModel.currentUser?.signature ?: "",
            currentSchool = viewModel.currentUser?.school ?: "",
            currentAvatarUrl = viewModel.currentUser?.avatarUrl ?: "",
            onConfirm = { nickname, signature, school, avatarUrl ->
                viewModel.updateProfile(nickname, signature, school, avatarUrl)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onConfirm = { oldPwd, newPwd ->
                viewModel.changePassword(oldPwd, newPwd)
                showChangePasswordDialog = false
            },
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    showEditPostDialog?.let { post ->
        EditPostDialog(
            currentTitle = post.title,
            currentContent = post.content,
            onConfirm = { title, content ->
                viewModel.editPost(post.id, title, content)
                showEditPostDialog = null
            },
            onDismiss = { showEditPostDialog = null }
        )
    }

    showDeleteConfirmDialog?.let { post ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除帖子「${post.title}」吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePost(post.id)
                    showDeleteConfirmDialog = null
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun UserProfileHeader(
    userNickname: String,
    userSchool: String,
    userSignature: String,
    avatarUrl: String?,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberVectorPainter(Icons.Default.AccountCircle)
        // 使用 ImageRequest 加固，防止因权限问题直接导致主线程异常
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "头像",
            placeholder = painter,
            error = painter,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = userNickname, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = userSchool, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = userSignature, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
        }

        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "编辑资料", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentNickname: String,
    currentSignature: String,
    currentSchool: String,
    currentAvatarUrl: String,
    onConfirm: (nickname: String, signature: String, school: String, avatarUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nickname by remember { mutableStateOf(currentNickname) }
    var signature by remember { mutableStateOf(currentSignature) }
    var school by remember { mutableStateOf(currentSchool) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { avatarUri = it }
    }

    val hasNewAvatar = avatarUri != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑资料") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val painter = rememberVectorPainter(Icons.Default.AccountCircle)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    if (hasNewAvatar) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatarUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "新头像",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentAvatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "当前头像",
                            placeholder = painter,
                            error = painter,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "更换头像",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("昵称") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = signature,
                    onValueChange = { signature = it },
                    label = { Text("签名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("学校") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalAvatar = avatarUri?.toString() ?: currentAvatarUrl
                    onConfirm(nickname, signature, school, finalAvatar)
                },
                enabled = nickname.isNotBlank()
            ) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun ChangePasswordDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var oldPwd by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改密码") },
        text = {
            Column {
                OutlinedTextField(value = oldPwd, onValueChange = { oldPwd = it }, label = { Text("原密码") }, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(value = newPwd, onValueChange = { newPwd = it }, label = { Text("新密码") }, visualTransformation = PasswordVisualTransformation())
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(oldPwd, newPwd) }, enabled = newPwd.length >= 6) { Text("修改") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun EditPostDialog(
    currentTitle: String,
    currentContent: String,
    onConfirm: (title: String, content: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }
    var content by remember { mutableStateOf(currentContent) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑帖子") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("内容") }, modifier = Modifier.height(120.dp))
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(title, content) }, enabled = title.isNotBlank()) { Text("更新") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun EditablePostCard(
    post: Post,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(java.util.Date(post.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp)) }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, fontSize = 14.sp, maxLines = 2)
        }
    }
}
