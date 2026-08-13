# Design di dettaglio

## Model

### Struttura complessiva

Per prima cosa, in questa fase sono state maggiormente dettagliate le interazioni tra le principali entità del Model, già delineate in precedenza. Il seguente diagramma mostra proprietà e metodi esposti da ciascuna entità.

```mermaid
classDiagram
  class Logic {
    + state: MatchState
    + placeUserDisk(position: Position) Logic
    + placeOpponentDisk() Logic
  }
  class PlacementStrategy {
    + computePlacement(board: Board) Position
  }
  class Opponent {
    + strategy: PlacementStrategy
  }
  class User
  class Board {
    + state: BoardState
    + getAvailablePlacements(diskColor: Color) Set~Position~
    + isPlacementValid(diskColor: Color, diskPosition: Position) Boolean
    + placeDisk(diskColor: Color, diskPosition: Position) Board
  }
  class Disk {
    + color: Color
    + flip() Disk
  }
  class Color {
    <<enumeration>>
    Black
    White
  }
  class Player {
    + color: Color
  }

  Logic ..> PlacementStrategy: applies
  Logic *-- Opponent
  Logic *-- User
  Logic *-- Board

  Opponent --> PlacementStrategy
  Opponent --|> Player
  User --|> Player
  Player --> Color: is assigned
  PlacementStrategy ..> Board
  Board *-- "1..*" Disk
  Disk --> Color: has
```

Come già citato nell'analisi di dominio, l'avversario (`Opponent`) effettua le proprie mosse seguendo una specifica strategia (`PlacementStrategy`), rappresentabile come una funzione che prende in input lo stato corrente del terreno di gioco (`Board`) e restituisce in output la posizione in cui effettuare la mossa, decisa secondo uno specifico algoritmo. L'applicazione di tale funzione è effettuata dalla `Logic` ad ogni turno dell'avversario virtuale.

Oltre all'applicazione della strategia dell'avversario per conoscere la sua prossima mossa, la `Logic` deve comunicare con la `Board` anche per le seguenti operazioni:

- ottenerne lo stato (necessario affinché la `Logic` possa restituire lo stato completo della partita);
- sapere se il posizionamento di un disco di un certo colore in una specifica posizione è valido;
- posizionare un disco (operazione che implica anche il rovesciamento dei dischi catturati, effettuato dalla `Board`);
- conoscere i posizionamenti validi che un giocatore può effettuare dato lo stato corrente della `Board` (sia per stabilire se un giocatore non ha mosse valide disponibili, sia affinché possano essere fornite all'utente le mosse valide che può effettuare).

Da notare che il metodo `placeDisk()` della `Board`, che ne modifica lo stato, restituisce una nuova istanza di `Board`, coerentemente con il principio di immutabilità adottato anche per la `Logic`; analogo principio è stato adottato per il metodo `flip()` dei `Disk` (che ne effettua il rovesciamento).

Nel diagramma sottostante è inoltre dettagliata la struttura di `MatchState` (che descrive uno stato della partita) e delle sue sottoparti.

```mermaid
classDiagram
    class MatchState {
      <<record>>
      + status: MatchStatus
      + user: User
      + opponent: Opponent
      + activePlayer: ActivePlayer
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
      <<record>>
      + shape: Shape
      + disks: Set~DiskState~
      + userAvailablePlacements: Set~Position~
    }
    class Shape
    class Square {
      + size: int
    }
    class Rectangle {
      + height: int
      + width: int
    }
    class DiskState {
      <<record>>
      + color: Color
      + position: Position
    }
    class Color {
      <<enumeration>>
      Black
      White
    }
    class Position {
      <<record>>
      + row: int
      + column: int
    }
    class ActivePlayer {
      <<enumeration>>
      User
      Opponent
    }

    MatchState *-- BoardState
    MatchState --> MatchStatus
    BoardState *-- DiskState
    Shape <|-- Square
    Shape <|-- Rectangle
    BoardState --> Shape
    DiskState --> Color
    DiskState --> Position
    BoardState --> Position
    MatchState --> ActivePlayer
```

### Board e Disk

`Disk` è un componente del Model che modella le pedine (o dischi) del gioco.

```mermaid
classDiagram
  class Disk {
    <<interface>>
    + color: Color
    + flip() Disk
  }
```

Nello specifico:

- `color` restituisce il suo colore;
- `flip()` capovolge il disco (cambiandone il colore).

Quando viene eseguito un `flip()` viene creato un nuovo disco con il colore presente sull'altra faccia del disco da capovolgere.

Questo permette anche di mantenere facilmente l'immutabilità dei dischi, evitando possibili *side-effect*.

`Board` è un componente del Model che modella la scacchiera su cui si svolge la partita.

```mermaid
classDiagram
  class Board {
    <<interface>>
	  ~ shape: Shape
    ~ disks: Map~Position, Disk~
    + state: BoardState
    + getAvailablePlacements(color: Color) Set~Position~
    + isPlacementValid(position: Position, color: Color) Boolean
    + placeDisk(position: Position, color: Color) Board
  }
  class BoardComputations {
    + getAvailablePlacements(color: Color) Set~Position~
    + isPlacementValid(position: Position, color: Color) Boolean
    + placeDisk(position: Position, color: Color) Board
  }
  Board --> BoardComputations: delegates
```

In particolare:

- `shape` è la forma della scacchiera;
- `disks` sono tutti i dischi presenti sulla scacchiera;
- `state` è lo stato attuale della scacchiera;
- `getAvailablePlacements()` restituisce tutte le posizioni delle mosse valide del giocatore specificato;
- `isPlacementValid()` controlla se la mossa selezionata è valida in base alle regole del gioco;
- `placeDisk()` posiziona un nuovo disco sulla scacchiera, capovolgendo poi i dischi catturati da quest'ultimo, restituendo così una nuova scacchiera con le informazioni aggiornate.

Eseguendo `placeDisk()` viene creata una nuova `Board` invece che aggiornare quelle attuale, questo viene fatto per mantenere l'immutabilità della `Board` e quindi garantire l'eliminazione di *side-effect*.

Nell'implementazione della Board viene utilizzato il design pattern: **delegation pattern**:

- la `Board` delega i calcoli associati alle sue operazioni alla classe `BoardComputations`;
- nello specifico il pattern viene applicato sia delegando le operazioni a `BoardComputations`, sia passando a quest'ultima un riferimento alla `Board` per permetterle di operare sull'istanza corrente della stessa.

### Giocatori

La struttura del componente Player è stata progettata come un'interfaccia, che rappresenta un giocatore generico, che può fare scelte di posizionamento dei dischi sulla scacchiera.

```mermaid
classDiagram
    class Player <<Interface>> {
        + color: Color
    }
    class User
    class Opponent <<Enumeration>> {
        + strategy: PlacementStrategy
        + ErraticOpponent(color: Color) Opponent
        + EasyOpponent(color: Color) Opponent
        + MediumOpponent(color: Color) Opponent
        + HardOpponent(color: Color) Opponent
    }

    Player <|-- User
    Player <|-- Opponent
```

### Strategie dell'avversario

La placement strategy è un'interfaccia che permette di definire il comportamento di un giocatore.

Per l'avversario virtuale, la placement strategy consiste nel calcolare la mossa da eseguire in base a uno stile di gioco predefinito, come ad esempio massimizzare il numero di pedine catturate o minimizzare il numero di pedine catturate dall'avversario.

- Pattern Strategy

  La "strategia di mossa" consiste nel calcolare come il giocatore sceglie la posizione in cui piazzare il disco.\
  È una funzione che data un'informazione restituisce la posizione in cui il giocatore vuole piazzare il disco.

  Questo approccio permette di separare la logica del gioco dalla logica decisionale dei giocatori, rendendo più semplice l'implementazione di diversi tipi di avversari virtuali con differenti stili di gioco.

```mermaid
classDiagram
    class PlacementStrategy~-C, O~ <<Interface>> {
        + computePlacement(using context: C)* O
    }
    class OpponentPlacementStrategy ~Board, Position~ {
    }
    class ErraticPlacementStrategy {
        + color: Color
        + computePlacement(using board: Board): Position
    }
    class SmartPlacementStrategy {
        + color: Color
        + depth: Int
        + computePlacement(using board: Board) Position
    }

    PlacementStrategy <|.. OpponentPlacementStrategy
    OpponentPlacementStrategy <|.. ErraticPlacementStrategy
    OpponentPlacementStrategy <|.. SmartPlacementStrategy
```

#### Scenario: calcolo delle mosse

```mermaid
sequenceDiagram
    actor User
    box View
    participant View
    end
    box Controller
    participant Controller
    end
    box Model
    participant Logic
    participant Player
    participant Board
    end
    User->>View: chooses a position
    View->>Controller: handleSelection(userChoice)
    Controller->>Logic: placeDisk(userChoice)
    Logic-->>Logic: get Opponent player
    Logic->>Player: get placement strategy
    Player-->>Logic: PlacementStrategy
    Logic-->>Logic: computePlacement()
    Note over Logic, Board: placement then follows<br> the board diagram below
```

## Controller

`Controller` è un componente del Controller che si occupa del coordinamento della partita.

```mermaid
classDiagram
	class Controller {
		<<interface>>
		+ startMatch(shape: Shape, color: Color, opponent: OpponentType)
		+ handleSelection(position: Position)
		+ saveMatch(fileName: String)
		+ loadMatch(fileName: String) MatchState
		+ saveFileNames() Seq[String]
		+ deleteSaveFile(fileName: String)
	}
```

In dettaglio:

- `startMatch()` crea una nuova partita istanziando tutti i componenti necessari; 
- `handleSelection()` si occupa di gestire la posizione in cui l'utente vuole posizionare un nuovo disco e gestisce il turno dell'avversario di conseguenza;
- `saveMatch()` salva lo stato della partita attuale;
- `loadMatch()` carica il salvataggio di una partita;
- `saveFileNames()` restituisce una sequenza contenente il nome dei *file* di salvataggio esistenti;
- `deleteSaveFile()` cancella il *file* di salvataggio richiesto.

### SaveManager

Il save manager è un componente del controller che si occupa di gestire il salvataggio e il caricamento delle partite.

Solo un'istanza del save manager è presente all'interno del controller.\
Questa istanza può salvare più partite, ognuna in un file separato all'interno di una cartella passata come parametro al momento della creazione del save manager.

- Pattern Adapter

    Nel modulo `SaveManager` viene usato il pattern ***adapter***.\
    Il modulo esporrà un'interfaccia unica per le operazioni di salvataggio e caricamento, delegando la conversione e la gestione dei formati specifici ad *adapter* concreti (es. JSON, XML, binario).

```mermaid
classDiagram
  class SaveManager <<Interface>> {
    + save(data)(filePath)
    + load(filePath)
    + saveFileNames() Seq[String]
    + deleteSaveFile(filePath)
  }
```

Data la natura delle operazioni di I/O, le chiamate al save manager possono fallire.\
Per questo motivo i metodi `save()`, `load()` e `deleteSaveFile()` in caso di fallimento, restituiranno un oggetto contenente un'eccezione che ne descrive il motivo.

#### Scenario: salvataggio di una partita

```mermaid
sequenceDiagram
    View->>Controller: saveMatch(filePath)
    Controller->>Logic: get state
    Logic->>Controller: MatchState
    Controller->>SaveManager: save(MatchState, filePath)
```

#### Scenario: caricamento di una partita

```mermaid
sequenceDiagram
    View->>Controller: loadMatch(filePath)
    Controller->>SaveManager: load(filePath)
    SaveManager->>Controller: MatchState
    Controller->>Logic: apply(MatchState)
```

## Propagazione degli aggiornamenti di stato di una partita

Come accennato nel capitolo precedente, uno degli obiettivi del design di dettaglio è stato trovare una soluzione che permettesse di notificare gli aggiornamenti di stato della partita alla View senza introdurre una dipendenza diretta dal Controller alla View.

Tale soluzione è stata individuata nel pattern Observer, applicato come segue.

Nell'atto di notifica di un aggiornamento, si distinguono due categorie di entità:

- l'entità che emette gli aggiornamenti (`Publisher`);
- le entità che devono essere notificate riguardo agli aggiornamenti pubblicati, le quali si registrano presso il `Publisher` (e pertanto sono dette `Subscriber`) in modo che esso possa notificarle di propria iniziativa nel momento in cui c'è un nuovo aggiornamento.

Nel contesto dell'architettura progettata, il ruolo di `Publisher` è ricoperto dal Controller, il quale riceve gli aggiornamenti di stato della partita dal Model, mentre la View ha il ruolo di `Subscriber`.

Quando si inizia una nuova partita, la View si registra come `Subscriber` presso il Controller attraverso il metodo `subscribe()`. Ad ogni turno, il Controller legge lo stato del Model, il quale lo espone mediante la proprietà `state` della `Logic`. Il Controller propaga quindi lo stato aggiornato ai `Subscriber` registrati attraverso il metodo `notifySubscribers()`, il quale chiama il metodo `update` di ogni `Subscriber`. Al termine della partita, la View si disiscrive dal Controller attraverso il metodo `unsubscribe()`.

Allo stato attuale, l'unico `Subscriber` previsto è la View ai fini del rendering dello stato della partita, ma si potrebbero aggiungere altri `Subscriber` per ulteriori funzionalità (ad esempio, un logger che registri tutti gli stati della partita su file, oppure un'entità di gestione dell'audio che riproduca effetti sonori ad ogni cambio di stato).

```mermaid
classDiagram
  class Publisher {
    <<interface>>
    + subscribe(subscriber: Subscriber)
    + unsubscribe(subscriber: Subscriber)
    # notifySubscribers(state: State)
  }
  class Subscriber {
    <<interface>>
    + update(state: State)
  }
  class Controller {
    <<interface>>
  }
  class View {
    <<interface>>
  }
  class Logic {
    <<interface>>
    + state: MatchState
  }

  Subscriber <|.. View
  Publisher <|.. Controller

  Publisher --> Subscriber: notifies

  View --> Controller: subscribes / unsubscribes to
  Controller --> Logic
```

## View

L'unica implementazione della View attualmente prevista è quella basata su command-line interface (`CLIView`).

Ai fini di una migliore ripartizione delle responsabilità, sono state adottate le seguenti scelte.

- Per ogni macro-funzionalità della View (ad esempio il menu iniziale, lo svolgimento di una partita, la creazione di un salvataggio e la gestione dei salvataggi effettuati), è definita un'implementazione di `CLIScreen` (tali implementazioni sono omesse dal diagramma che segue per brevità).
- Le operazioni di richiesta di input all'utente sono delegate ad una classe `InputComponent`, che implementa metodi riutilizzabili all'interno di tutta la View per ciascun pattern di richiesta presente nell'applicazione (ad esempio, un metodo per le richieste che chiedono all'utente di scegliere tra un elenco di opzioni proposte, oppure un metodo per le richieste che chiedono all'utente di confermare o rifiutare un'operazione).
- Per consentire all'utente, come da requisiti, di uscire dall'applicazione in qualsiasi momento, di salvare una partita o di abbandonare una partita senza salvare, sono state definite delle shortcut da tastiera. L'attivazione, la disattivazione e il rilevamento della digitazione delle shortcut sono delegati ad una classe `ShortcutManager`.
- Per le stringhe di testo presenti nella View, ai fini della possibilità di supportare altre lingue oltre all'inglese e di centralizzare la definizione delle stringhe, è stato definito il seguente meccanismo. Invece di mettere le stringhe di testo direttamente nel codice, ad ogni stringa è associata una chiave testuale (per mezzo di appositi file di localizzazione che contengono tutte le associazioni chiave-valore per ogni lingua), e tale chiave viene passata come parametro ad un provider che ne ritorna la traduzione corretta in base alla lingua impostata per l'applicazione. Il provider a cui è delegato questo processo di localizzazione è la classe `I18n`.

Inoltre, per le funzioni di I/O è stato adottato un tipo di ritorno monadico (per mezzo della monade `IO`). Tale scelta è stata presa in quanto rende il codice più puramente funzionale (poiché sposta i side-effect dati dalle operazioni di I/O all'esterno di ciascuna funzione).

```mermaid
classDiagram
  class View {
    <<interface>>
    + show()
    + update(state: MatchState)
  }
  class CLIView
  class I18n {
    + t(key: String) String
  }
  class InputComponent {
    + askForOption~T~(...) IO~T~
    + askForInteger(...) IO~int~
    + askForConfirmation(...) IO~bool~
    + askForFilename(...) IO~String~
  }
  class ShortcutManager {
    + enableAppExitShortcut()
    + enableMatchShortcuts()
    + disableMatchShortcuts()
  }
  class CLIScreen {
    <<interface>>
    + render() IO~Unit~
  }

  View <|.. CLIView

  CLIView --> I18n
  CLIView *-- InputComponent
  CLIView *-- "1..*" CLIScreen
  CLIView --> ShortcutManager
```

## Organizzazione del codice

Il diagramma sottostante raffigura la gerarchia di package prevista per l'organizzazione del codice, il cui contenuto è descritto nel seguito:

- `model` contiene il codice relativo al Model e include i seguenti subpackage:
  - `board` per il codice relativo alla Board, che include a sua volta un subpackage `computations` per il codice relativo ai calcoli legati alle operazioni della Board;
  - `player` per il codice relativo all'implementazione dei giocatori, che include a sua volta un subpackage `strategy` per il codice relativo alle strategie dell'avversario virtuale.
- `controller` contiene il codice relativo al Controller, incluso un subpackage `save` che contiene il codice relativo alla gestione dei salvataggi;
- `view` contiene il codice relativo alla View e include i seguenti subpackage:
  - `i18n` per il codice relativo alla localizzazione;
  - `cli` per il codice relativo all'implementazione della View su command-line interface (l'unica implementazione attualmente prevista), che include i seguenti subpackage:
    - `io` per il codice relativo alle operazioni di I/O;
    - `screens` per il codice relativo all'implementazione delle diverse parti dell'intefaccia.
- `state` contiene le strutture dati dedicate alla memorizzazione dello stato di una partita;
- `domain` contiene strutture dati che rappresentano concetti di dominio utilizzati da tutta l'applicazione (ad esempio, i concetti di colore, di forma della Board e di posizione sulla Board);
- `observer` contiene le interfacce utilizzate per attuare il pattern Observer;
- `utils` contiene altre utilities per cui non è stata individuata una locazione più specifica.

```mermaid
flowchart BT
  root
  model
  controller
  view
  domain
  state
  observer
  utils

  board
  player
  strategy
  computations

  save

  cli
  i18n
  io
  screens

  model --> root
  controller --> root
  view --> root
  state --> root
  domain --> root
  observer --> root
  utils --> root

  board --> model
  player --> model
  computations --> board
  strategy --> player

  save --> controller

  i18n --> view
  cli --> view
  io --> cli
  screens --> cli
```
