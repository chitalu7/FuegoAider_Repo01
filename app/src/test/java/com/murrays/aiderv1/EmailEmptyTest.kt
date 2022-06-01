package com.murrays.aiderv1


import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EmailEmptyTest{


    @Test
    fun whenEmailInputIsValid(){
        val email = ""
        val result = TestValidator.validateEmailInput(email)
        assertThat(result).isEqualTo(true)

    }
}