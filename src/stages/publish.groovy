#!groovy

def call(String buildName, String ecrServiceName, String serviceName, String branchName) {
  def deployEnvironment = utils.getDeployEnvironment(branchName)

  stage 'Publish'
  try {
    utils.bulkCommitStatus(['Publish': 'PENDING'])

    sh "\$(aws ecr get-login --profile ${env.AWS_ECR_PROFILE} --region ${env.AWS_ECR_REGION})"
    sh "docker tag ${buildName} ${ecrServiceName}"
    sh "docker push ${ecrServiceName}"
    sh "sudo docker rmi ${buildName} ${ecrServiceName}"

    utils.bulkCommitStatus(['Publish': 'SUCCESS'])
  } catch (err) {
    // Set expectations
    utils.bulkCommitStatus(['Publish': 'ERROR'])

    // Send slack notification
    slack.failedStage('Publish failed!', serviceName, branchName, deployEnvironment)

    sh "sudo docker rmi ${buildName} ${ecrServiceName}"
    utils.abort(err)
  }
}

return this
