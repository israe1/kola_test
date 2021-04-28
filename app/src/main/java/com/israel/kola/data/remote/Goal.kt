package com.israel.kola.data.remote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Goal(
    var id: String?,
    var name: String?,
    var amount: String?,
    var date: String?,
    var description: String?,
    var imagePath: String?,
    var numberOfMembers: String?
): Parcelable