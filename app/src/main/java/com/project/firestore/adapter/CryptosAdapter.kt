package com.project.firestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.firestore.model.Crypto
import com.project.realtimetrader.R
import com.squareup.picasso.Picasso

class CryptosAdapter(val cryptosAdapterListener: CryptosAdapterListener) : RecyclerView.Adapter<CryptosAdapter.ViewHolder> () {

    var cryptoList: List<Crypto> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto = cryptoList [position]
        //Actualizamos los valores de las Cryptomonedas
        Picasso.get().load(crypto.imageUrl).into(holder.image)
        holder.cryptoName.text = crypto.name
        holder.available.text = holder.itemView.context.getString(R.string.available_message, crypto.available.toString())
        holder.buyBotton.setOnClickListener {
            cryptosAdapterListener.onBuyCryptoClicked(crypto)
        }
    }

    override fun getItemCount(): Int {
        return cryptoList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        //Mapeamos los componente del "crypto_row"
        var image = view.findViewById<ImageView>(R.id.image)
        var cryptoName = view.findViewById<TextView>(R.id.nameTextView)
        var available = view.findViewById<TextView>(R.id.availableTextView)
        var buyBotton = view.findViewById<TextView>(R.id.buyButton)
    }
}