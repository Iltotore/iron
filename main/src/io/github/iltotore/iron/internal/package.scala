package io.github.iltotore.iron.internal

extension (text: String)
  
  def colorized(color: String): String =  s"$color$text${Console.RESET}"