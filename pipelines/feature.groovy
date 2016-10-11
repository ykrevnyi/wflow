#!groovy

def exec(String serviceName, String branchName) {
  def executeTest = load 'ci/stages/test.groovy'
  def executeBuild = load 'ci/stages/build.groovy'
  def executePublish = load 'ci/stages/publish.groovy'

  def buildTag = utils.getBuildTag(branchName)
  def buildName = utils.getBuildName(serviceName, buildTag)

  // Set expectations
  utils.bulkCommitStatus(['Test': 'WAITING'])

  // Do not notify slack on FEATURE branches
  // slack.infoStage('Starting build..', serviceName, branchName)

  // Execute test stage
  executeTest(serviceName, branchName)
}

return this
