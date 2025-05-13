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


class HoursFragment : Fragment() {
    private val sharedViewModel: MainFragment.SharedViewModel by activityViewModels()
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
    private fun updateCurrentCard(){
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

    private val dataModel: MainViewModel by activityViewModels()

    //перевірка, чи потрібно змінювати кількість элементів hour, залежно від вибраного дня
    private fun checkIndexList(): Int {
        var startIndex = 0
        val lastUpdated = sharedViewModel.lastUpdated
        val currentData = dataModel.liveDataCurrent.value

        if (currentData != null) {

            val inputDateFormat = SimpleDateFormat("dd/mm/yyyy")
            val inputDate = inputDateFormat.parse(
                currentData.dateAndTimeData.substringBefore(" ").replace("-", "/")
                    .split("/").reversed().joinToString("/")
            )
            val outputDateFormat = SimpleDateFormat("dd")
            val outputDate = outputDateFormat.format(inputDate).toInt()

            val calendar = Calendar.getInstance()
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val inputFormatHour = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val outputFormatHour = SimpleDateFormat("HH")
            val date = inputFormatHour.parse(lastUpdated)
            val currentHour = outputFormatHour.format(date).toInt()

            startIndex = if (dayOfMonth != outputDate) {
                0
            } else {
                currentHour
            }
        }
        return startIndex
    }


    //заповнення вкладки "Hours"
    private fun getHoursList(weatherItem: WeatherData): List<WeatherData> {
        val hoursArray = JSONArray(weatherItem.hoursData)
        val list = ArrayList<WeatherData>()
        val startIndex = checkIndexList()
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


    companion object {

        @JvmStatic

        fun NewInstance() = HoursFragment()
    }
}
