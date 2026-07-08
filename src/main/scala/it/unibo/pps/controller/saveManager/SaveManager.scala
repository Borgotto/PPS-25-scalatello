package it.unibo.pps.controller.saveManager

trait Saveable[Class]:
  def encode(data: Class): String
  def decode(data: String): Class

trait SaveManager:
  type FilePath
  def filePath: FilePath
  def save[Class](data: Class)(using saveable: Saveable[Class]): Unit
  def load[Class]()(using saveable: Saveable[Class]): Class

case class SaveManagerImpl(filePath: os.Path) extends SaveManager:
  type FilePath = os.Path

  def save[Class](data: Class)(using saveable: Saveable[Class]): Unit =
    val serializedData = saveable.encode(data)
    os.write.over(filePath, serializedData)

  def load[Class]()(using saveable: Saveable[Class]): Class =
    val serializedData = os.read(filePath)
    saveable.decode(serializedData)

