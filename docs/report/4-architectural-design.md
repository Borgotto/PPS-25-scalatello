# Design architetturale

## Spiegazione dell'architettura

L'architettura del gioco è stata progettata seguendo il pattern architetturale Model-View-Controller (MVC).

Dato un gioco da tavolo come Othello, l'architettura MVC è particolarmente adatta perché permette di separare le responsabilità tra la logica del gioco (Model), l'interfaccia utente (View) e il controllo del flusso dell'applicazione (Controller).

Permette di avere la possibilità in futuro di sostituire facilmente l'interfaccia utente da terminale con un'interfaccia grafica, senza dover modificare la logica del gioco o il controller, e viceversa.

Facilita anche la testabilità dei singoli componenti, sfruttando `Mockito` per creare *placeholder* dei componenti non ancora sviluppati, o per isolarli durante i test.

## Diagramma dei componenti

```mermaid
---
title: Diagramma UML architetturale
---
classDiagram
  namespace ViewPackage {
    class View {
      + showMenu()
      + update(state: MatchState)
    }
    class CLIView
  }
  namespace ControllerPackage {
    class Controller {
      + startMatch(shape: Shape, color: Color)
      + handleSelection(position: Position)
      + saveMatch(filePath: String)
      + loadMatch(filePath: String)
    }
    class SaveManager {
      + save(state: MatchState)
      + load(): MatchState
    }
  }
  namespace ModelPackage {
    class Logic {
      + state: MatchState
      + placeUserDisk(position: Position): Logic
      + placeOpponentDisk(): Logic
    }
    class Board {
      + state: BoardState
      + isPlacementValid(color: Color, position: Position): Boolean
      + placeDisk(color: Color, position: Position): Board
      + captureDisks(newDiskPosition: Position): Board
      + getAvailablePlacements(color: Color): Seq[Position]
    }
    class Disk {
      + color: Color
      + flip(): Disk
    }
    class Player {
      + state: PlayerState
    }
    class PlacementStrategy
    class User
    class Opponent
  }

    Logic --> Board
    Logic --> Player
    Logic --> PlacementStrategy: applies

    Player <|-- User
    Player <|-- Opponent

    Player --> PlacementStrategy : provides

    Board --> Disk

    View --> Controller
    Controller ..> View : notifies
    Controller --> Logic
    SaveManager <-- Controller

    View <|.. CLIView
```

Struttura degli state:

```mermaid
classDiagram
    class MatchState {
      + status: Status
      + activePlayer: PlayerState
      + board: BoardState
    }
    class Status {
      <<enumeration>>
      InProgress
      UserWon
      OpponentWon
      Tie
    }
    class PlayerState {
      <<enumeration>>
      User
      Opponent
      + color: Color
      + strategy: PlacementStrategy
    }
    class BoardState {
      + shape: Shape
      + disks: Seq[DiskState]
    }
    class Shape {
      <<enumeration>>
    }
    class Square {
      + size: int
    }
    class Rectangle {
      + height: int
      + width: int
    }
    class DiskState {
      + color: Color
      + position: Position
    }
    class Color {
      <<enumeration>>
      Black
      White
    }
    class Position {
      + row: int
      + column: int
    }

    MatchState --> BoardState
    MatchState --> Status
    MatchState --> PlayerState
    PlayerState --> Color
    BoardState --> DiskState
    Shape <|-- Square
    Shape <|-- Rectangle
    BoardState --> Shape
    DiskState --> Color
    DiskState --> Position
```

## Interazione tra View, Controller e Model

Iterazione del loop di gioco tra View, Controller e Model, nell'eventualità di una mossa legale da parte dell'utente a cui segue una mossa dell'avversario.

```mermaid
sequenceDiagram
    View->>Controller: handleSelection(position)
    Controller->>Logic: placeUserDisk(position)
    Logic->>Controller: true
    Controller->>Logic: get match state
    Logic->>Controller: MatchState
    Controller->>View: update(matchState)
    Controller->>Logic: placeOpponentDisk()
    Logic->>Controller: true
    Controller->>Logic: get match state
    Logic->>Controller: MatchState
    Controller->>View: update(matchState)
```
