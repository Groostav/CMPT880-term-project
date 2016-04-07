package ca.sfu.cs.porter

import org.junit.Test
import java.util.*

/**
 * Created by Geoff on 4/7/2016.
 */

@Test fun when_using_lambdas_and_method_refs(){


    val isNowImplicit = true
    val semanticListeners = arrayListOf("x1", "x2")

    semanticListeners.run{ if(isNowImplicit) { x:String -> add(x) } else { x:String -> remove(x) }}("Something")
    //not quite semanticListeners.run { if(isNowImplicit) ::add else ::remove } (listener)
    //see https://discuss.kotlinlang.org/t/method-refs/1586

}