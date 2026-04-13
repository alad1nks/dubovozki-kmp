package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.HomeApi
import kotlinx.coroutines.flow.StateFlow

class HomeRepository(
    private val homeApi: HomeApi,
) {
    fun getItems(): StateFlow<List<String>> {
        return homeApi.getItems()
    }
}
