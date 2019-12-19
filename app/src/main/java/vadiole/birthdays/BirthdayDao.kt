package vadiole.birthdays

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface BirthdayDao {

//    @Query("SELECT * FROM birthday ORDER BY name")
    @Query("select * from birthday order by CAST(strftime('%m', birthdayDate) AS INTEGER), CAST(strftime('%d', birthdayDate) AS INTEGER)")
    fun getAll(): LiveData<List<Birthday>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(birthday: Birthday)

    @Insert
    suspend fun insert(list: List<Birthday>)

    @Update
    suspend fun update(birthday: Birthday)

    @Delete
    suspend fun delete(birthday: Birthday)

    @Query("DELETE FROM birthday")
    suspend fun deleteAll()

}
