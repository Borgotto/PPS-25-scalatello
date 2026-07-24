package it.unibo.pps.view.i18n

import java.util.{Locale, ResourceBundle}

/** A provider that manages the internationalization of the application contents,
 *  localizing them according to a specific locale.
 *
 * @param locale the provided locale.
 */
class I18n(locale: Locale):
  
  private val bundle = ResourceBundle.getBundle("messages", locale)

  /** Given the identifying key of a string, provides the
   *  localized string according to the locale set for this i18n provider.
   *
   * @param key the key that identifies the string.
   * @return the localized string.
   */
  def t(key: String): String =
    try bundle.getString(key)
    catch case _: Exception => s"[$key]"

extension (keys: Seq[String])

  /** @return the localized strings given a sequence of string keys. */
  def localize(using i18n: I18n): Seq[String] = keys.map(key => i18n.t(key))
  