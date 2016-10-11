#!groovy

import notifications.Slack
import services.StageNotifier
import utils.Helpers
import services.GifFactory

GifFactory gifs = new GifFactory()
Helpers helpers = new Helpers()

Slack slack = new Slack(
  username: 'jenkins',
  channel: 'staging',
  url: 'webhook.url'
)

def notifier = new StageNotifier(
  helpers: helpers,
  gifs: gifs,
  notifications: [slack]
)

notifier.notify('error', 'msg', 'service', 'branch')
