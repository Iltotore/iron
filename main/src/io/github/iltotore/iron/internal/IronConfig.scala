package io.github.iltotore.iron.internal

import io.github.iltotore.iron.internal.IronConfig.CodeFormat

/**
 * The config or Iron at compile-time.
 *
 * @param codeFormat the format used to print code
 * @param shortMessages use abbreviated messages, useful for error lenses and similar
 * @param shortReasons use more concise error messages or full error if `false`
 */
case class IronConfig(codeFormat: CodeFormat, shortMessages: Boolean, shortReasons: Boolean):
  val color = codeFormat == CodeFormat.FullColored

object IronConfig:

  /**
    * The format used to print code.
    */
  enum CodeFormat:
    case Full, FullColored, Short, Structure 

  object CodeFormat:

    /**
      * Get the format associated with the given key.
      *
      * @param value the key associated to a format
      * @return the associated code format
      */
    def fromString(value: String): Option[CodeFormat] = value.toLowerCase match
      case "full" => Some(CodeFormat.Full)
      case "full_colored" => Some(CodeFormat.FullColored)
      case "short" => Some(CodeFormat.Short)
      case "structure" => Some(CodeFormat.Structure)
      case _ => None

  /**
   * The config as defined by the properties/environment.
   */
  val fromSystem: IronConfig =
    val color =
      sys.props.get("iron.color")
        .orElse(sys.env.get("IRON_COLOR"))
        .flatMap(_.toBooleanOption)

    val format =
      sys.props.get("iron.codeFormat")
        .orElse(sys.env.get("IRON_CODE_FORMAT"))
        .flatMap(CodeFormat.fromString)
        .orElse(color.map(if _ then CodeFormat.FullColored else CodeFormat.Full))
        .getOrElse(CodeFormat.Short)

    IronConfig(
      codeFormat = format,
      shortMessages = sys.props.get("iron.shortMessages").orElse(sys.env.get("IRON_SHORT_MESSAGES")).flatMap(_.toBooleanOption).getOrElse(false),
      shortReasons = sys.props.get("iron.shortReasons").orElse(sys.env.get("IRON_SHORT_REASONS")).flatMap(_.toBooleanOption).getOrElse(true)
    )
