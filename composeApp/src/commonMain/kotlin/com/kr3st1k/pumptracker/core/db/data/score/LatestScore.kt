package com.kr3st1k.pumptracker.core.db.data.score

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "latest_scores")
data class LatestScore(
    override val songName: String,
    override var songBackgroundUri: String?,
    override val difficulty: String,
    override val score: String,
    override val rank: String,
    @PrimaryKey @ColumnInfo(name = "hash") val hash: String,
    @ColumnInfo(name = "datetime") val datetime: String,
) : Score(songName, songBackgroundUri, difficulty, score, rank)

