package notifications

import groovy.json.JsonOutput

class Slack implements Notification {

  String username
  String channel
  String icon = ':zombie:'
  String url

  public notify(
    String title,
    String titleUrl,
    String message,
    String service,
    String branch,
    String color,
    String image
  ) {
    Map options = [
      image: image,
      title: title,
      branch: branch,
      icon: this.icon,
      message: message,
      service: service,
      titleUrl: titleUrl,
      channel: this.channel,
      username: this.username,
      color: this.resolveColor(color)
    ]

    this.send(options)
  }

  /**
   * Send message
   *
   * @param options Message configuration/options
   * @return void
   */
  protected void send(Map options) {
    def messageOptions = [
      color: options.color,
      title: options.title,
      text: options.message,
      channel: options.channel,
      icon_emoji: options.icon,
      username: options.username,
      title_link: options.titleUrl,
      image_url: options.image,
  		fields: [[
  			title: 'Service',
  			value: options.service,
  			short: true
  		], [
  			title: 'Branch',
  			value: options.branch,
  			short: true
  		]]
    ]

    def payload = JsonOutput.toJson([attachments: [messageOptions]])

    println payload
    // sh "curl -X POST --data-urlencode \'payload=${payload}\' ${this.url}"
  }

  /**
   * Resolves message color
   *
   * @param  color Color to resolve
   * @return string
   */
  String resolveColor(String color) {
    if (color == 'info') {
      return '#5BC0DE'
    }

    if (color == 'error') {
      return 'danger'
    }

    if (color == 'ok') {
      return 'good'
    }

    return color
  }


}
