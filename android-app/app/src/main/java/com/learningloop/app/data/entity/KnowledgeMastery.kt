package com.learningloop.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 知识点掌握度：每个孩子对每个知识点的掌握程度 0-100。
 *
 * 自适应测评引擎、成长曲线分析、薄弱点雷达图，都依赖这张表。
 */
@Entity(
    tableName = "knowledge_mastery",
    indices = [
        Index(value = ["user_id", "knowledge_point"], unique = true, name = "idx_km_u_kp")
    ]
)
data class KnowledgeMastery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long = 1L,

    @ColumnInfo(name = "knowledge_point")
    val knowledgePoint: String,

    /** 掌握度 0-100 */
    val mastery: Int = 50,

    @ColumnInfo(name = "last_practiced_at")
    val lastPracticedAt: Long = System.currentTimeMillis()
)
