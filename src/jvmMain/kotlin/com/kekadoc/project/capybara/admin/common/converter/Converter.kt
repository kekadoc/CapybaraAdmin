package com.kekadoc.project.capybara.server.common.converter

interface Converter<T1, T2> {

    fun convert(value: T1): T2

    interface Bidirectional<T1, T2> : Converter<T1, T2> {

        fun revert(value: T2): T1

    }

}

fun <T1, T2> Converter<T1, T2>.convertOrNull(value: T1?): T2? = value?.let(::convert)

fun <T1, T2> Converter.Bidirectional<T1, T2>.revertOrNull(value: T2?): T1? = value?.let(::revert)

fun <T1, T2> T1.convert(converter: Converter<T1, T2>): T2 = converter.convert(this)

fun <T1, T2> T2.revert(converter: Converter.Bidirectional<T1, T2>): T1 = converter.revert(this)

fun <T1, T2> Iterable<T1>.convertAll(converter: Converter<T1, T2>): List<T2> = map { value -> value.convert(converter) }

fun <T1, T2> Iterable<T2>.revertAll(converter: Converter.Bidirectional<T1, T2>): List<T1> = map { value -> value.revert(converter) }