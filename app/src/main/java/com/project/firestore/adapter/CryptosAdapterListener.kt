package com.project.firestore.adapter

import com.project.firestore.model.Crypto

interface CryptosAdapterListener {

    fun onBuyCryptoClicked(crypto:Crypto)

}