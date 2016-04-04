package ca.sfu.cs.porter

import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.nio.file.Paths
/**
 * Created by Geoff on 4/3/2016.
 */
class Tests{

    val packageName = "ca.sfu.cs.porter"
    val pathToExpected = Paths.get(Tests::class.java.getResource("RegressionTest0Driver.expected.java").toURI())
    val pathToInitial = Paths.get(Tests::class.java.getResource("RegressionTest0.java").toURI())
    val pathToRootOfInitial = pathToInitial.root.resolve(pathToInitial.subpath(0, pathToInitial.nameCount - packageName.split(".").size - 1))

    @Test fun when_asking_for_test_001_of_regression_test_against_treeset_should_properly_build_source(){
        //setup
        val args = arrayOf(
                "--source-method", "test001",
                "--source-file", "\"${pathToInitial.toAbsolutePath()}\"",
                "--output-dir", "\"${pathToRootOfInitial.toAbsolutePath()}\""
        )
        val porter = Porter()

        //act
        porter.port(args)

        //assert
        val pathOfResult = Paths.get(Tests::class.java.getResource("RegressionTest0Driver.java").toURI())
        assertThat(pathOfResult .toFile()).hasSameContentAs(pathToExpected.toFile())
    }

    @Test fun when_asking_for_test_001_of_regression_test_against_treeset_with_specific_package_name_should_properly_build_source(){
        //setup
        val args = arrayOf(
                "--source-method", "test001",
                "--package-name", "$packageName",
                "--source-file", "\"${pathToInitial.toAbsolutePath()}\"",
                "--output-dir", "\"${pathToRootOfInitial.toAbsolutePath()}\""
        )
        val porter = Porter()

        //act
        porter.port(args)

        //assert
        val pathOfResult = Paths.get(Tests::class.java.getResource("RegressionTest0Driver.java").toURI())
        assertThat(pathOfResult .toFile()).hasSameContentAs(pathToExpected.toFile())
    }
}