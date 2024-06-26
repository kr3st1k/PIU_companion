package dev.kr3st1k.piucompanion.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.kr3st1k.piucompanion.core.db.dao.LatestScoresDao
import dev.kr3st1k.piucompanion.core.db.data.BestScore
import dev.kr3st1k.piucompanion.core.db.data.LatestScore

@Database(entities = [LatestScore::class, BestScore::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scoresDao(): LatestScoresDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
