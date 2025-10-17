package com.bionica.visor_prueba3.ui.foro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bionica.visor_prueba3.R
import com.google.android.material.appbar.MaterialToolbar

class ForoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarForo)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
