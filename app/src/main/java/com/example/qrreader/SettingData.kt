package com.example.qrreader

class SettingData {
    private val securePolicy =
        SettingItem(R.drawable.setting_politicy_secure, "Политика безопасности")
    private val data = SettingItem(R.drawable.setting_data, "Данные")
    private val exit = SettingItem(R.drawable.setting_exit, "Выйти")

    private val settingItems = SettingItems(securePolicy, data, exit)

   fun getSettingItems(): SettingItems {
        return settingItems
    }
}