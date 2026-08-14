# Retrospettiva

## Documentazione di processo

La documentazione di processo, composta da backlog e report di ciascuno sprint, è disponibile alla [relativa pagina](../process/index.md).

## Commenti finali

Il processo di sviluppo adottato ha permesso di cimentarci in un progetto ben strutturato e più vicino alle dinamiche di un contesto lavorativo.

L'esperienza è stata positiva: l'organizzazione pianificata si è rivelata efficace, con una distribuzione del lavoro equa e sostenibile nel tempo a disposizione. Le scadenze di ciascuno sprint sono state sempre rispettate, ottenendo i risultati attesi al termine di essi. I requisiti individuati sono stati pienamente soddisfatti, grazie sia all'efficace organizzazione del lavoro che alla collaborazione tra tutti i componenti del gruppo in caso di necessità.

Nel complesso, ci riteniamo soddisfatti del risultato finale e del processo da noi seguito.

## Possibili sviluppi futuri

Alcune possibilità di evoluzione del progetto potrebbero essere le seguenti:

- l'aggiunta di nuove forme per la Board;
- l'aggiunta di nuove modalità di gioco (come la [modalità "a perdere"](https://it.wikipedia.org/wiki/Othello_%28gioco%29#Anti-reversi_o_Othello_a_perdere));
- rendere il compimento delle mosse indipendente dal tipo di giocatore;
- l'implementazione di un'interfaccia grafica.

### Compimento delle mosse indipendente dal tipo di giocatore

Durante la realizzazione del progetto, è stato individuato il possibile intervento migliorativo descritto nel seguito. Esso non è poi stato effettuato in considerazione delle tempistiche del progetto, lo riportiamo però come possibile sviluppo futuro.

L'attributo `placementStrategy` potrebbe essere spostato nell'interfaccia `Player` e l'utente umano (`User`) potrebbe essere dotato di una `UserPlacementStrategy`, che consisterebbe nella funzione identità della posizione scelta dall'utente.

Questa astrazione permetterebbe di avere una logica che gestisca i posizionamenti di ciascun giocatore mediante un unico metodo e in maniera astratta rispetto alla tipologia del giocatore (umano o virtuale).

La strategia sarebbe definita in questo modo:

```scala
trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

class UserPlacementStrategy extends PlacementStrategy[Position, Position]:
  def computePlacement(using userChoice: Position): Position = userChoice

trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position]
```

L'uso dei parametri contestuali permetterebbe poi di usare le strategie senza dover conoscere il tipo concreto, nel seguente modo:

```scala
// Give context to the strategies
given context: Board & Position = ... // union of current board and player choice

// Whether the player is a `User` or an `Opponent`, we can compute the placement
val placement: Position = player.strategy.computePlacement
```

### Interfaccia grafica

In virtù della sua maggiore essenzialità e della possibilità di avere maggiore tempo da dedicare ad altri aspetti del progetto, il gruppo ha preferito realizzare un'interfaccia da riga di comando piuttosto che un'interfaccia grafica.

Un prototipo funzionante di interfaccia grafica, seppur incompleto, è stato comunque realizzato; il codice è disponibile sul [branch `feature/gui`](https://github.com/Borgotto/PPS-25-scalatello/tree/feature/gui) del repository. Il prototipo è stato realizzato usando la libreria [scala-swing](https://github.com/scala/scala-swing), un wrapper Scala della libreria Java Swing.

[Indice](../index.md) | [Capitolo precedente](./6-testing/index.md)
