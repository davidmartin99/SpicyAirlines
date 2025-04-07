package com.spicyairlines.app.model

data class CombinacionVuelos(
    val vueloIda: Vuelo,
    val vueloVuelta: Vuelo? = null // null si solo es vuelo de ida
)
