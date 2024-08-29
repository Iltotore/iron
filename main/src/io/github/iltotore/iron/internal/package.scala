package io.github.iltotore.iron.internal

extension (text: String)
  def colorized(color: String)(using config: IronConfig): String =
    if config.color then s"$color$text${Console.RESET}"
    else text
