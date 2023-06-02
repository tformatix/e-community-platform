package at.fhooe.ecommunity

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.fhooe.ecommunity.util.Validator

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ValidatorTest {
    @Test
    fun password() {
        assertEquals(false, Validator.validatePassword("asdfghjklöä"))
        assertEquals(false, Validator.validatePassword("Asdfghjklöä"))
        assertEquals(false, Validator.validatePassword("Asdfghjklöä1"))
        assertEquals(false, Validator.validatePassword("Asöä1"))
        assertEquals(true, Validator.validatePassword("Asdfghjklöä1!"))
    }
}