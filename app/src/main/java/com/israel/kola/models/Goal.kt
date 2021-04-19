package com.israel.kola.models

import android.os.Parcel
import android.os.Parcelable

class Goal() : Parcelable {
    var id: Int = 0
    var amount: Int = 0
    var numberOfMembers: Int = 0
    var name: String? = ""
    var dueDate: String? = ""
    var description: String? = ""
    var imagePath: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        amount = parcel.readInt()
        numberOfMembers = parcel.readInt()
        name = parcel.readString()
        dueDate = parcel.readString()
        description = parcel.readString()
        imagePath = parcel.readString()
    }

    constructor(
        id: Int,
        goalAmount: Int,
        numberOfMembers: Int,
        name: String,
        dueDate: String,
        description: String,
        imagePath: String
    ) : this() {
        this.id = id
        this.amount = goalAmount
        this.numberOfMembers = numberOfMembers
        this.name = name
        this.dueDate = dueDate
        this.description = description
        this.imagePath = imagePath
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(amount)
        parcel.writeInt(numberOfMembers)
        parcel.writeString(name)
        parcel.writeString(dueDate)
        parcel.writeString(description)
        parcel.writeString(imagePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Goal> {
        override fun createFromParcel(parcel: Parcel): Goal {
            return Goal(parcel)
        }

        override fun newArray(size: Int): Array<Goal?> {
            return arrayOfNulls(size)
        }
    }
}