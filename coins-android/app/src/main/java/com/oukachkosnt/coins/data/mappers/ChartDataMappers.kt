package com.oukachkosnt.coins.data.mappers

import java.util.*

fun List<List<String>>.parsePlotArgs()
        = map { Pair(Date(it[0].toLong()), it[1].toDouble()) }

fun List<List<Double>>.parseDoublePlotArgs()
        = map { Pair(Date(it[0].toLong()), it[1]) }