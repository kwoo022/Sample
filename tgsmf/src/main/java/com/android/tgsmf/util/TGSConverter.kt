package com.android.tgsmf.util

object TGSConverter {

    inline fun <T>arrayToArrayList(_array:Array<T>):ArrayList<T> {
        return _array.toCollection(ArrayList<T>())
    }

    inline fun <T>arrayToList(_array:Array<T>):List<T> {
        return _array.toList()
    }

    inline fun <reified T>arrayListToArray(_arrayList :ArrayList<T>) : Array<T> {
        return _arrayList.toTypedArray()
    }

    inline fun <reified T>listToArray(_list :List<T>) : Array<T> {
        return _list.toTypedArray()
    }


}