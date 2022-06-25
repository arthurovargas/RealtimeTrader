package com.project.realtimetrader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.project.firestore.adapter.CryptosAdapter
import com.project.firestore.adapter.CryptosAdapterListener
import com.project.firestore.model.Crypto
import com.project.firestore.model.network.Callback
import com.project.firestore.model.network.FirestoreService
import java.lang.Exception

class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    //Definimos el servicio
    lateinit var firestoreService: FirestoreService
    //Definimos el Adaptador
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)

        //Ya tenemos el servicio podemos hacer un llamado a nuestras funciones que implementamos
        // anteriormente para la lectura las cryptomonedas
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        configureRecyclerView()
        loadCryptos()

    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object: Callback<List<Crypto>>{
            override fun onSuccess(result: List<Crypto>?) {
                this@TraderActivity.runOnUiThread{
                    if (result != null) {
                        cryptosAdapter.cryptoList = result
                        cryptosAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailed(exception: Exception) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun configureRecyclerView() {
        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        TODO("Not yet implemented")
    }
}