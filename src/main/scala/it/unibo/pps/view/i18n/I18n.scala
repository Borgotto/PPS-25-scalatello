package it.unibo.pps.view.i18n

import java.util.{Locale, ResourceBundle}

class I18n(locale: Locale):
  
  private val bundle = ResourceBundle.getBundle("messages", locale)

  def t(key: String): String =
    try bundle.getString(key)
    catch case _: Exception => s"[$key]"
