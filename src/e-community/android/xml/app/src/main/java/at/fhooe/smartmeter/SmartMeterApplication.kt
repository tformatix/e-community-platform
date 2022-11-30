package at.fhooe.smartmeter

import android.app.Application
import at.fhooe.smartmeter.database.MemberRepository
import at.fhooe.smartmeter.database.NotificationRepository
import at.fhooe.smartmeter.database.SmartMeterDatabase
import at.fhooe.smartmeter.database.TileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SmartMeterApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { SmartMeterDatabase.getDatabase(this, applicationScope) }

    val memberRepository by lazy { MemberRepository(database.memberDao()) }
    val notificationRepository by lazy { NotificationRepository(database.notificationDao()) }
    val tileRepository by lazy { TileRepository(database.tileDao()) }
}
