# Design architetturale

## Spiegazione dell'architettura



## Diagramma dei componenti

```mermaid
---
title: Diagramma architetturale
---
classDiagram
  namespace ViewPackage {
    class View {
      + renderBoard()
      + showMenu()
    }
  }
  namespace ControllerPackage {
    class Controller {
      + initializeMatch()
      + getMatchState(): MatchState
      + placeDisk(position: Position)
      + isMatchOver(): Boolean
      + saveGame()
      + loadGame()
    }
    class SaveManager {
      + save(matchState: MatchState)
      + load(): MatchState
    }
  }
  namespace ModelPackage {
    class Board {
    }
    class Disk {
    }
    class Logic {
      + initialize()
      + getMatchState(): MatchState
      + makeMove(moveStrategy: MoveStrategy)
      + isGameOver(): Boolean
    }
    class Opponent {
    }
  }

  View o-- Controller : interacts with
  Controller o-- Logic : interacts with
  SaveManager --o Controller : used by
  
  Logic o-- Board
  Logic o-- Opponent
  Board o-- Disk

```
