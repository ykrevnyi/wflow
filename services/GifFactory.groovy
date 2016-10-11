package services

class GifFactory {

  Random random = new Random()
  Map gifs = [
    ok: [],
    info: [],
    error: [
      'https://media.giphy.com/media/d7TkxiMvUS3Ju/giphy.gif',
      'https://media.giphy.com/media/2ZFuPKWcSw16E/giphy.gif',
      'https://media.giphy.com/media/U8MnmuVDpK264/giphy.gif',
      'https://media.giphy.com/media/BtmaOkfGVrNFm/giphy.gif'
    ]
  ]

  public String make(String type) {
    List gifList = this.gifs[type]
    Integer gifListCount = gifList.size()

    if (!gifListCount) {
      return ''
    }

    return gifList[random.nextInt(gifList.size() - 1)]
  }

}
