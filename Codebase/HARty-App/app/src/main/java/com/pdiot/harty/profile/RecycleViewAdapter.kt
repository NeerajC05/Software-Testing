package com.pdiot.harty.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.pdiot.harty.R
import com.pdiot.harty.utils.MinutesHelper

/* This Kotlin class handles the historic data to be displayes. */
class RecycleViewAdapter(private val dataList : ArrayList<HistoricData>) : RecyclerView.Adapter<RecycleViewAdapter.RecycleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.segment_historic_activity_tile, parent, false)
        return RecycleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val currentItem = dataList[position]
        val totalTime = currentItem.sittingTime!! + currentItem.standingTime!! + currentItem.walkingTime!! + currentItem.runningTime!! + currentItem.lyingTime!! + currentItem.generalTime!! + currentItem.stairsTime!! + currentItem.sittingStandingTime!!
        holder.dateText.text = currentItem.date
        holder.stepText.text = currentItem.steps
        holder.activityTimeText.text = MinutesHelper.convertToString(totalTime)

        val entries: ArrayList<PieEntry> = ArrayList()
        checkFor0(entries, currentItem.sittingTime!!, "Sitting")
        checkFor0(entries, currentItem.standingTime!!, "Standing")
        checkFor0(entries, currentItem.walkingTime!!, "Walking")
        checkFor0(entries, currentItem.runningTime!!, "Running")
        checkFor0(entries, currentItem.lyingTime!!, "Lying")
        checkFor0(entries, currentItem.generalTime!!, "General")
        checkFor0(entries, currentItem.stairsTime!!, "Stairs")
        checkFor0(entries, currentItem.sittingStandingTime!!, "Sitting/Standing")

        drawPie(entries, holder)
    }

    //Checks for zero values
    private fun checkFor0(entries : ArrayList<PieEntry>, time : Int, label : String) {
        if (time != 0) {
            entries.add(PieEntry(time.toFloat(), label))
        }
    }

    //Draws the pie chart
    private fun drawPie(entries : ArrayList<PieEntry>, holder : RecycleViewHolder) {
        holder.pieChart.isDrawHoleEnabled = true
        holder.pieChart.setUsePercentValues(true)
        holder.pieChart.setEntryLabelTextSize(12F)
        holder.pieChart.setEntryLabelColor(R.color.black)
        holder.pieChart.description.isEnabled = false

        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = PieDataSet(entries,"")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(holder.pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(R.color.black)
        holder.pieChart.setData(data)
        holder.pieChart.invalidate()

        holder.pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class RecycleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pieChart : PieChart = itemView.findViewById(R.id.pieChart)
        val dateText : TextView = itemView.findViewById(R.id.dateText)
        val stepText : TextView = itemView.findViewById(R.id.stepsText)
        val activityTimeText : TextView = itemView.findViewById(R.id.activityTimeText)
    }
}