#!groovy

def call(String buildName, String serviceName, String branchName) {
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  stage 'Build'
  try {
    utils.bulkCommitStatus(['Build': 'PENDING'])
    sh "docker build -t ${buildName} -f Dockerfile-production ."
    utils.bulkCommitStatus(['Build': 'SUCCESS'])
  } catch (err) {
    // Set expectations
    utils.bulkCommitStatus(['Build': 'ERROR', 'Publish': 'FAILURE'])

    // Send slack notification
    slack.failedStage('Build failed!', serviceName, branchName, deployEnvironment)

    sh "sudo docker rmi ${buildName}"
    utils.abort(err)
  }
}

return this
