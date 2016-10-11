#!groovy

def exec(String serviceName, String branchName) {
  def executeTest = load 'ci/stages/test.groovy'
  def executeBuild = load 'ci/stages/build.groovy'
  def executePublish = load 'ci/stages/publish.groovy'
  def executeQA = load 'ci/stages/qa.groovy'
  def executeDeploy = load 'ci/stages/deploy.groovy'

  def buildTag = utils.getBuildTag(branchName)
  def buildName = utils.getBuildName(serviceName, buildTag)
  def ecrServiceName = utils.getEcrServiceName(buildName)
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  // Set expectations
  utils.bulkCommitStatus([
    'Test': 'WAITING',
    'Build': 'WAITING',
    'Publish': 'WAITING',
    'Q&A': 'WAITING',
    'Deploy': 'WAITING'
  ])

  // Notify slack before start
  slack.infoStage('Starting build..', serviceName, branchName, deployEnvironment)

  // Execute test stage
  executeTest(serviceName, branchName)

  // Execute build stage
  executeBuild(buildName, serviceName, branchName)

  // Execute publish stage
  executePublish(buildName, ecrServiceName, serviceName, branchName)

  // Execute QA stage
  executeQA(serviceName, branchName)

  // Execute deploy stage
  executeDeploy(serviceName, branchName)
}

return this
