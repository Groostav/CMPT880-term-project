package ca.sfu.cs.porter

import org.apache.commons.cli.*
import org.stringtemplate.v4.ST
import org.stringtemplate.v4.misc.ErrorBuffer
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * Created by Geoff on 4/3/2016.
 */
fun main(args: Array<String>){
    Porter().port(args);
}

class Porter{

    fun port(args : Array<String>){

        //multiple return values, probably my #1 feature for java (considering JEP 286)
        val (testClass, testMethod, targetPath) = CommandLineInterpreter().parse(args);

        val generator = JunitDriverSourceGenerator();

        generator.generateSource(testClass, testMethod, targetPath);
    }
}

class JunitDriverSourceGenerator {

    val templatePath = Paths.get(JunitDriverSourceGenerator::class.java.getResource("driver.st").toURI());

    fun generateSource(sourceClass : Class<*> , sourceMethod : String, target : Path){

        val content = try { Scanner(templatePath.toFile()).useDelimiter("\\Z").next(); }
        catch(e : FileNotFoundException) { System.exit(4); throw RuntimeException() }

        val template = ST(content)

        template.apply {
            add("sourceClass", sourceClass)
            add("sourceMethod", sourceMethod)
        }

        val result = template.render();

        template.write(target.toFile(), ErrorBuffer())
    }
}

class CommandLineInterpreter{

    private val options = Options();
    private val helpCmd = HelpFormatter();

    private val printHelp = { helpCmd.printHelp("java -jar porter.jar", options) }

    constructor(){

        val optionSpecs : Set<Triple<String, String, String>> = hashSetOf(
                Triple("s", "source-class",     "the source Junit4 test class's canonical class name"),
                Triple("m", "source-method",    "the name of the test method to wrap"),
                Triple("t", "target-java-file", "the path to the source file to generate, including file name")
        );

        for((opt, longOpt, description) in optionSpecs){
            val option = Option
                    .builder(opt).longOpt(longOpt)
                    .required().hasArg()
                    .desc(description)
                    .build()

            options.addOption(option)
        }
    }

    fun parse(args : Array<String>) : Triple<Class<*>, String, Path> {
        val parser = DefaultParser();

        val cmd = try {
            parser.parse(options, args);
        }
        catch(e : MissingOptionException){
            println(e.message)
            printHelp()
            System.exit(1);
            throw RuntimeException()
        }

        val sourceMethod = cmd.getOptionValue("source-method")
        val sourceClass : String? = cmd.getOptionValue("source-class")

        val targetType = try{
            Class.forName(sourceClass)
        }
        catch(exception : ClassNotFoundException){
            println("Couldn't find class: $sourceClass")
            println("please make sure its fully canonical, and can be loaded with Class.forName()")
            println("(did you forget to include it in the classpath?)")
            exception.printStackTrace();
            System.exit(2)
            throw RuntimeException();
        }

        try{
            targetType.getMethod(sourceMethod)
        }
        catch(exception : NoSuchMethodException){
            println("Couldn't find test method: $sourceMethod (ie $sourceClass#$sourceMethod)")
            println("please make sure it's spelled correctly, and that it is a zero-arg 'test' method")
            exception.printStackTrace();
            System.exit(3)
        }

        val targetJavaFile = try{
            val specifiedTargetPath = cmd.getOptionValue("target-java-file")
            val candidateTargetJavaFile = Paths.get(specifiedTargetPath)
            if( ! Files.isDirectory(candidateTargetJavaFile.getParent())) { throw InvalidPathException(specifiedTargetPath, "Parent directory does not exist") }
            if( ! candidateTargetJavaFile.fileName.toString().endsWith(".java")) { throw InvalidPathException(specifiedTargetPath, "Not a *.java file") }

            candidateTargetJavaFile
        }
        catch(exception : InvalidPathException){
            println(exception.message)
            exception.printStackTrace()
            System.exit(4)
            throw RuntimeException()
        }

        return Triple(targetType, sourceMethod, targetJavaFile);
    }

}


