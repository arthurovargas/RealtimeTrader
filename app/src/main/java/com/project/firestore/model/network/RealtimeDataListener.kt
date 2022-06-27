package com.project.firestore.model.network

import java.lang.Exception

interface RealtimeDataListener<T> {

    fun onDataChange(updateData: T)
    fun onError(exception: Exception)
}