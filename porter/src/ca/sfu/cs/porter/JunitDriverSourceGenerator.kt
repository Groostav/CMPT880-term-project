package ca.sfu.cs.porter

import org.stringtemplate.v4.ST
import org.stringtemplate.v4.misc.ErrorBuffer
import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.*

class JunitDriverSourceGenerator {

    val templatePath = Paths.get(JunitDriverSourceGenerator::class.java.getResource("driver.st").toURI());

    fun generateSource(srcTestDescriptor: TestDescriptor){

        val content = try { Scanner(templatePath.toFile()).useDelimiter("\\Z").next(); }
        catch(e : FileNotFoundException) { System.exit(4); throw RuntimeException() }

        val template = ST(content)

        template.add("src", srcTestDescriptor)

        val target = srcTestDescriptor.testSource.run {
            parent.resolve(fileName.toString().replace(".java", "Driver.java"))
        }

        template.write(target.toFile(), ErrorBuffer())
    }
}