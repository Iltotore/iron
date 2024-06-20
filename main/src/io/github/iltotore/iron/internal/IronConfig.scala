package io.github.iltotore.iron.internal

case class IronConfig(color: Boolean, oneLine: Boolean)

object IronConfig:

  val fromSystem: IronConfig = IronConfig(
    color = sys.props.get("iron.color").orElse(sys.env.get("IRON_COLOR")).flatMap(_.toBooleanOption).getOrElse(true),
    oneLine = sys.props.get("iron.oneLine").orElse(sys.env.get("IRON_ONE_LINE")).flatMap(_.toBooleanOption).getOrElse(false),
  )
