package vadiole.birthdays.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import vadiole.birthdays.models.Birthday


@TypeConverters(vadiole.birthdays.repository.TypeConverters::class)
@Database(entities = [Birthday::class], version = 2, exportSchema = false)
abstract class BirthdayRoomDatabase : RoomDatabase() {

    abstract fun birthdayDao(): BirthdayDao

    companion object {
        @Volatile
        private var INSTANCE: BirthdayRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): BirthdayRoomDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BirthdayRoomDatabase::class.java,
                    "birthday_database"
                )
                    // .addMigrations(MIGRATION1to2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
//migration
//    object MIGRATION1to2 : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE birthday ADD COLUMN gifts TEXT")
//        }
//    }
}

