package com.mask.sliceviewdemo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.toast
import com.mask.sliceviewdemo.ext.bind
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mActivity : AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mActivity = this
        etSliceUri.setText("content://com.example.android.app/temperature")
        btnShowSlice.setOnClickListener {
            if(etSliceUri.text.isEmpty()){
                toast("Enter uri")
                return@setOnClickListener
            }
            val uri = etSliceUri.text.toString().toUri()
            sliceView.bind(mActivity, mActivity, uri)
        }
    }
}
