package at.fhooe.smartmeter.database

import androidx.room.*
import at.fhooe.smartmeter.models.Member

@Dao
interface MemberDao {
    @Query("SELECT * FROM member")
    fun getMember(): Member

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(member: Member)

    @Delete
    fun delete(member: Member)

    @Update
    fun update(member: Member)

    @Query("DELETE FROM member")
    fun deleteMemberTable()
}