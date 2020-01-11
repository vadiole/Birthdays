package vadiole.birthdays.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import vadiole.birthdays.models.Birthday

@TypeConverters(vadiole.birthdays.repository.TypeConverters::class)
@Database(entities = [Birthday::class], version = 1, exportSchema = false)
abstract class BirthdayRoomDatabase : RoomDatabase() {

    abstract fun birthdayDao(): BirthdayDao

//    private class BirthdayDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    val birthdayDao = database.birthdayDao()
//                    birthdayDao.deleteAll()
//                    val list = listOf(
//                        Birthday("Dmitriy @vadiole",    LocalDate.of(2000, 11, 8),    getYearsOld( LocalDate.of(2000, 11, 8),  LocalDate.now())  ),
//                        Birthday("Sasha",               LocalDate.of(2001, 1, 18),    getYearsOld( LocalDate.of(2001, 1, 18),  LocalDate.now())  ),
//                        Birthday("Alexandr",            LocalDate.of(2000, 11, 21),   getYearsOld( LocalDate.of(2000, 11, 21), LocalDate.now())  ),
//                        Birthday("Nikolay Levtik",      LocalDate.of(1960, 3, 23),    getYearsOld( LocalDate.of(1960, 3, 23),  LocalDate.now())  ),
//                        Birthday("Platon Rimskiy",      LocalDate.of(1988, 4, 8),     getYearsOld( LocalDate.of(1988, 4, 8),   LocalDate.now())  ),
//                        Birthday("Leonardo Davinchi",   LocalDate.of(1500, 12, 12),   getYearsOld( LocalDate.of(1500, 12, 12), LocalDate.now())  ),
//                        Birthday("Nikola Tesla",        LocalDate.of(1913, 9, 13),    getYearsOld( LocalDate.of(1913, 9, 13),  LocalDate.now())  ),
//                        Birthday("User User",           LocalDate.of(1980, 5, 15),    getYearsOld( LocalDate.of(1980, 5, 15),  LocalDate.now())  ),
//                        Birthday("Name Soname",         LocalDate.of(1990, 7, 8),     getYearsOld( LocalDate.of(1990, 7, 8),   LocalDate.now())  ),
//                        Birthday("Test Test",           LocalDate.of(1950, 3, 3),     getYearsOld( LocalDate.of(1950, 3, 3),   LocalDate.now())  ),
//                        Birthday("Sherlock Holmes",     LocalDate.of(1999, 2, 18),    getYearsOld( LocalDate.of(1999, 2, 18),  LocalDate.now())  ),
//                        Birthday("Hello World",         LocalDate.of(2008, 10, 19),   getYearsOld( LocalDate.of(2008, 10, 19), LocalDate.now())  ),
//                        Birthday("Birthday",            LocalDate.of(2005, 7, 5),     getYearsOld( LocalDate.of(2005, 7, 5),   LocalDate.now())  ),
//                        Birthday("Last Element",        LocalDate.of(2006, 6, 17),    getYearsOld( LocalDate.of(2006, 6, 17),  LocalDate.now())  )
//                    )
//                    birthdayDao.insert(list)
//                }
//            }
//        }
//
//        fun getYearsOld(dataOdBirthday: LocalDate, dateNow: LocalDate): Int {
//            val years = ChronoUnit.YEARS.between(dataOdBirthday, dateNow).toInt()
//            return if ((dataOdBirthday.dayOfMonth == dateNow.dayOfMonth) and (dataOdBirthday.monthValue == dateNow.monthValue)) years else years + 1
//        }
//    }

    companion object {
        @Volatile
        private var INSTANCE: BirthdayRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
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
                    // .addCallback(BirthdayDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }


}
