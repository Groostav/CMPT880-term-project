package ca.sfu.cs.porter

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.junit.runner.JUnitCore
import sun.plugin.dom.exception.InvalidStateException

/**
 * Created by Geoff on 4/3/2016.
 */
fun main(args: Array<String>){

    //multiple return values, probably my #1 feature for java (considering JEP 286)
    val (cmd, type) = CommandLineInterpreter().parse(args);

    JUnitAdapter().run(type);
}

class JUnitAdapter(){

    private val junit = JUnitCore();

    fun run(targetType : Class<*>){
        junit.run(targetType);
    }

}

class CommandLineInterpreter{

    private val options = Options();
    private val helpCmd = HelpFormatter();

    private val printHelp = { helpCmd.printHelp("java -jar porter", options) }

    constructor(){
        options.addOption(
                "s", "source-tests", true, "the source Junit4 test class"
        )
    }

    fun parse(args : Array<String>) : Pair<CommandLine, Class<*>> {
        val parser = DefaultParser();

        val cmd = parser.parse(options, args);

        val targetClass : String? = cmd.getOptionValue("source-tests")

        if(targetClass == null){
            printHelp();
            System.exit(1);
        }

        try{
            val targetType = Class.forName(targetClass)
            return Pair(cmd, targetType);
        }
        catch(exception : ClassNotFoundException){
            println("Couldn't find class: $targetClass")
            println("please make sure its fully canonical, and can be loaded with Class.forName()")
            System.exit(2)
            throw java.lang.RuntimeException()
        }
    }

}


