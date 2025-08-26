package com.example.airesume.utils

import android.content.Context
import androidx.room.Room
import com.example.airesume.data.local.ResumeDatabase

fun getDatabase(context: Context): ResumeDatabase {
    return Room.databaseBuilder(
        context,
        ResumeDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()
}