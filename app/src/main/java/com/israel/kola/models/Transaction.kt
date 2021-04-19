package com.israel.kola.models

class Transaction {
    var id: Int
    var state: TransactionState
    var amount: Int

    constructor(id: Int, state: TransactionState, amount: Int) {
        this.id = id
        this.state = state
        this.amount = amount
    }
}