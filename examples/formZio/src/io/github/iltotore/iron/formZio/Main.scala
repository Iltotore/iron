import zio.*
import zio.http.*

object Main extends ZIOAppDefault:

  override val run =
    Server.serve(HttpServer.routes).provide(Server.default)
