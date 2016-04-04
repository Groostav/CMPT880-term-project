package ca.sfu.cs.porter

/**
 * Created by Geoff on 4/3/2016.
 */
fun main(args: Array<String>){
    Porter().port(args);
}

class Porter{

    fun port(args : Array<String>){

        //multiple return values, probably my #1 feature for java (considering JEP 286)
        val testTarget = CommandLineInterpreter().parse(args);

        val generator = JunitDriverSourceGenerator();

        generator.generateSource(testTarget);
    }
}



