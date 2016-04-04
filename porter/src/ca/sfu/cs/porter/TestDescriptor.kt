package ca.sfu.cs.porter

import java.nio.file.Path

data class TestDescriptor(
        val testSource : Path,
        val testClass : String,
        val testMethod : String,
        val packageName : String,
        val outputDir : Path
){}