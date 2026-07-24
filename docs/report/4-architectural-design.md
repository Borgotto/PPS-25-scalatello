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
      + startMatch(shape: Shape, color: Color, opponent: OpponentType)
      + handleSelection(position: Position)
      + saveMatch(fileName: String)
      + loadMatch(fileName: String): MatchState
      + saveFileNames(): List(String)

    }
    class SaveManager {
      + save(data)
      + load(): data
      + saveFileNames(): List(String)
      + deleteSaveFile()
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
      + getAvailablePlacements(color: Color): List(Position)
      + isPlacementValid(color: Color, position: Position): Boolean
      + placeDisk(color: Color, position: Position): Board
    }
    class Disk {
      + color: Color
      + flip(): Disk
    }
    class Player {
      + color: Color
      + strategy: PlacementStrategy
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
      + status: MatchStatus
      + user: User
      + opponent: Opponent
      + activePlayer: PlayerState
      + board: BoardState
    }
    class MatchStatus {
      <<enumeration>>
      InProgress
      UserWon
      OpponentWon
      Tie
    }
    class BoardState {
      + shape: Shape
      + disks: List(DiskState)
      + userAvailablePlacements: List(Position)
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
    MatchState --> MatchStatus
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
