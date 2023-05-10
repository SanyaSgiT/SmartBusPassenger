package com.example.smartbuspassenger.ui.routeList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartbuspassenger.R
import com.example.smartbuspassenger.data.repository.RoutesRepository
import com.example.smartbuspassenger.domain.Route

class RouteAdapter(
    private val onRouteClicked: (Route) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    private var items: List<Route> = emptyList()

    fun updateList(newItems: List<Route>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder(
            LayoutInflater
                .from(parent.context).inflate(R.layout.item_route, parent, false),
            onRouteClicked
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class RouteViewHolder(itemView: View, private val onRouteClicked: (Route) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(route: Route) {
            itemView.setOnClickListener {
                onRouteClicked(route)
            }

            itemView.findViewById<TextView>(R.id.routeName).text = route.name

//            val imageView = itemView.findViewById<ImageView>(R.id.image)
//
//            Glide
//                .with(imageView)
//                .load(book.imageUrl)
//                .into(imageView)
        }
    }

}