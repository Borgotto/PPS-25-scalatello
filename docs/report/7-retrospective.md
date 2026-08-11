# Retrospettiva

Il processo di sviluppo seguito ci ha permesso di cimentarci in un progetto ben strutturato e simile a progetti lavorativi futuri.

Averlo seguito ci ha permesso di avere uno sviluppo con un andamento lineare, senza avere picchi di lavoro più intenso.

Il [design architetturale](./3-architectural-design.md) del dominio è stato deciso durante un incontro iniziale con la collaborazione di tutti i componenti.

Il lavoro invece è stato suddiviso, in modo equo, per decidere il [design di dettaglio](./4-detailed-design.md) dei diversi aspetti del dominio, per poi, di conseguenza, implementarli.

Ad inizio progetto, seguendo il processo di sviluppo da noi scelto, abbiamo anche prefissato i diversi sprint in cui suddividerlo. Essi sono stati ampiamente rispettati, avendo alla fine degli stessi dei risultati concreti e verificabili anche grazie alla tecnica **TDD** da noi seguita.

Anche i [requisiti](./2-requirements.md) da noi prefissati sono stati pienamente raggiunti, sia grazie all'aver rispettato gli sprint, ma anche grazie alla collaborazione tra tutti i componenti del gruppo in caso di necessità.

Nel complesso ci riteniamo soddisfatti del risultato finale e del processo da noi seguito che ci ha permesso di arrivare ad esso.

## Possibili sviluppi futuri

- aggiungere nuove forme per la scacchiera;
- aggiungere nuove modalità di gioco;
- rendere la [logica indipendente dal giocatore](#logica-indipendente-dal-giocatore);
- [aggiungere una GUI](#interfaccia-grafica).

### Logica indipendente dal giocatore

La classe `Player` e `PlacementStrategy` erano state inizialmente progettate per essere agnostiche al contesto in cui vengono utilizzate, ma la loro implementazione è stata successivamente vincolata al solo contesto del giocatore artificiale.

L'implementazione originale vedeva l'attributo `placementStrategy` nel `trait Player` e l'utente conteneva una `UserPlacementStrategy` mentre il giocatore artificiale conteneva una `OpponentPlacementStrategy`.\
La strategia di posizionamento del giocatore non era altro che l'identità della posizione scelta dall'utente attraverso la *view*.

Questa astrazione avrebbe permesso di avere una logica che potesse svolgere i turni con contesti diversi (la scelta del giocatore umano e la scacchiera per l'avversario artificiale) ed eseguire la stessa funzione indipendentemente di quale tipo di giocatore fosse il turno.

Definita la strategia in questo modo:

```scala
trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

class UserPlacementStrategy extends PlacementStrategy[Position, Position]:
  def computePlacement(using userChoice: Position): Position = userChoice

trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position]
```

L'uso dei **contextual parameters** permette di usare le strategie senza dover conoscere il tipo concreto in questo modo:

```scala
// Give context to the strategies
given Board = board
given Position = playerChoice
// Whether the player is a `User` or an `Opponent`, we can compute the placement
val position = player.strategy.computePlacement
```

Per semplicità e per mancanza di tempo, il gruppo ha deciso di non implementare questa astrazione, ma di rimuovere l'attributo `placementStrategy` dal `trait Player` e di spostarlo nella classe `Opponent`.

È possibile visionare la parte di codice relativa alle strategie nei primi commit del [*branch* `feature/placement-strategy`](https://github.com/Borgotto/PPS-25-scalatello/tree/feature/placement-strategy).

### Interfaccia grafica

Visto che l'obiettivo principale del progetto era quello di sviluppare un programma funzionale e seguendo il processo TDD, oltre a questioni di tempo, il gruppo ha preferito concentrarsi sulla realizzazione di una interfaccia testuale.

Un prototipo funzionante di interfaccia grafica è stato comunque realizzato, ma non terminato, il codice è presente nel [*branch* `feature/gui` della repository](https://github.com/Borgotto/PPS-25-scalatello/tree/feature/gui).

L'interfaccia grafica è stata realizzata con la libreria [scala-swing](https://github.com/scala/scala-swing), un wrapper per la libreria Java Swing, che permette di realizzare interfacce grafiche in Scala.
