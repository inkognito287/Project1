package com.example.qrreader.model

import com.example.qrreader.R

class SettingData {

    private val data = SettingItem(R.drawable.setting_data, "Данные")
    private val clearHistory = SettingItem(R.drawable.clear_history, "Очистить историю")
    private val exit = SettingItem(R.drawable.setting_exit, "Выйти")

    private val settingItems = SettingItems(data, clearHistory, exit)

    fun getSettingItems(): SettingItems {
        return settingItems
    }
}