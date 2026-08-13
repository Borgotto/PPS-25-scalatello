# Implementazione - Emanuele Borghini

## Lavoro svolto

- [Player](#player)
  - [User](#user)
  - [Opponent](#opponent)
- [PlacementStrategy](#placementstrategy)
  - [OpponentPlacementStrategy](#opponentplacementstrategy)
  - [StrategyComputations](#strategycomputations)
  - [StrategyHelper](#strategyhelper)
- [SaveManager](#savemanager)
  - [Serializer](#serializer)
  - [SaveError](#saveerror)
- [Conversioni implicite](#conversioni-implicite)
- [Ottimizzazione delle prestazioni](#ottimizzazione-delle-prestazioni)

## Aspetti implementativi rilevanti

---

### Player

<sup>[(link al codice completo)](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/model/Logic.scala)</sup>

---

Il `trait Player` definisce l'interfaccia comune a tutti i giocatori del gioco, indipendentemente dal fatto che siano controllati dall'utente o dal programma.

L'unico attributo che definisce un giocatore è il colore delle pedine che possiede, rappresentato dalla classe `Color`.\
Questa interfaccia verrà usata dalla **logica** di gioco per gestire le mosse, il turno e il punteggio dei giocatori.

```scala
trait Player:
  def color: Color
```

#### User

---

Un utente è un giocatore umano, che interagisce con il gioco tramite la *view*.

L'implementazione tramite `case class` serve al *controller* per poter fare pattern matching e richiedere la mossa che l'utente desidera fare attraverso la *view*.

```scala
case class User(color: Color) extends Player
```

#### Opponent

---

Un avversario invece è un giocatore artificiale, che svolge mosse in autonomia in base a una strategia definita.

I diversi livelli di difficoltà sono rappresentati tramite un `enum`.\
La strategia associata a ciascun livello viene determinata tramite pattern matching.\
In questo modo la selezione della strategia è localizzare interamente nella definizione di `Opponent` e l'implementazione della logica di gioco non deve conoscere le singole strategie disponibili.

```scala
enum Opponent extends Player:
  case ErraticOpponent(color: Color)
  case EasyOpponent(color: Color)
  case MediumOpponent(color: Color)
  case HardOpponent(color: Color)

  val strategy: OpponentPlacementStrategy = this match
    case ErraticOpponent(_) => ErraticPlacementStrategy(color)
    case EasyOpponent(_) => SmartPlacementStrategy(color, depth = 1)
    case MediumOpponent(_) => SmartPlacementStrategy(color, depth = 2)
    case HardOpponent(_) => SmartPlacementStrategy(color, depth = 4)
```

---

### PlacementStrategy

<sup>[(link al codice completo)](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/model/player/strategy)</sup>

---

La strategia di posizionamento è un `trait` che definisce il comportamento di un avversario in base a un dato contesto.

```scala
trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O
```

Il metodo `computePlacement` è una funzione `C => O`, che verrà eseguita dalla **logica** di gioco per calcolare la prossima mossa dell'avversario.

Il tipo generico `C` è dichiarato **controvariante** con lo scopo di permettere il passaggio di un tipo `C` più generico a una strategia che ne richiede uno più specifico, favorendo così il riuso in diversi contesti.

Inoltre grazie all'uso dei **contextual parameters**, è possibile definire nello *scope* delle classi chiamanti un contesto specifico attraverso un `given` e non doversi preoccupare di passarlo ogni volta che si invoca la strategia.

#### OpponentPlacementStrategy

---

Il `trait` che definisce le strategie di posizionamento degli avversari, estende l'interfaccia generica `PlacementStrategy` con la scacchiera (`Board`) come contesto e la posizione (`Position`) come output.

```scala
sealed trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position]
```

Sono state implementate due strategie di posizionamento, `RandomPlacementStrategy` e `SmartPlacementStrategy`, che rappresentano rispettivamente un avversario che posiziona le pedine in maniera casuale e un avversario che cerca di posizionare le pedine in maniera ottimale.

```scala
case class ErraticPlacementStrategy(color: Color) extends OpponentPlacementStrategy:
  ...

case class SmartPlacementStrategy(color: Color, depth: Int) extends OpponentPlacementStrategy:
  ...
```

#### StrategyComputations

---

Per sviluppare una strategia di posizionamento ottimale, ho implementato un algoritmo di ricerca *negamax* con *alpha-beta pruning* in maniera funzionale, senza l'uso di variabili mutabili, e utilizzando il costrutto `boundary/break` di Scala 3, che permette di interrompere anticipatamente l'iterazione dei branch quando il valore calcolato supera il limite beta.

Grazie a delle euristiche di valutazione delle mosse (punteggio, peso della posizione) e l'utilizzo del pruning dei branch, l'algoritmo riesce a cercare mosse ottimali ed efficientemente.

Questo algoritmo è usato per definire le difficoltà degli avversari in base alla profondità di ricerca, maggiore è la profondità, più difficile sarà l'avversario.

Per una spiegazione più dettagliata dell'algoritmo, fare riferimento a [wikipedia](https://en.wikipedia.org/wiki/Negamax_Negamax_algorithm).

```scala
  object StrategyComputations:
    def negamax(board: Board, depth: Int, color: Color)
                      (using alpha: Int = Int.MinValue + 1, beta: Int = Int.MaxValue): Int =
      val availablePlacements = board.getAvailablePlacements(color)
      val nodeIsTerminal = availablePlacements.isEmpty

      if depth == 0 || nodeIsTerminal then
        board.score(color)
      else
        boundary: // boundary for pruning
          availablePlacements.foldLeft(alpha): (currentAlpha, position) =>
            val newBoard = board.placeDisk(color, position, validatePosition = false)
            val value = -negamax(newBoard, depth - 1, color.opposite)(using -beta, -currentAlpha)
            if value >= beta then
              break(value) // stop searching this branch
            max(value, currentAlpha)
```

Notare l'uso di `using` per i parametri `alpha` e `beta`, che permette di passare i valori correnti di alpha e beta ai branch successivi senza doverli specificare esplicitamente in ogni chiamata ricorsiva.

Inoltre impostando i valori di default a `Int.MinValue + 1` e `Int.MaxValue`, si rimuovono i parametri anche dalla chiamata iniziale, rendendo il codice ancora più pulito.

```scala
(using alpha: Int = Int.MinValue + 1, beta: Int = Int.MaxValue)
```

#### StrategyHelper

---

A supporto di questo algoritmo è stata definita la classe `StrategyHelper`, che usa gli ***extension methods*** di Scala 3 per aggiungere dei metodi alle classi `Board` e `Position`.

Le operazioni utilizzate esclusivamente dall'algoritmo non appartengono al dominio generale di `Board` e `Position`, ma rimangono localizzate nel modulo delle strategie, permettendo di mantenere l'algoritmo di ricerca pulito e leggibile senza impattare le classi originali.

```scala
  private object StrategyHelper:
  ...

  extension (pos: Position)(using shape: Shape)
    private def weight: Int = ...

  extension (board: Board)(using shape: Shape)
    private def score(color: Color): Int = ...
```

---

### SaveManager

<sup>[(link al codice completo)](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/controller/save)</sup>

---

Il `trait SaveManager` serve a definire un gestore di salvataggio generico, che può salvare e caricare dati di qualsiasi tipo `C` in un percorso specificato dall'istanza che lo implementa.

L'interfaccia implementa il *pattern **adapter*** attraverso il *contextual parameter* `Serializer[C]`, separando la gestione dei file dalla conversione del dominio nel formato utilizzato per la persistenza, delegando ad esso le operazioni di codifica e decodifica.

```scala
trait SaveManager[C]
    (val savePath: Path)
    (using serializer: Serializer[C]):
  def save(data: C)(using filePath: Path): Try[_] = ...
  def load(using filePath: Path): Try[C] = ...
  def saveFileNames: Seq[String] = ...
  def deleteSaveFile(using filePath: Path): Try[Unit] = ...
```

L'implementazione concreta per salvare e caricare i dati di gioco è definita nella classe `MatchStateSaveManager`, che implementa il `trait SaveManager` con il tipo `MatchState`:

```scala
class MatchStateSaveManager(override val savePath: Path)
  extends SaveManager(savePath)
  (using Serializers.MatchSerializer)
```

#### Serializer

---

Il `trait Serializer` è un'interfaccia che espone funzioni di serializzazione `encode` e deserializzazione `decode` per un tipo generico `Class`.

```scala
trait Serializer[Class](
  val encode: Class => String,
  val decode: String => Class
)
```

L'implementazione di `MatchSerializer` per i dati di gioco sfrutta la libreria `upickle` per convertire i dati in JSON e viceversa:

```scala
import upickle.default.{read, write}
class MatchSerializer extends Serializer[MatchState](write(_), read(_))
```

Per l'utilizzo di questa libreria è stato necessario aggiungere la clausola `derives ReadWriter` alle classi:

- `ActivePlayer`
- `Color`
- `MatchStatus`
- `Position`
- `Shape`
- `Opponent`
- `OpponentPlacementStrategy`
- `User`
- `DiskState`

per permettere a `upickle` di generare automaticamente conversioni per queste classi.

#### SaveError

---

Per rappresentare in maniera esplicita i possibili errori delle operazioni di input/output, è stato definito l'**ADT** `SaveError`.

Le operazioni del `SaveManager` utilizzano la classe `Try` per rappresentare un risultato che può contenere un successo oppure un'eccezione, mentre `SaveError` permette di distinguere le diverse categorie di errore che possono verificarsi durante la lettura, scrittura, decodifica o cancellazione dei dati di gioco.

In questo modo l'errore non viene propagato implicitamente attraverso l'eccezione, e le funzioni rimangono "pure", senza *side effects*.

```scala
enum SaveError extends Throwable:
  case WriteError(cause: Throwable)
  case ReadError(cause: Throwable)
  case DecodeError(cause: Throwable)
  case DeleteError(cause: Throwable)

private object SaveError:
  def handleSaveErrors(cause: Throwable): WriteError = ...
  def handleLoadErrors(cause: Throwable): ReadError | DecodeError = ...
  def handleDeleteErrors(cause: Throwable): DeleteError = ...
```

---

### Conversioni implicite

<sup>[(link al codice completo)](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/domain/Position.scala#L105-L114)</sup>

---

Per ridurre il *boilerplate* delle operazioni sulla classe `Position`, sono state definite **conversioni implicite** di diverso tipo:

- Da `Tuple2[Int, Int]` a `Position`;

  ```scala
  object Position:
    ...
    given Conversion[(Int, Int), Position] = (x, y) =>
      Position(x, y)
    ...
  ```

- da `Tuple2[String, String]` a `Position`;

  ```scala
  object Position:
    ...
    given Conversion[(String, String), Position] = (x, y) =>
      (x.toInt, y.toInt)
    ...
  ```

- da `String` a `Position`.

  ```scala
  object Position:
    ...
    given Conversion[String, Position] = s =>
      val parts = s.split(",").map(_.trim)
      (parts(0), parts(1))
    ...
  ```

Queste conversioni riducono da quello che sarebbe un codice più verboso con la sintassi di un ipotetico costruttore o extension method, a una sintassi più concisa e leggibile, come mostrato nell'esempio seguente:

Esempio di creazione di una scacchiera iniziale:

```scala
// con costruttore
Position(1, 1) -> W
Position(1, 2) -> B
Position(2, 1) -> B
Position(2, 2) -> W
// oppure con extension method
"(1, 1)".toPosition -> W
"(1, 2)".toPosition -> B
(2, 1).toPosition -> B
(2, 2).toPosition -> W
```

```scala
// con conversioni implicite
(1, 1) -> W
(1, 2) -> B
(2, 1) -> B
(2, 2) -> W
// oppure
"(1, 1)" -> W
"(1, 2)" -> B
"(2, 1)" -> B
"(2, 2)" -> W
```

---

### Ottimizzazione delle prestazioni

<sup>[(link al codice completo)](https://github.com/Borgotto/PPS-25-scalatello/commit/080d7215605b79d2ce0829a1ca80bad843b31c1e)</sup>

---

L'implementazione iniziale è stata sottoposta a test delle prestazioni ([come descritto nella sezione successiva](../6-testing/emanuele-borghini.md#test-delle-prestazioni)) riguardo il calcolo delle mosse dell'avversario.

I test hanno evidenziato che alcuni metodi della `Board` e l'esplorazione dell'albero di gioco rappresentavano i principali colli di bottiglia.\
Le prestazioni del gioco non rientravano in un range di aspettative accettabile da un utente, con tempi di risposta dell'avversario troppo lunghi per le difficoltà più elevate.

Sono stati quindi introdotti i seguenti miglioramenti al codice al fine di migliorare le prestazioni:

- [nel package `board`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/model/board)
  - Riscritto il metodo `BoardComputations.getCapturableNeighboursPair()` per ridurre il costo computazionale da $O(n^2)$ a $O(n)$, dove $n$ è il numero di celle della scacchiera.
  - Riscritto il metodo `BoardComputations.placeDisk._captureDisks()` per rimuovere l'iterazione non necessaria di tutti i dischi, ma solo quelli che sono stati catturati.
  - Riscritto il metodo `Board.equals()` rimuovendo allocazioni non necessarie di oggetti `DiskState`
  - Aggiunto un parametro booleano `validatePosition` al metodo `Board.placeDisk()` per disabilitare la validazione della posizione quando non necessaria.
  - Impostato il valore `Board.state` a `lazy val` per evitare di ricalcolare lo stato quando non necessario.

- [nel package `strategy`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/model/player/strategy)
  - Ottimizzato l'algoritmo di ricerca `StrategyComputations.negamax()` con *alpha-beta pruning* come descritto nella [sezione precedente](#strategycomputations).
  - Parallelizzazione dell'algoritmo tramite l'uso di `par` della libreria `scala-parallel-collections`

    La valutazione delle diverse possibili mosse è indipendente: ogni ramo dell'albero di ricerca viene valutato a partire da una copia immutabile della `Board`.\
    È quindi possibile parallelizzare la valutazione delle mosse senza introdurre sincronizzazione tra i diversi calcoli.

    ```scala
    object StrategyComputations:
      def calculateBestPlacement(color: Color, depth: Int)
                                (using board: Board): Position =
        val availablePlacements = board.getAvailablePlacements(color)

        // Evaluate positions in parallel, using original index for deterministic tie-breaking
        val zippedPlacements = availablePlacements.zipWithIndex
        val evaluatedMoves = zippedPlacements.par.map((position, index) =>
          val newBoard = board.placeDisk(color, position, validatePosition = false)
          val score = negamax(newBoard, depth - 1, color.opposite)
          (position, score, index)
        )

        val bestMove: Position = evaluatedMoves.seq.minBy((_, score, index) => (score, index))._1
        bestMove
      ```
