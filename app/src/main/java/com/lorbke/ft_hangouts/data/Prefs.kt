package com.lorbke.ft_hangouts.data

import android.content.Context
import android.graphics.Color

// A tiny persistent key/value store for simple app-wide settings. Unlike
// SQLite there are no tables or SQL - just named values that survive app
// restarts, backed by a small XML file the OS manages for us.
object Prefs {

    private const val PREFS_NAME = "ft_hangouts_prefs"
    private const val KEY_HEADER_COLOR = "header_color"
    private val DEFAULT_HEADER_COLOR = Color.parseColor("#6750A4")

    fun getHeaderColor(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_HEADER_COLOR, DEFAULT_HEADER_COLOR)
    }

    fun setHeaderColor(context: Context, color: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_HEADER_COLOR, color).apply()
    }
}
