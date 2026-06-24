# Design architetturale

## Spiegazione dell'architettura



## Diagramma dei componenti

```mermaid
---
title: Diagramma UML architetturale
---
classDiagram
  namespace ViewPackage {
    class View {
      <<interface>>
      + showMenu()
      + update(state: MatchState)
    }
    class CLIView
  }
  namespace ControllerPackage {
    class Controller {
      <<interface>>
      + initializeMatch()
      + handleSelection(position: Position)
      + saveMatch()
      + loadMatch()
    }
    class SaveManager {
      <<interface>>
      + save(state: MatchState)
      + load(): MatchState
    }
  }
  namespace ModelPackage {
    class Logic {
      <<interface>>
      + initialize()
      + getMatchState(): MatchState
      + placeUserDisk(position: Position): bool
      + placeOpponentDisk(): bool
    }
  }

  View --> Controller
  Controller ..> View : notifies
  Controller --> Logic
  SaveManager <-- Controller

  View <|.. CLIView
```

Scenario: iterazione del loop di gioco tra View, Controller e Model, nell'eventualità di una mossa legale da parte dell'utente a cui segue una mossa dell'avversario.
```mermaid
sequenceDiagram
    View->>Controller: handleSelection(position)
    Controller->>Logic: placeUserDisk(position)
    Logic->>Controller: true
    Controller->>Logic: getMatchState()
    Logic->>Controller: MatchState
    Controller->>View: update(matchState)
    Controller->>Logic: placeOpponentDisk()
    Logic->>Controller: true
    Controller->>Logic: getMatchState()
    Logic->>Controller: MatchState
    Controller->>View: update(matchState)
```