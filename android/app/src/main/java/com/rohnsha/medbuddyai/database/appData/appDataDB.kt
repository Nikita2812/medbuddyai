package com.rohnsha.medbuddyai.database.appData

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rohnsha.medbuddyai.database.appData.disease.diseaseDAO
import com.rohnsha.medbuddyai.database.appData.disease_questions.questionDAO
import com.rohnsha.medbuddyai.database.appData.disease_questions.questions
import com.rohnsha.medbuddyai.database.appData.doctors.doctor
import com.rohnsha.medbuddyai.database.appData.doctors.doctorsDAO
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomDAO
import com.rohnsha.medbuddyai.database.appData.symptoms.symptomDC
import com.rohnsha.medbuddyai.domain.dataclass.disease_data_dataClass

@Database(
    entities = [disease_data_dataClass::class, symptomDC::class, questions::class, doctor::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 2, to = 3)]
)
abstract class appDataDB: RoomDatabase() {

    abstract fun diseaseDAO(): diseaseDAO
    abstract fun symptomDAO(): symptomDAO
    abstract fun questionsDAO(): questionDAO
    abstract fun doctorsDAO(): doctorsDAO

    companion object{
        @Volatile
        private var INSTANCE: appDataDB? = null

        fun getAppDBReference(context: Context): appDataDB {
            val getUserDBtemp = INSTANCE
            if (getUserDBtemp!=null){
                return getUserDBtemp
            }
            synchronized(this){
                val instanceUserDB= Room.databaseBuilder(
                    context.applicationContext,
                    appDataDB::class.java,
                    "appDataDB"
                ).build()
                INSTANCE= instanceUserDB
                return instanceUserDB
            }
        }
    }

}