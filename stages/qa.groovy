#!groovy

def call(String serviceName, String branchName) {
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  stage 'Q&A'
  try {
    utils.bulkCommitStatus(['Q&A': 'PENDING'])

    // slack.infoStage('Waiting for Q&A to validate build.', serviceName, branchName, deployEnvironment)
    //
    // timeout(time: 20, unit: 'SECONDS') {
    //   input message: 'Do you want to deploy?'
    // }

    utils.bulkCommitStatus(['Q&A': 'SUCCESS'])
  } catch (err) {
    slack.failedStage('Q&A failed!', serviceName, branchName, deployEnvironment)
    utils.bulkCommitStatus(['Q&A': 'ERROR'])
  }
}

return this
