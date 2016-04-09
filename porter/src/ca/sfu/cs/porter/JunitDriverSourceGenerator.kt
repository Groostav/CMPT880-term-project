package ca.sfu.cs.porter

import org.stringtemplate.v4.ST
import org.stringtemplate.v4.misc.ErrorBuffer
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.*

class JunitDriverSourceGenerator {

    val templatePath = JunitDriverSourceGenerator::class.java.getResource("driver.st");

    fun generateSource(srcTestDescriptor: TestDescriptor) : Path {

        val content = try { templatePath.openStream().use { Scanner(it).useDelimiter("\\Z").next(); } }
        catch(e : FileNotFoundException) { System.exit(4); throw RuntimeException() }

        val template = ST(content)

        //eh, this is a ~ simple map, empty strings -> null
        val resolvedTestDescriptor = srcTestDescriptor.run {  copy(packageName = if(packageName!!.isEmpty()) null else packageName) };
        template.add("src", resolvedTestDescriptor )

        val target = srcTestDescriptor.testSource.run {
            parent.resolve(fileName.toString().replace(".java", "Driver.java"))
        }

        template.write(target.toFile(), ErrorBuffer())

        return target;
    }
}