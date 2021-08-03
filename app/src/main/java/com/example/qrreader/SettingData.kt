package com.example.qrreader

class SettingData {

    private val data = SettingItem(R.drawable.setting_data, "Данные")
    private val exit = SettingItem(R.drawable.setting_exit, "Выйти")

    private val settingItems = SettingItems( data, exit)

   fun getSettingItems(): SettingItems {
        return settingItems
    }
}