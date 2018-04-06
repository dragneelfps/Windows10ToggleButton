package com.nooblabs.windows10togglebutton

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        win_toggle_btn.setStateChangedListener(object : WinToggleButton.StateChanedListener {
            override fun onToggle(isOn: Boolean) {
                Log.d("debug","State changed to $isOn")
            }
        })
    }
}
