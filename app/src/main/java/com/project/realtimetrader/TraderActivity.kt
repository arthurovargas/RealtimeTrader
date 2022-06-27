package com.project.realtimetrader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.project.firestore.adapter.CryptosAdapter
import com.project.firestore.adapter.CryptosAdapterListener
import com.project.firestore.model.Crypto
import com.project.firestore.model.User
import com.project.firestore.model.network.Callback
import com.project.firestore.model.network.FirestoreService
import com.squareup.picasso.Picasso
import java.lang.Exception

class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    //Definimos el servicio
    lateinit var firestoreService: FirestoreService
    //Definimos el Adaptador
    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    private var username: String? = null

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)

        //Ya tenemos el servicio podemos hacer un llamado a nuestras funciones que implementamos
        // anteriormente para la lectura las cryptomonedas
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras?.get(USERNAME_KEY).toString()

        configureRecyclerView()
        loadCryptos()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            generateCryptoCurrenciesRandom()
        }

    }

    private fun generateCryptoCurrenciesRandom() {
        for (crypto in cryptosAdapter.cryptoList){
            val amount = (1..10).random()
            crypto.available += amount
            firestoreService.updateCrypto(crypto)
        }
    }

    private fun loadCryptos() {
        firestoreService.getCryptos(object: Callback<List<Crypto>>{
            override fun onSuccess(cryptoList: List<Crypto>?) {

                username?.let { s ->
                    firestoreService.finUserById(s, object: Callback<User>{
                        override fun onSuccess(result: User?) {
                            user = result
                            if (user?.cryptosList == null){
                                val userCryptoList = mutableListOf<Crypto>()

                                for (crypto in cryptoList!!) {
                                    val cryptoUser = Crypto()
                                    cryptoUser.name = crypto.name
                                    cryptoUser.available = crypto.available
                                    cryptoUser.imageUrl = crypto.imageUrl
                                    userCryptoList.add(cryptoUser)
                                }
                                user?.cryptosList = userCryptoList
                                user?.let { firestoreService.updateUser(it, null) }
                            }
                            loadUserCryptos()
                        }

                        override fun onFailed(exception: Exception) {
                            showGeneralServerErrorMessage()
                        }

                    })
                }
                this@TraderActivity.runOnUiThread{
                    if (cryptoList != null) {
                        cryptosAdapter.cryptoList = cryptoList
                        cryptosAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailed(exception: Exception) {
                Log.e("TraderActivity", "error loading cryptos", exception)
                showGeneralServerErrorMessage()
            }

        })
    }

    private fun loadUserCryptos() {
        runOnUiThread{
            if(user != null && user?.cryptosList != null){
                for (crypto in user?.cryptosList!!){
                    addUserCryptoInfoRow(crypto)
                }
            }
        }
    }

    private fun addUserCryptoInfoRow(crypto: Crypto){
        val infoPanel = findViewById<LinearLayout>(R.id.infoPanel)
        infoPanel.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text =
            getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)
    }

    private fun configureRecyclerView() {
        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = cryptosAdapter
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        if (crypto.available > 0 ){
            for (userCrypto in user?.cryptosList!!){
                if (userCrypto.name == crypto.name){
                    userCrypto.available += 1
                    break
                }
            }
            crypto.available --
            // Actualización de la compra en la base de datos
            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        }
    }

    fun showGeneralServerErrorMessage() {
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
        }
    }
}