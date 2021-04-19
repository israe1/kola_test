package com.israel.kola.models

class Contribution {
    var id: Int = 0
    var amount: Int = 0
    var contributorId: Int = 0

    constructor(id: Int, amount: Int, contributorId: Int) {
        this.id = id
        this.amount = amount
        this.contributorId = contributorId
    }
}