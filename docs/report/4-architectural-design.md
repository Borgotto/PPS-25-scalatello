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
      <<interface>>
      + showMenu()
      + update(state: MatchState)
    }
    class CLIView
  }
  namespace ControllerPackage {
    class MatchController {
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
    class MatchLogic {
      <<interface>>
      + initialize()
      + getMatchState(): MatchState
      + placeUserDisk(position: Position): bool
      + placeOpponentDisk(): bool
    }
  }

  View --> MatchController
  MatchController ..> View : notifies
  MatchController --> MatchLogic
  SaveManager <-- MatchController

  View <|.. CLIView
```

## Interazione tra View, Controller e Model

Iterazione del loop di gioco tra View, Controller e Model, nell'eventualità di una mossa legale da parte dell'utente a cui segue una mossa dell'avversario.

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
