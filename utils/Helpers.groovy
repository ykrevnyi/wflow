package utils

class Helpers {

  public String getCommitAuthorName(String service) {
    return 'getCommitAuthorName'
    // return sh(returnStdout: true, script: "git --no-pager show -s --format='%an' `git rev-parse HEAD`").trim()
  }

  public String getBuildUrl() {
    String authorName = getCommitAuthorName()

    return "https://github.com/${authorName}"
  }

  public String getBuildId() {
    return 'build_id'
    // return env?.BUILD_ID;
  }

}
