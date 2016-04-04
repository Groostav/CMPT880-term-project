package ca.sfu.cs.porter

import org.apache.commons.cli.*
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

class CommandLineInterpreter{

    companion object {
        const val InsufficientArgumentsCode = 1
        const val InvalidOutputDirCode = 3
        const val InvalidSourcePathCode = 4
    }

    data class OptionSpec(
            val shortOpt : Char,
            val longOpt : String,
            val required : Boolean,
            val description : String
    ){}

    private val options = Options();
    private val helpCmd = HelpFormatter();

    private val printHelp = { helpCmd.printHelp("java -jar porter.jar", options) }

    constructor(){

        val optionSpecs : Set<OptionSpec> = hashSetOf(
                OptionSpec('f', "source-file", true, "the path to the source file for the Junit4 test"),
                OptionSpec('m', "source-method", true, "the name of the test method to wrap"),
                OptionSpec('p', "package-name", false, "the package name for the corresponding file, can be inferred by convention"),
                OptionSpec('t', "output-dir", false, "the directory to put the generated code")
        );

        for(optSpec in optionSpecs) optSpec.apply {
            val option = Option
                    .builder("" + shortOpt).longOpt(longOpt)
                    .required(required)
                    .desc(description)
                    .hasArg().build()

            options.addOption(option)
        }
    }

    fun parse(args : Array<String>) : TestDescriptor {
        val parser = DefaultParser();

        val cmd = makeCommandLineFrom(args, parser)

        val sourceMethod = cmd.getOptionValue("source-method")
        val sourceFileSpecified = cmd.getOptionValue("source-file")

        val sourceFile = makeSourceFile(sourceFileSpecified);

        val packageName = cmd.getOptionValue("package-name") ?: inferPackageName(sourceFile);

        val outputDir = if (cmd.hasOption("output-dir")) makeOutputDir(cmd.getOptionValue("output-dir")) else inferOuputDir(sourceFile, packageName)

        return TestDescriptor(
                testSource = sourceFile,
                testClass = sourceFile.fileName.toString().replace(".java", ""),
                testMethod = sourceMethod,
                packageName = packageName,
                outputDir = outputDir
        );
    }

    private fun inferOuputDir(sourceFile: Path, packageName : String): Path {
        return sourceFile.subpath(0, sourceFile.nameCount - packageName.split(".").size - 1)
    }

    private fun inferPackageName(javaSourceFile : Path) : String {
        val commonPrefixes = hashSetOf("org", "com", "ca", "net")

        var currentPath : Path? = javaSourceFile;

        while(currentPath != null && currentPath.fileName.toString() !in commonPrefixes){
            currentPath = currentPath.parent
        }

        val srcRoot = currentPath!!.parent;

        val packageTraversal = srcRoot.relativize(javaSourceFile).parent;

        return packageTraversal.toString().replace("/", ".").replace("\\", ".")
    }

    private fun makeOutputDir(outputDir: String): Path {
        return try {
            val candidate = Paths.get(outputDir)
            if ( ! Files.isDirectory(candidate)) {
                throw InvalidPathException(outputDir, "output directory must be an existing directory")
            }

            candidate;
        }
        catch(exception : InvalidPathException){
            println(exception.message)
            System.exit(InvalidOutputDirCode)
            throw RuntimeException()
        }
    }

    private fun makeSourceFile(sourceFileSpecified: String?): Path {
        return try {
            val candidate = Paths.get(sourceFileSpecified);
            if ( !Files.isRegularFile(candidate)) {
                throw InvalidPathException(sourceFileSpecified, "$sourceFileSpecified does not exist")
            }
            if ( !candidate.getFileName().toString().endsWith(".java")) {
                throw InvalidPathException(sourceFileSpecified, "$sourceFileSpecified is not a java file")
            }

            candidate
        }
        catch(exception: InvalidPathException) {
            println(exception.message)
            exception.printStackTrace()
            System.exit(InvalidSourcePathCode)
            throw RuntimeException()
        }
    }


    private fun makeCommandLineFrom(args: Array<String>, parser: DefaultParser): CommandLine {
        return try {
            val candidateCmd = parser.parse(options, args);
            if (candidateCmd.hasOption("source-class") == candidateCmd.hasOption("source-file")) {
                throw MissingOptionException("must specify either --source-class or --source-file (but NOT both)")
            }

            candidateCmd
        }
        catch(e: MissingOptionException) {
            println(e.message)
            printHelp()
            System.exit(InsufficientArgumentsCode);
            throw RuntimeException()
        }
    }

}