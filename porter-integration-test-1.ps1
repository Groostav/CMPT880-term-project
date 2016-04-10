mkdir -Force porter-integration-test-output
java -jar porter.jar `
  --source-file ./porter/test-resources/RegressionTest0NoPackage.java `
  --source-method test001 `
  --output-dir ./porter-integration-test-output