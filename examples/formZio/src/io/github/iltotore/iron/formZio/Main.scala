import zio.*
import zio.http.*

object Main extends ZIOAppDefault:

  override val run =
    Server.serve(HttpServer.app, None).provide(Server.default)