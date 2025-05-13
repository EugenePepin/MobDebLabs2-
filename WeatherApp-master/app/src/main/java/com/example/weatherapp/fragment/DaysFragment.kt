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
import com.example.weatherapp.databinding.FragmentDaysBinding


class DaysFragment : Fragment(), ListenerAdapter.Listener {
    private lateinit var adapter: ListenerAdapter
    private lateinit var binding: FragmentDaysBinding
    private val dataModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    //оновлення даних при виході з вкладки Settings
    override fun onResume() {
        super.onResume()
        updateCurrentCard()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCurrentCard()
        }


    private fun updateCurrentCard(){
        init()
        dataModel.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it.subList(1, it.size))
        }
    }
    private fun init() = with(binding) {
        adapter = ListenerAdapter(this@DaysFragment)
        daysRecyclerView.layoutManager = LinearLayoutManager(activity)
        daysRecyclerView.adapter = adapter
    }

    companion object {

        @JvmStatic

        fun NewInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherData) {
        dataModel.liveDataCurrent.value = item
    }
}
