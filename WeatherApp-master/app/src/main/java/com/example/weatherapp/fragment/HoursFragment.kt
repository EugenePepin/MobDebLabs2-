package com.example.weatherapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.utils.WeatherData
import com.example.weatherapp.adapter.ListenerAdapter
import com.example.weatherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: ListenerAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateCurrentCard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCurrentCard()
    }

    private fun updateCurrentCard() {
        initRecyclerView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRecyclerView() = with(binding) {
        hoursRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ListenerAdapter(null)
        hoursRecyclerView.adapter = adapter
    }

    private fun getHoursList(weatherItem: WeatherData): List<WeatherData> {
        val hoursArray = JSONArray(weatherItem.hoursData)
        val list = ArrayList<WeatherData>()
        val startIndex = getStartIndex(weatherItem)
        
        for (i in startIndex until hoursArray.length()) {
            val item = WeatherData(
                weatherItem.cityNameData,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c").toFloat().toInt().toString(),
                (hoursArray[i] as JSONObject).getString("temp_f").toFloat().toInt().toString(),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                "",
                "",
                "",
                "",
                ""
            )
            list.add(item)
        }
        return list
    }

    private fun getStartIndex(weatherItem: WeatherData): Int {
        val locationTime = weatherItem.dateAndTimeData
        val locationDate = try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .parse(locationTime)
        } catch (e: Exception) {

            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(locationTime)
        }

        val hoursArray = JSONArray(weatherItem.hoursData)
        val firstHourTime = (hoursArray[0] as JSONObject).getString("time")
        val firstHourDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .parse(firstHourTime)

        val calendar = Calendar.getInstance()
        calendar.time = locationDate
        val locationDay = calendar.get(Calendar.DAY_OF_MONTH)
        val locationHour = calendar.get(Calendar.HOUR_OF_DAY)

        calendar.time = firstHourDate
        val firstHourDay = calendar.get(Calendar.DAY_OF_MONTH)
        
        return if (locationDay == firstHourDay) {
            locationHour
        } else {
            0
        }
    }

    companion object {
        @JvmStatic
        fun NewInstance() = HoursFragment()
    }
}
