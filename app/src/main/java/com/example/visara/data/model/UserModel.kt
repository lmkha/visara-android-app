package com.example.visara.data.model

data class UserModel(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
) {
    override fun toString(): String {
        return "[UserModel] id = $id, username = $username, email = $email, phone = $phone"
    }
}
