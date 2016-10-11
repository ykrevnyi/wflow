#!groovy

import groovy.json.JsonOutput

// List of gifs to show on error
failedGIFs = [
  'https://media.giphy.com/media/d7TkxiMvUS3Ju/giphy.gif',
  'https://media.giphy.com/media/2ZFuPKWcSw16E/giphy.gif',
  'https://media.giphy.com/media/U8MnmuVDpK264/giphy.gif',
  'https://media.giphy.com/media/BtmaOkfGVrNFm/giphy.gif'
]

def infoStage(String message, String service, String branch, String channel = '') {
  notifySlack(
    composeHeading(service),
    utils.getBuildUrl(),
    message,
    service,
    branch,
    "info",
    "",
    channel
  )
}

def okStage(String message, String service, String branch, String channel = '') {
  notifySlack(
    composeHeading(service),
    utils.getBuildUrl(),
    message,
    service,
    branch,
    "ok",
    "",
    channel
  )
}

def failedStage(String message, String service, String branch, String channel = '') {
  Random random = new Random()
  def image = failedGIFs[random.nextInt(3)]

  notifySlack(
    composeHeading(service),
    utils.getBuildUrl(),
    message,
    service,
    branch,
    "error",
    image,
    channel
  )
}

/**
 * Send message to the Slack
 *
 * @param   Main title
 * @param   Description
 * @param   Message color (info, good, error, warn)
 * @param   Channel to send message into
 *
 * @return  void
 */
def notifySlack(title, titleUrl, description, service, branch, color, image, channel) {
  def messageColor = resolveMessageColor(color)
  def slackURL = resolveSlackEndpoint(channel)
  def config = [
    title      : title,
    title_link : titleUrl,
    text       : description,
    username   : "jenkins",
    icon_emoji : ":zombie:",
    image      : '',
    channel    : channel,
    color      : messageColor,
    image_url  : image,
		fields: [[
			title: 'Service',
			value: service,
			short: true
		], [
			title: 'Branch',
			value: branch,
			short: true
		]]
  ]
  def payload = JsonOutput.toJson([attachments: [config]])

  sh "curl -X POST --data-urlencode \'payload=${payload}\' ${slackURL}"
}

/**
 * Resolve Slack webhook url from channel name
 *
 * @param  channel Channel name
 * @return         Webhook channel url or false
 */
def resolveSlackEndpoint(String channel) {
  if (channel == 'staging') {
    return env.SLACK_ENDPOINT_STAGING
  }

  if (channel == 'production') {
    return env.SLACK_ENDPOINT_PRODUCTION
  }

  return false
}

/**
 * Compose heading
 *
 * @param   Service name
 *
 * @return  string
 */
def composeHeading(String service) {
  def author = utils.getCommitAuthorName();

  return "${service.capitalize()} #${env.BUILD_ID} (by @${author})"
}

/**
 * Resolve predefined Slack colors
 *
 * @return string
 */
def resolveMessageColor(color) {
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

return this
