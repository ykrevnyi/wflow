#!groovy

@NonCPS def entries(m) {m.collect {k, v -> [k, v]}}

def abort(err) {
  sleep 3

  throw err
}

def getShellOutput(cmd) {
    Random random = new Random()

    def randomNumber = Math.random()*1000;
    def file = "tmp/read-from-the-output-${randomNumber}.txt"
    sh cmd + " > $file"
    def newId = readFile(file).trim()
    sh "rm $file"

    return newId
}

def normalizeBranchName(String name) {
    return name.toLowerCase()
}

// Sets commit status on the github
def setCommitStatus(context, message, status) {
    step([
      $class: 'GitHubCommitStatusSetter',
      contextSource: [
        $class: 'ManuallyEnteredCommitContextSource',
        context: context
      ],
      statusResultSource: [
        $class: 'ConditionalStatusResultSource',
        results: [[
          $class: 'AnyBuildResult',
          message: message,
          state: status
        ]]
      ]
    ])
}

def bulkCommitStatus(statuses) {
  statuses = entries(statuses)

  for (int i = 0; i < statuses.size(); i++) {
    def status = statuses.get(i)

    if (status[1] == 'WAITING') {
      setCommitStatus(status[0], "Waiting..", 'PENDING')
    }

    if (status[1] == 'PENDING') {
      setCommitStatus(status[0], "${status[0]}ing service..", 'PENDING')
    }

    if (status[1] == 'SUCCESS') {
      setCommitStatus(status[0], "${status[0]}ing service.. Done!", 'SUCCESS')
    }

    if (status[1] == 'ERROR') {
      setCommitStatus(status[0], "${status[0]}ing service.. Failed!", 'ERROR')
    }

    if (status[1] == 'FAILURE') {
      setCommitStatus(status[0], "Canceled.", 'FAILURE')
    }
  }
}

def getBuildName(serviceName, tag) {
  return "${serviceName}:${tag}"
}

def getEcrServiceName(buildName) {
  return "${env.REGISTRY_URI}/yippie-${buildName}"
}

def getServiceName(jobName) {
  return sh(returnStdout: true, script: "basename `dirname ${jobName}`").trim()
}

def getBuildTag(branchName) {
  if (branchName == 'master') {
    return 'latest'
  }

  return 'staging'
}

def getDeployEnvironment(branchName) {
  if (branchName == 'master') {
    return 'master'
  }

  return 'staging'
}

def getBuildUrl() {
  return env.BUILD_URL;
}

def getCommitAuthorName() {
  return sh(returnStdout: true, script: "git --no-pager show -s --format='%an' `git rev-parse HEAD`").trim()
}

def getCommitAuthorUrl() {
  def authorName = getCommitAuthorName();

  return "https://github.com/${authorName}"
}

return this
