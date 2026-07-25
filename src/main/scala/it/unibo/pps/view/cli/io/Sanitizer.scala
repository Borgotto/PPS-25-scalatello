package it.unibo.pps.view.cli.io

object Sanitizer:

  private val replacementChar = "_"
  private val maxFileNameLength = 255

  // ASCII control characters and forbidden characters on Windows
  private val illegalCharPatterns = "[\\x00-\\x1f\\x7f<>:\"/\\\\|?*]".r

  // Reserved filenames on Windows
  private val reservedFileNames = Set(
    "CON", "PRN", "AUX", "NUL", "CONIN$", "CONOUT$",
    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
  )

  private def replaceIllegalChars(fileName: String): String =
    illegalCharPatterns.replaceAllIn(fileName, replacementChar)

  private def trimEdges(fileName: String): String =
    // Remove trailing spaces and trailing points
    fileName.trim.replaceAll("^[\\s.]+|[\\s.]+$", "")

  private def handleReservedNames(fileName: String): String =
    if reservedFileNames.contains(fileName.toUpperCase) then s"$replacementChar$fileName" else fileName

  private def truncate(fileName: String): String =
    if fileName.length > maxFileNameLength
    then fileName.substring(0, maxFileNameLength)
    else fileName

  def sanitize(fileName: String): String =
    if fileName == null || fileName.trim.isEmpty then
      val timestamp = System.currentTimeMillis()
      s"unnamed_$timestamp"
    else
      val _sanitize: String => String =
        replaceIllegalChars _ andThen
          trimEdges           andThen
          handleReservedNames andThen
          truncate
      _sanitize(fileName)
