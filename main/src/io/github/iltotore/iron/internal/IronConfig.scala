package io.github.iltotore.iron.internal

/**
 * The config or Iron at compile-time.
 *
 * @param color enable colored messages
 * @param shortMessages use abbreviated messages, useful for error lenses and similar
 */
case class IronConfig(color: Boolean, shortMessages: Boolean)

object IronConfig:

  /**
   * The config as defined by the properties/environment.
   */
  val fromSystem: IronConfig = IronConfig(
    color = sys.props.get("iron.color").orElse(sys.env.get("IRON_COLOR")).flatMap(_.toBooleanOption).getOrElse(true),
    shortMessages = sys.props.get("iron.shortMessages").orElse(sys.env.get("IRON_SHORT_MESSAGES")).flatMap(_.toBooleanOption).getOrElse(false)
  )
