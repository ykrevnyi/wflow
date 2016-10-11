#!groovy

def call(String serviceName, String branchName) {
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  stage 'Deploy'
  try {
    def legacyServiceName = "yippie-${serviceName}"

    utils.bulkCommitStatus(['Deploy': 'PENDING'])

    build([
      job: 'deploy',
      quietPeriod: 0,
      parameters: [
        string(name: 'ENV', value: deployEnvironment),
        string(name: 'SERVICE', value: legacyServiceName)
      ]
    ])

    slack.okStage('Deployment done :tada:', serviceName, branchName, deployEnvironment)

    utils.bulkCommitStatus(['Deploy': 'SUCCESS'])
  } catch (err) {
    // Set expectations
    utils.bulkCommitStatus(['Deploy': 'ERROR'])

    // Send slack notification
    slack.failedStage('Deploy failed!', serviceName, branchName, deployEnvironment)

    utils.abort(err)
  }


}

return this
