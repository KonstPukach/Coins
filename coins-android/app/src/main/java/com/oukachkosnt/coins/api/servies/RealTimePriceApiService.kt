package com.oukachkosnt.coins.api.servies

import com.google.gson.Gson
import com.oukachkosnt.coins.data.api.RealTimePriceApiData
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.domain.RealTimePriceData
import com.oukachkosnt.coins.data.mappers.toDomainObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealTimePriceApiService(private val coin: CryptoCoinData) {
    private val deserializer = Gson()
    private val priceData = BehaviorSubject.createDefault(RealTimePriceData(coin.priceUsd))
    private val connectionState = BehaviorSubject.createDefault(false)
    private val error = BehaviorSubject.create<Any>()

    private val socket =
            IO.socket("wss://ws.coincap.io/prices?assets=bitcoin")
              .also {
                  it.on("message") { onTradesReceived(it.first()) }
                      .on(Socket.EVENT_CONNECT) { connectionState.onNext(true) }
                      .on(Socket.EVENT_DISCONNECT) { connectionState.onNext(false) }
                      .on(Socket.EVENT_CONNECT_ERROR) { error.onNext(Any()) }
              }

    private fun onTradesReceived(trades: Any) {
        val apiData = try {
            deserializer.fromJson(trades.toString(), RealTimePriceApiData::class.java)
        } catch (e : Exception) {
            error.onNext(Any())
            null
        }

        if (apiData?.msg?.short == coin.symbol) {
            priceData.onNext(apiData.toDomainObject())
        }
    }

    fun getPriceDataObservable(): Observable<RealTimePriceData> = priceData.observeOn(AndroidSchedulers.mainThread())

    fun getConnectionStateObservable(): Observable<Boolean> = connectionState.observeOn(AndroidSchedulers.mainThread())

    fun getErrorObservable(): Observable<Any> = error.observeOn(AndroidSchedulers.mainThread())

    fun connect() {
        if (!socket.connected()) {
            socket.connect()
        }
    }

    fun disconnect() {
        socket.disconnect()
    }
}