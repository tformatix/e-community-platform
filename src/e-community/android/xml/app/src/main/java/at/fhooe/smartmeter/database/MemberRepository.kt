package at.fhooe.smartmeter.database

import at.fhooe.smartmeter.models.Member

class MemberRepository(private val memberDao: MemberDao) {

    fun insert(member: Member) {
        memberDao.insert(member)
    }
}