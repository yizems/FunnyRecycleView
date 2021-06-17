package cn.yize.funny.recycleview.sample

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.Orientation
import cn.yize.funny.recycleview.layoutmanager.wheel.WheelLayoutManager

class MainActivity : AppCompatActivity() {

    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycleView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        WheelLayoutManager().attach(recyclerView)

        recyclerView.adapter = MyAdapter(this)


        recyclerView.postDelayed({
            recyclerView.smoothScrollToPosition(20)
        }, 3000)
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