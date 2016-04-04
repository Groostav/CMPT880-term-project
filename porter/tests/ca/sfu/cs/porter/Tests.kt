package ca.sfu.cs.porter

import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.nio.file.Paths
/**
 * Created by Geoff on 4/3/2016.
 */
class Tests{

    @Test fun when_asking_for_test_001_of_regression_test_against_treeset_should_properly_build_source(){
        //setup
        val targetBinariesPath = Paths.get(RegressionTest0::class.java.protectionDomain.codeSource.location.toURI())
        val pathToExpected = Paths.get(Tests::class.java.getResource("RegressionTest0Driver.expected").toURI())

        val args = arrayOf(
                "--source-method",
                "test001",
                "--source-class",
                "ca.sfu.cs.porter.RegressionTest0",
                "--target-java-src-dir",
                //TODO this test is odd, its putting java source in a binary directory. Not-obvious.
                "\"${targetBinariesPath.toAbsolutePath().toString()})\""
        )
        val porter = Porter()

        //act
        porter.port(args)

        //assert
        val expectedSrc = targetBinariesPath.resolve("ca/sfu/cs/porter/RegressionTest0Driver.java")
        assertThat(expectedSrc.toFile()).hasSameContentAs(pathToExpected.toFile())
    }
}