# Testing - Cristina Zoccola

Mi sono occupata di testare il codice da me descritto nella mia sezione di [implementazione](../5-implementation/cristina-zoccola.md).

L'ho fatto creando:

- le seguenti classi di test: [`BoardTest`](#boardtest), [`DiskTest`](#disktest), [`ComputationsPosExtensionsTest`](#computationsposextensionstest), [`IntExtensionsTest`](#intextensionstest), [`PositionTest`](#positiontest) e [`ControllerTest`](#controllertest);
- una classe di supporto: [`BoardTestParams`](#boardtestparams).

Per poter scrivere test con una sintassi molto simile al linguaggio naturale ho scelto di:

- implementare il `trait` di ***ScalaTest***: `AnyFlatSpec`;
- usare i `should` `Matchers` al posto delle `assert`.

Per evitare ripetizioni di codice, ho creato dei test parametrici: l'ho fatto implementando il `trait` di **ScalaTest** `TableDrivenPropertyChecks`: questo `trait` permette di creare delle tabelle contenenti i parametri su cui chiamare uno o più test.

Esempio nella classe `BoardTest`:

```scala
import org.scalatest.matchers.should.Matchers.{an, be, should}
...

class BoardTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  ...
  private val initialBoardsTestTable = Table(
    ("initialBoard", "initialDisksOnBoard"),
    (..., ...),
    ...
  )
  "A Board without disks" should 
  "initialize itself with the disks in the correct positions" in:
    forEvery(initialBoardsTestTable):
      (initialBoard, initialDisksOnBoard) =>
        initialBoard.disks should be(initialDisksOnBoard)
```

## BoardTest

Questa classe contiene i test relativi alle classi: `BoardImpl`, `BoardComputations` e `BoardCreationExtensions`.

Per rimuovere le ripetizioni di codice ho usato, sia i test parametrici, sia una classe di supporto: `BoardTestParams`.

### BoardTestParams

Questa classe contiene tutti i parametri necessari per i test dedicati alle funzionalità della scacchiera.

I parametri sono istanziati in base alla forma passata ad essa.

```scala
class BoardTestParams(val shape: Shape):
  val bottomRightCenterPos: Position =
    shape match
      case Shape.Square(n) => (n.half, n.half)
      case Shape.Rectangle(h, w) => (h.half, w.half)
  ...

// in BoardTest:
private val BOARD_SIZE: Int = 4
private val BOARD_HEIGHT: Int = 4
private val BOARD_WIDTH: Int = 6

private val squareParams: BoardTestParams = 
  BoardTestParams(Shape.Square(BOARD_SIZE))
private val rectangleParams: BoardTestParams = 
  BoardTestParams(Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH))
```

Questo rende i test contenuti nella classe `BoardTest` facilmente estendibili a possibili forme aggiuntive della scacchiera:

- aggiungendo un `case` nei `match case` dei parametri presenti in `BoardTestParams`;
- in `BoardTest`:

  - istanziando la classe `BoardTestParams` con la nuova forma della scacchiera;
  - aggiungendo i parametri nelle tabelle dei test già esistenti.

## DiskTest

In questa classe sono presenti i test che riguardano i metodi nella classe: `Disk`.

## ComputationsPosExtensionsTest

In questa classe sono contenuti i test che riguardano la classe: `ComputationsPosExtensionsRectangle`. 

Questa classe di test è facilmente estendibile alle possibili future implementazioni del `trait` `ComputationsPosExtensions`:

- importando il metodo del nuovo `object` creato;
- aggiungendo i nuovi parametri nella tabella del test;
- aggiungendo un `case` nel `match case` presente nel test.

  ```scala
  private val inBoundsTestTable = Table(
    ("shape", "pos", "expectedResult"),
    ...
  )
  "A Position" should "know if it is in the bounds of a shape" in:
    forEvery(inBoundsTestTable):
      (shape, pos, expectedResult) =>
        shape match
          case _ => pos.inBounds(shape) should be(expectedResult)
  ```

## IntExtensionsTest

In questa classe sono contenuti i test dei comportamenti che devono avere i metodi contenuti all'interno dell'oggetto `IntExtensions`.

## PositionTest

In questa classe sono presenti i test dei metodi contenuti nella `case class` `Position`.

## ControllerTest

In questa classe sono contenuti i test dei metodi, che riguardano la gestione dei turni di una partita, presenti nella classe `ControllerImpl`.

Per implementare questi test ho utilizzato **Mockito** e in particolare la sua funzionalità *Spy*: essa mi ha permesso di creare dei test senza rompere l'incapsculamento. \
L'ho utilizzata per contare la quantità di volte in cui viene chiamato il metodo `notifySubscribers(MatchState)` in diverse situazioni:

```scala
class ControllerTest extends AnyFlatSpec:
...

  "A Controller, if the first player is the User" should 
  "notify the state of the match only once" in:
    val mockedController: ControllerImpl = Mockito.spy(ControllerImpl())
    mockedController.startMatch(...)
    verify(mockedController, 
      times(1)).notifySubscribers(any(classOf[MatchState]))

  "A Controller, if the first player is the opponent" should 
  "notify the state of the match twice" in:
    val mockedController: ControllerImpl = Mockito.spy(ControllerImpl())
    mockedController.startMatch(...)
    verify(mockedController, 
      times(2)).notifySubscribers(any(classOf[MatchState]))

  "A Controller" should "notify the state of the match thrice, 
  at the start, after user selection and opponent turn" in:
    val mockedController: ControllerImpl = Mockito.spy(ControllerImpl())
    mockedController.startMatch(...)
    mockedController.handleSelection(...)
    verify(mockedController, 
      times(3)).notifySubscribers(any(classOf[MatchState]))
```
