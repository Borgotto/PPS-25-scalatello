package it.unibo.pps.view.io

object Sanitizer:

  // Forbidden chars on Windows and ASCII control characters
  private val IllegalCharPatterns = "[\\x00-\\x1f\\x7f<>:\"/\\\\|?*]".r

  // Reserved Windows filenames
  private val ReservedWindowsNames = Set(
    "CON", "PRN", "AUX", "NUL",
    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
  )

  private def replaceIllegalChars(replacement: String)(s: String): String =
    IllegalCharPatterns.replaceAllIn(s, replacement)

  private def trimEdges(s: String): String =
    s.trim.replaceAll("^[\\s.]+|[\\s.]+$", "")

  private def handleReservedNames(replacement: String)(s: String): String =
    val baseName = s.split("\\.").headOption.getOrElse("").toUpperCase
    if ReservedWindowsNames.contains(baseName) then s"$replacement$s" else s

  private def handleEmpty(replacement: String)(s: String): String =
    if s.isEmpty then s"file$replacement" else s

  private def truncate(s: String): String =
    if s.length > 255 then s.substring(0, 255) else s

  def sanitize(filename: String, replacement: String = "_"): String =
    if filename == null || filename.trim.isEmpty then
      val timestamp = System.currentTimeMillis()
      s"unnamed_save_$timestamp"
    else
      val _sanitize: String => String =
        replaceIllegalChars(replacement) _ andThen
          trimEdges                        andThen
          handleReservedNames(replacement) andThen
          handleEmpty(replacement)         andThen
          truncate
      _sanitize(filename)
