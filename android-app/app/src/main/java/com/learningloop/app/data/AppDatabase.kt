package com.learningloop.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.learningloop.app.data.dao.AnchorStateDao
import com.learningloop.app.data.dao.ChildProfileDao
import com.learningloop.app.data.dao.KnowledgeMasteryDao
import com.learningloop.app.data.dao.LearningRecordDao
import com.learningloop.app.data.dao.PointLogDao
import com.learningloop.app.data.dao.QuestionDao
import com.learningloop.app.data.dao.RewardItemDao
import com.learningloop.app.data.dao.WrongAnswerDao
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.ChildProfile
import com.learningloop.app.data.entity.KnowledgeMastery
import com.learningloop.app.data.entity.LearningRecord
import com.learningloop.app.data.entity.PointLog
import com.learningloop.app.data.entity.Question
import com.learningloop.app.data.entity.RewardItem
import com.learningloop.app.data.entity.WrongAnswer

/**
 * 本地 Room 数据库，完全离线，无服务器依赖。
 *
 * 版本历史：
 *  - v1：题库 / 错题 / 学习记录 / 画像 / 积分 / 掌握度
 *  - v2：新增 reward_item（家长自定义奖励商城）
 *  - v3：question 新增 enabled / source / updated_at（题库显性化管理）
 *  - v4：新增 anchor_state；question 增加锚点与认知层级；learning_record 增加信号质量
 *  - v5：question / child_profile 增加 stage（幼儿园 / 小学低年级，按孩子自动切题库）
 *  - v6：积分改为「颗星」小单位，历史积分清零，奖励定价按 1/100 折算
 *  - v7：learning_record 增加改答 / 切换选项次数（证据链）
 */
@Database(
    entities = [
        Question::class,
        WrongAnswer::class,
        LearningRecord::class,
        ChildProfile::class,
        PointLog::class,
        KnowledgeMastery::class,
        RewardItem::class,
        AnchorState::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun wrongAnswerDao(): WrongAnswerDao
    abstract fun learningRecordDao(): LearningRecordDao
    abstract fun childProfileDao(): ChildProfileDao
    abstract fun pointLogDao(): PointLogDao
    abstract fun knowledgeMasteryDao(): KnowledgeMasteryDao
    abstract fun rewardItemDao(): RewardItemDao
    abstract fun anchorStateDao(): AnchorStateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `reward_item` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`emoji` TEXT NOT NULL, " +
                        "`points` INTEGER NOT NULL, " +
                        "`description` TEXT NOT NULL, " +
                        "`enabled` INTEGER NOT NULL, " +
                        "`sort_order` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `question` ADD COLUMN `enabled` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `question` ADD COLUMN `source` TEXT NOT NULL DEFAULT 'builtin'")
                db.execSQL("ALTER TABLE `question` ADD COLUMN `updated_at` INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `anchor_state` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`user_id` INTEGER NOT NULL, " +
                        "`anchor_code` TEXT NOT NULL, " +
                        "`cognitive_level` TEXT NOT NULL, " +
                        "`probability` REAL NOT NULL, " +
                        "`confidence` TEXT NOT NULL, " +
                        "`status` TEXT NOT NULL, " +
                        "`attempts` INTEGER NOT NULL, " +
                        "`correct` INTEGER NOT NULL, " +
                        "`updated_at` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `idx_as_uq` " +
                        "ON `anchor_state` (`user_id`, `anchor_code`, `cognitive_level`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `idx_as_user` ON `anchor_state` (`user_id`)"
                )

                db.execSQL("ALTER TABLE `question` ADD COLUMN `anchor_code` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `question` ADD COLUMN `cognitive_level` TEXT NOT NULL DEFAULT 'MEMORY'")
                db.execSQL("ALTER TABLE `question` ADD COLUMN `variant_type` TEXT NOT NULL DEFAULT ''")

                db.execSQL("ALTER TABLE `learning_record` ADD COLUMN `anchor_code` TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE `learning_record` ADD COLUMN `cognitive_level` TEXT NOT NULL DEFAULT 'MEMORY'")
                db.execSQL("ALTER TABLE `learning_record` ADD COLUMN `signal_quality` TEXT NOT NULL DEFAULT 'NORMAL'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `question` ADD COLUMN `stage` TEXT NOT NULL DEFAULT 'PRIMARY_LOW'")
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `idx_question_stage` ON `question` (`stage`)"
                )

                db.execSQL(
                    "ALTER TABLE `child_profile` ADD COLUMN `stage` TEXT NOT NULL DEFAULT 'PRIMARY_LOW'"
                )
                // 已经填过「幼儿园中班」这类年级的孩子，升级后自动落到幼儿园学段
                db.execSQL(
                    "UPDATE `child_profile` SET `stage` = 'KG' WHERE " +
                        "`grade` LIKE '%幼儿%' OR `grade` LIKE '%学前%' OR " +
                        "`grade` LIKE '%小班%' OR `grade` LIKE '%中班%' OR `grade` LIKE '%大班%'"
                )
            }
        }

        /**
         * v5 → v6：积分单位从「几十上百」压缩为个位数「颗星」。
         * 经确认历史数据可丢弃：所有孩子积分清零、积分流水清空；
         * 奖励定价按原值 1/100 折算（最低 1 颗），避免家长重新配置。
         */
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE `child_profile` SET `total_points` = 0")
                db.execSQL("DELETE FROM `point_log`")
                db.execSQL("UPDATE `reward_item` SET `points` = MAX(1, `points` / 100)")
            }
        }

        /** v6 → v7：记录作答过程信号，供题目质量诊断与后续归因使用 */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `learning_record` ADD COLUMN `option_switches` INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE `learning_record` ADD COLUMN `answer_changes` INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "learningloop.db"
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                        MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7
                    )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
