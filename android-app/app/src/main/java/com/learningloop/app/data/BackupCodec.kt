package com.learningloop.app.data

import com.learningloop.app.data.entity.ChildProfile
import com.learningloop.app.data.entity.AnchorState
import com.learningloop.app.data.entity.KnowledgeMastery
import com.learningloop.app.data.entity.LearningRecord
import com.learningloop.app.data.entity.Question
import com.learningloop.app.data.entity.PointLog
import com.learningloop.app.data.entity.RewardItem
import com.learningloop.app.data.entity.WrongAnswer
import org.json.JSONArray
import org.json.JSONObject

/**
 * 用户数据备份 / 恢复。
 *
 * 目的：换机、重装、清数据前可完整导出，导入后恢复到导出时的状态。
 * 覆盖范围：孩子档案、学习记录、错题本、知识点掌握度、积分流水、奖励配置。
 * 题库不在此列——题库通过 CSV 单独管理。
 */
object BackupCodec {

    private const val SCHEMA = 1
    private const val APP_TAG = "learningloop"

    data class ImportResult(
        val children: Int,
        val records: Int,
        val wrongs: Int,
        val mastery: Int,
        val points: Int,
        val rewards: Int,
        val anchors: Int
    ) {
        fun summary(): String =
            "恢复完成：$children 个孩子、$records 条学习记录、$wrongs 条错题、" +
                "$mastery 个知识点、$points 条积分流水、$rewards 个奖励、$anchors 个锚点状态"
    }

    suspend fun export(db: AppDatabase): String {
        val root = JSONObject()
        root.put("app", APP_TAG)
        root.put("schema", SCHEMA)
        root.put("exportedAt", System.currentTimeMillis())

        root.put("children", JSONArray().apply {
            db.childProfileDao().listAll().forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("grade", c.grade)
                    put("avatar", c.avatar)
                    put("totalPoints", c.totalPoints)
                    put("currentLevel", c.currentLevel)
                    put("stage", c.stage)
                })
            }
        })

        root.put("learningRecords", JSONArray().apply {
            db.learningRecordDao().dumpAll().forEach { r ->
                put(JSONObject().apply {
                    put("id", r.id)
                    put("userId", r.userId)
                    put("questionId", r.questionId)
                    put("isCorrect", r.isCorrect)
                    put("durationMs", r.durationMs)
                    put("timestamp", r.timestamp)
                    put("anchorCode", r.anchorCode)
                    put("cognitiveLevel", r.cognitiveLevel)
                    put("signalQuality", r.signalQuality)
                    put("optionSwitches", r.optionSwitches)
                    put("answerChanges", r.answerChanges)
                })
            }
        })

        root.put("wrongAnswers", JSONArray().apply {
            db.wrongAnswerDao().dumpAll().forEach { w ->
                put(JSONObject().apply {
                    put("id", w.id)
                    put("questionId", w.questionId)
                    put("userId", w.userId)
                    put("userAnswer", w.userAnswer)
                    put("wrongCount", w.wrongCount)
                    put("lastWrongAt", w.lastWrongAt)
                    put("masterLevel", w.masterLevel)
                })
            }
        })

        root.put("mastery", JSONArray().apply {
            db.knowledgeMasteryDao().dumpAll().forEach { m ->
                put(JSONObject().apply {
                    put("id", m.id)
                    put("userId", m.userId)
                    put("knowledgePoint", m.knowledgePoint)
                    put("mastery", m.mastery)
                    put("lastPracticedAt", m.lastPracticedAt)
                })
            }
        })

        root.put("pointLogs", JSONArray().apply {
            db.pointLogDao().dumpAll().forEach { p ->
                put(JSONObject().apply {
                    put("id", p.id)
                    put("userId", p.userId)
                    put("points", p.points)
                    put("reason", p.reason)
                    put("timestamp", p.timestamp)
                })
            }
        })

        root.put("rewards", JSONArray().apply {
            db.rewardItemDao().listAll().forEach { x ->
                put(JSONObject().apply {
                    put("id", x.id)
                    put("name", x.name)
                    put("emoji", x.emoji)
                    put("points", x.points)
                    put("description", x.description)
                    put("enabled", x.enabled)
                    put("sortOrder", x.sortOrder)
                })
            }
        })

        // 锚点 × 层级状态属于用户学习数据，必须随备份迁移
        val anchorArr = JSONArray()
        db.childProfileDao().listAll().forEach { child ->
            db.anchorStateDao().listByUser(child.id).forEach { s ->
                anchorArr.put(JSONObject().apply {
                    put("userId", s.userId)
                    put("anchorCode", s.anchorCode)
                    put("cognitiveLevel", s.cognitiveLevel)
                    put("probability", s.probability.toDouble())
                    put("confidence", s.confidence)
                    put("status", s.status)
                    put("attempts", s.attempts)
                    put("correct", s.correct)
                    put("updatedAt", s.updatedAt)
                })
            }
        }
        root.put("anchorStates", anchorArr)

        return root.toString(2)
    }

    suspend fun restore(db: AppDatabase, text: String): ImportResult {
        val root = JSONObject(text)
        if (root.optString("app") != APP_TAG) {
            throw IllegalArgumentException("不是本应用导出的备份文件")
        }

        // 全量覆盖：先清空，再按原 id 写回，保持孩子与学习数据的关联
        db.learningRecordDao().clearAll()
        db.wrongAnswerDao().clearAll()
        db.knowledgeMasteryDao().clearAll()
        db.pointLogDao().clearAll()
        db.childProfileDao().clearAll()
        db.rewardItemDao().clearAll()
        db.anchorStateDao().clearAll()

        val childrenArr = root.optJSONArray("children") ?: JSONArray()
        for (i in 0 until childrenArr.length()) {
            val o = childrenArr.optJSONObject(i) ?: continue
            db.childProfileDao().insertOrUpdate(
                ChildProfile(
                    id = o.optLong("id", 0L),
                    name = o.optString("name", "小朋友"),
                    grade = o.optString("grade", "二年级"),
                    avatar = o.optString("avatar", "🧒"),
                    totalPoints = o.optLong("totalPoints", 0L),
                    currentLevel = o.optInt("currentLevel", 1),
                    stage = o.optString("stage", Stage.PRIMARY_LOW)
                )
            )
        }

        val recordsArr = root.optJSONArray("learningRecords") ?: JSONArray()
        for (i in 0 until recordsArr.length()) {
            val o = recordsArr.optJSONObject(i) ?: continue
            db.learningRecordDao().insert(
                LearningRecord(
                    id = o.optLong("id", 0L),
                    userId = o.optLong("userId", 1L),
                    questionId = o.optLong("questionId", 0L),
                    isCorrect = o.optBoolean("isCorrect", false),
                    durationMs = o.optLong("durationMs", 0L),
                    timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                    anchorCode = o.optString("anchorCode", ""),
                    cognitiveLevel = o.optString("cognitiveLevel", Question.LEVEL_MEMORY),
                    signalQuality = o.optString("signalQuality", "NORMAL"),
                    optionSwitches = o.optInt("optionSwitches", 0),
                    answerChanges = o.optInt("answerChanges", 0)
                )
            )
        }

        val wrongArr = root.optJSONArray("wrongAnswers") ?: JSONArray()
        for (i in 0 until wrongArr.length()) {
            val o = wrongArr.optJSONObject(i) ?: continue
            db.wrongAnswerDao().insertOrReplace(
                WrongAnswer(
                    id = o.optLong("id", 0L),
                    questionId = o.optLong("questionId", 0L),
                    userId = o.optLong("userId", 1L),
                    userAnswer = o.optString("userAnswer", ""),
                    wrongCount = o.optInt("wrongCount", 1),
                    lastWrongAt = o.optLong("lastWrongAt", System.currentTimeMillis()),
                    masterLevel = o.optInt("masterLevel", 0)
                )
            )
        }

        val masteryArr = root.optJSONArray("mastery") ?: JSONArray()
        for (i in 0 until masteryArr.length()) {
            val o = masteryArr.optJSONObject(i) ?: continue
            db.knowledgeMasteryDao().upsert(
                KnowledgeMastery(
                    id = o.optLong("id", 0L),
                    userId = o.optLong("userId", 1L),
                    knowledgePoint = o.optString("knowledgePoint", ""),
                    mastery = o.optInt("mastery", 50),
                    lastPracticedAt = o.optLong("lastPracticedAt", System.currentTimeMillis())
                )
            )
        }

        val pointArr = root.optJSONArray("pointLogs") ?: JSONArray()
        for (i in 0 until pointArr.length()) {
            val o = pointArr.optJSONObject(i) ?: continue
            db.pointLogDao().insert(
                PointLog(
                    id = o.optLong("id", 0L),
                    userId = o.optLong("userId", 1L),
                    points = o.optLong("points", 0L),
                    reason = o.optString("reason", ""),
                    timestamp = o.optLong("timestamp", System.currentTimeMillis())
                )
            )
        }

        val rewardArr = root.optJSONArray("rewards") ?: JSONArray()
        val rewards = mutableListOf<RewardItem>()
        for (i in 0 until rewardArr.length()) {
            val o = rewardArr.optJSONObject(i) ?: continue
            rewards += RewardItem(
                id = o.optLong("id", 0L),
                name = o.optString("name", ""),
                emoji = o.optString("emoji", "🎁"),
                points = o.optLong("points", 100L),
                description = o.optString("description", ""),
                enabled = o.optBoolean("enabled", true),
                sortOrder = o.optInt("sortOrder", 0)
            )
        }
        if (rewards.isNotEmpty()) db.rewardItemDao().insertAll(rewards)

        val anchorArr = root.optJSONArray("anchorStates") ?: JSONArray()
        for (i in 0 until anchorArr.length()) {
            val o = anchorArr.optJSONObject(i) ?: continue
            db.anchorStateDao().upsert(
                AnchorState(
                    userId = o.optLong("userId", 1L),
                    anchorCode = o.optString("anchorCode", ""),
                    cognitiveLevel = o.optString("cognitiveLevel", "MEMORY"),
                    probability = o.optDouble("probability", 0.3).toFloat(),
                    confidence = o.optString("confidence", "LOW"),
                    status = o.optString("status", "UNTOUCHED"),
                    attempts = o.optInt("attempts", 0),
                    correct = o.optInt("correct", 0),
                    updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                )
            )
        }

        return ImportResult(
            children = childrenArr.length(),
            records = recordsArr.length(),
            wrongs = wrongArr.length(),
            mastery = masteryArr.length(),
            points = pointArr.length(),
            rewards = rewards.size,
            anchors = anchorArr.length()
        )
    }
}
