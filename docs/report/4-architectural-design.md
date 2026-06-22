# Design architetturale

## Spiegazione dell'architettura



## Diagramma dei componenti

```mermaid
---
title: Diagramma architetturale
---
classDiagram
  namespace ArchitetturaMVC.View {
    class CLIView {
      +renderBoard(): Unit
      +showMenu(): Unit
    }
  }
  namespace ArchitetturaMVC.Controller {
    class GameController {
      +newGame(): Unit
      +isGameOver(): Boolean
      +placeDisk(Position): Unit
      +getCurrentGameState(): GameState
      +getAvailableMoves(): List<Position>
      +saveGame(): Unit
      +loadGame(): Unit
    }
    class SaveManager {
      +save(gameState: GameState): Unit
      +load(): GameState
    }
  }
  namespace ArchitetturaMVC.Model {
    class Board {
    }
    class Disk {
    }
    class GameLogic {
      +getGameState(): GameState
    }
    class Opponent {
    }
  }

  CLIView o-- GameController : interagisce con
  GameController o-- GameLogic : opera su
  SaveManager --o GameController : utilizzato da
  
  GameLogic o-- Board
  GameLogic o-- Opponent
  Board o-- Disk

```
