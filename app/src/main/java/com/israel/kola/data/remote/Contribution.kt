package com.israel.kola.data.remote

import com.google.firebase.firestore.Exclude

data class Contribution(
    val id: String?,
    val userGoalId: String?,
    var amount: String?,
    val date: String?,
    @Exclude val user: User?
)