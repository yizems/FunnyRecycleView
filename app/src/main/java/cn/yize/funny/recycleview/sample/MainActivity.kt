package cn.yize.funny.recycleview.sample

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.widget.TextPickerRecycleView
import cn.yize.funny.recycleview.widget.datepicker.DateTimePickerView

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy {
        findViewById<TextPickerRecycleView>(R.id.recycleView)
    }

    private val dateTimePickerView by lazy {
        findViewById<DateTimePickerView>(R.id.date_time_picker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.addData((0..50).map { "哈哈哈哈::$it" })

//        recyclerView.adapter = MyAdapter(this)
//
//
//        recyclerView.postDelayed({
//            recyclerView.smoothScrollToPosition(1)
//        }, 3000)

        dateTimePickerView.onSelectedListener = {
            Log.e("MainActivity", it.formatStr)
            //            val (time, fieldMap, formatStr) = dateTimePickerView.getSelectedDateTime()
        }
    }
}

class MyAdapter(val context: Context) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            TextView(context)
                .apply {
                    setPadding(
                        20,
                        20,
                        20,
                        20,
                    )
                    textSize = 18F
                    gravity = Gravity.CENTER
                }
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        (holder.itemView as TextView).text = "哈哈哈哈哈$position"
    }

    override fun getItemCount(): Int {
        return 100
    }

}


class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)