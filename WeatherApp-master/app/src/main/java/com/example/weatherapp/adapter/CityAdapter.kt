package com.example.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ListItemCityFavoritesBinding
import com.example.weatherapp.utils.CityData

class CityAdapter(
    private val cities: List<CityData>,
    private val onCityClick: (CityData) -> Unit,
    private val onDeleteClick: (CityData) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(val binding: ListItemCityFavoritesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ListItemCityFavoritesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.binding.apply {
            cityName.text = city.name
            deleteButton.visibility = if (city.isCurrentLocation) View.GONE else View.VISIBLE
            deleteButton.setOnClickListener { onDeleteClick(city) }
            root.setOnClickListener { onCityClick(city) }
        }
    }

    override fun getItemCount() = cities.size
} 