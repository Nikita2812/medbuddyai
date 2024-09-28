package com.rohnsha.medbuddyai.database.userdata

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.chats.chatEntity
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageDAO
import com.rohnsha.medbuddyai.database.userdata.chatbot.messages.messageEntity
import com.rohnsha.medbuddyai.database.userdata.communityTable.Post
import com.rohnsha.medbuddyai.database.userdata.communityTable.Reply
import com.rohnsha.medbuddyai.database.userdata.communityTable.communityDAO
import com.rohnsha.medbuddyai.database.userdata.currentUser.currentUserDAO
import com.rohnsha.medbuddyai.database.userdata.currentUser.fieldValueDC
import com.rohnsha.medbuddyai.database.userdata.keys.keyDAO
import com.rohnsha.medbuddyai.database.userdata.keys.keyDC
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistory
import com.rohnsha.medbuddyai.database.userdata.scan_history.scanHistoryDAO

@Database(
    entities = [
        scanHistory::class, chatEntity::class, messageEntity::class,
        fieldValueDC::class, Post::class, Reply::class, keyDC::class
               ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5)
    ]
)
abstract class userDataDB: RoomDatabase() {

    abstract fun scanDAO(): scanHistoryDAO
    abstract fun chatDA0(): chatDAO
    abstract fun messageDAO(): messageDAO
    abstract fun currentUserDAO(): currentUserDAO
    abstract fun communityDAO(): communityDAO
    abstract fun keyDAO(): keyDAO

    companion object{
        @Volatile
        private var INSTANCE: userDataDB? = null

        fun getUserDBRefence(context: Context): userDataDB {
            val getUserDBtemp = INSTANCE
            if (getUserDBtemp!=null){
                return getUserDBtemp
            }
            synchronized(this){
                val instanceUserDB= Room.databaseBuilder(
                    context.applicationContext,
                    userDataDB::class.java,
                    "userDB"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_X_Y)
                    .build()
                INSTANCE= instanceUserDB
                return instanceUserDB
            }
        }
    }
}

@DeleteTable(
    tableName = "intFastingTable"
)
class AutoMig23: AutoMigrationSpec

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE messages ADD COLUMN hasAttachments INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_X_Y = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE scan_history ADD COLUMN userIndex INTEGER NOT NULL DEFAULT 2147483647")
    }
}