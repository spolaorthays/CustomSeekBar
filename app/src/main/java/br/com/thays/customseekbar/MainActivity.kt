package br.com.thays.customseekbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectorColor.addListener { color ->
            //linearLayout.setBackgroundColor(color)
            frameColor.setBackgroundColor(color)
        }
    }

}