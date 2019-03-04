package com.werockstar.withkotlinpoet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.werockstar.annotation.Generate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generatePath = GeneratePath(BuildConfig.DEBUG)
        Log.d("Path", generatePath.getPath("login"))
    }

    @Generate(path = "path")
    fun path(path: String) {
    }
}
