
package com.sogoamobile.farmersurvey.database

import android.content.Context
import androidx.room.*

@Database(entities = [FarmerSurveyTable::class, QuestionsTable::class,
    OptionsTable::class, StringsTable::class], version = 1, exportSchema = false)

abstract class SurveyDatabase : RoomDatabase() {

    abstract fun surveyDao(): FarmerSurveyDao

    companion object {
        @Volatile
        private var INSTANCE: SurveyDatabase? = null

        fun getDatabase(context: Context): SurveyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SurveyDatabase::class.java,
                    "survey_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}