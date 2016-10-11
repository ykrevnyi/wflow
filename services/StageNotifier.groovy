package services

import notifications.Notification
import services.GifFactory
import utils.Helpers

class StageNotifier {

  GifFactory gifs
  Helpers helpers
  List<Notification> notifications

  public void notify(String type, String message, String service, String branch) {
    this.notifications.each {
      it.notify(
        this.composeHeading(service),
        this.composeBuildUrl(),
        message,
        service,
        branch,
        type,
        this.composeImage(type)
      )
    }
  }

  private String composeImage(String type) {
    return this.gifs.make(type)
  }

  private String composeHeading(String service) {
    String author = this.helpers.getCommitAuthorName()
    String buildId = this.helpers.getBuildId()

    return "${service.capitalize()} #${buildId} (by @${author})"
  }

  private String composeBuildUrl() {
    return this.helpers.getBuildUrl()
  }

}
