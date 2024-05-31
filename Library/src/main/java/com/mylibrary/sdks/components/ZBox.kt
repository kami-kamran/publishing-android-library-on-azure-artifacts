package com.mylibrary.sdks.components

import android.widget.TextView

object ZBox {
    fun test() {
        println("test")
    }
    fun String.testView(view: TextView){
        view.text = this
    }
    fun newMethod(){
        println("This is test")
    }
}