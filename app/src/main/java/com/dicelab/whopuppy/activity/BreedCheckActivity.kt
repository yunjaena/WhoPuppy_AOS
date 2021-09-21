package com.dicelab.whopuppy.activity

import android.graphics.Color
import android.os.Bundle
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.dicelab.whopuppy.R
import com.dicelab.whopuppy.base.activity.ViewBindingActivity
import com.dicelab.whopuppy.base.slideVisibility
import com.dicelab.whopuppy.data.entity.DogBreedAnalysis
import com.dicelab.whopuppy.databinding.ActivityBreedCheckBinding
import com.dicelab.whopuppy.util.showToast
import com.dicelab.whopuppy.viewmodel.BreedCheckViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BreedCheckActivity : ViewBindingActivity<ActivityBreedCheckBinding>() {
    override val layoutId: Int = R.layout.activity_breed_check
    private val breedCheckViewModel: BreedCheckViewModel by viewModel()
    lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL) ?: kotlin.run {
            finish()
            return
        }
        initView()
        initObserver()
        findBreed()
    }

    private fun initView() {
        setBaseAppBar(getString(R.string.breed_analysis))
        setBackKey()
        initDogImageView()
        initChartView()
    }

    private fun initDogImageView() {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_brown_dog)
            .into(binding.dogImageView)
    }

    private fun initChartView() {
        with(binding.dogBreedChart) {
            axisLeft.axisMaximum = 100f
            axisLeft.axisMinimum = -20f
            setDrawValueAboveBar(false)
            setFitBars(true)
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            legend.isEnabled = false
            setTouchEnabled(false)
        }
    }

    private fun initObserver() {
        with(breedCheckViewModel) {
            animalBreedInfo.observe(this@BreedCheckActivity) { dogItemBreedAnalysisList ->
                if (dogItemBreedAnalysisList.isEmpty()) return@observe
                val sortAnalysisList = dogItemBreedAnalysisList.sortedBy { it.similarity }
                setAppBarTitle(sortAnalysisList.last().breed)
                makeChart(sortAnalysisList)
                showChart()
            }

            animalBreedInfoFetchFailEvent.observe(this@BreedCheckActivity) {
                if (it != null)
                    showToast(it)
            }
        }
    }

    private fun findBreed() {
        breedCheckViewModel.findAnimalBreed(imageUrl)
    }

    private fun makeChart(dogItemBreedAnalysisList: List<DogBreedAnalysis>) {
        val yValues: ArrayList<BarEntry> = ArrayList()
        val xValues: ArrayList<String> = ArrayList()

        dogItemBreedAnalysisList.forEachIndexed { index, dogBreedAnalysis ->
            xValues.add(dogBreedAnalysis.breed)
            yValues.add(BarEntry(index.toFloat(), dogBreedAnalysis.similarity))
        }

        val dataSet = BarDataSet(yValues, "Dog")
        dataSet.setDrawValues(true)
        val data = BarData(dataSet)
        data.setValueFormatter(PercentFormatter())

        binding.dogBreedChart.data = data
        binding.dogBreedChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        binding.dogBreedChart.xAxis.labelCount = xValues.size
        dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.DKGRAY)
        binding.dogBreedChart.animateXY(1000, 1000)
    }

    private fun showChart() {
        binding.dogBreedChart.invalidate()
        binding.dogBreedChart.slideVisibility(true)
    }

    companion object {
        const val TAG = "BreedCheckActivity"
        const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
    }
}
