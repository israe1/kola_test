package com.israel.kola.utils

import com.israel.kola.data.remote.User

object SingletonStore{
    var user: User? = null
    const val USER_TABLE = "users"
    const val GOAL_TABLE = "goals"
    const val USER_GOAL_TABLE = "user_goals"
    const val CONTRIBUTION_TABLE = "contributions"
}