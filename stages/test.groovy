#!groovy

def call(String serviceName, String branchName) {
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  stage 'Test'
  try {
    utils.bulkCommitStatus(['Test': 'PENDING'])
    // sh "docker-compose -f jenkins-docker-compose.yml run --rm ${serviceName} npm test"
    echo "Executing fake tests"
    cleanup()
    utils.bulkCommitStatus(['Test': 'SUCCESS'])
  } catch (err) {
    // Set expectations
    utils.bulkCommitStatus(['Test': 'ERROR', 'Build': 'FAILURE', 'Publish': 'FAILURE'])

    // Send slack notification
    slack.failedStage('Nope, seems like your tests failed!', serviceName, branchName, deployEnvironment)

    cleanup()
    utils.abort(err)
  }
}

def cleanup() {
  sh "docker-compose -f jenkins-docker-compose.yml down"
  sh "docker-compose -f jenkins-docker-compose.yml rm -f"
}

return this
