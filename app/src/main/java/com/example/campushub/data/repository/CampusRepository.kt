package com.example.campushub.data.repository

import com.example.campushub.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * 校园社区数据仓库
 */
class CampusRepository {
    /**
     * 获取模拟帖子列表
     */
    fun getPosts(): Flow<List<Post>> {
        return flowOf(
            listOf(
                Post("1", "欢迎使用 CampusHub", "这是我们的第一个帖子", "管理员", System.currentTimeMillis())
            )
        )
    }
}
