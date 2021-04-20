package com.israel.kola.models

class Transaction {
    var id: String
    var state: TransactionState
    var amount: Int

    constructor(id: String, state: TransactionState, amount: Int) {
        this.id = id
        this.state = state
        this.amount = amount
    }
}