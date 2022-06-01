package com.murrays.aiderv1

object TestValidator {
    fun validateEmailInput(email: String): Boolean{
return !(email.isEmpty())
    }
}