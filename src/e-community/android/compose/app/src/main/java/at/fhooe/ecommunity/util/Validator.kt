package at.fhooe.ecommunity.util

import android.util.Patterns

/**
 * different validators
 */
object Validator {
    /**
     * RegEx for the password guidelines
     */
    private val mPasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#\$^+=!*()@%&]).{6,}\$")

    /**
     * @param _email email address
     * @return is _email valid
     */
    fun validateEmail(_email: String) = Patterns.EMAIL_ADDRESS.matcher(_email).matches()

    /**
     * @param _text some text
     * @return is _text empty
     */
    fun validateNotEmpty(_text: String) = _text.isNotEmpty()

    /**
     * @param _password some password
     * @return does the password fulfill the guidelines
     */
    fun validatePassword(_password: String) = validateNotEmpty(_password) && mPasswordRegex.find(_password) != null
}