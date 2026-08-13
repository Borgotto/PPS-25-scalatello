# Testing - Emanuele Borghini

## Indice

- [Metodologia di testing](#metodologia-di-testing)
  - [Uso di Mockito](#uso-di-mockito)
  - [Test parametrici tramite TableDrivenPropertyChecks](#test-parametrici-tramite-tabledrivenpropertychecks)
- [PlayerTest](#playertest)
- [PlacementStrategyTest](#placementstrategytest)
  - [Test delle prestazioni](#test-delle-prestazioni)
  - [Test comportamentale delle strategie](#test-comportamentale-delle-strategie)
- [SaveManagerTest](#savemanagertest)
- [SaveErrorTest](#saveerrortest)

## Metodologia di testing

### Uso di Mockito

Durante la prima fase di sviluppo, le mie componenti da implementare avevano bisogno di diverse dipendenze, tra cui:

- **Color**: per assegnare i colori ai giocatori e per interagire con la scacchiera;
- **Board**: per le mosse dei giocatori e per testare la validità delle mosse;
- **Controller** e **Matchstate**: per le funzioni di salvataggio e caricamento dei file.

Durante lo sviluppo delle diverse componenti, alcune dipendenze non erano ancora state implementate.\
Sono stati quindi definiti dei ***mock*** delle dipendenze, configurandoli solamente con le operazioni necessarie al test.\
Questo ha permesso ai membri del gruppo di sviluppare e testare le proprie componenti indipendentemente dallo stato di implementazione delle altre, parallelizzando lo sviluppo.

### Test parametrici tramite TableDrivenPropertyChecks

Il `trait TableDrivenPropertyChecks` è stato utilizzato per due motivi:

- ridurre la duplicazione del codice dei test, evitando di scrivere più volte lo stesso test con parametri diversi;
- esprimere un insieme di proprietà che devono essere soddisfatte da più classi indipendentemente dalla configurazione o dal tipo di dato utilizzato.

Gli input vengono organizzati all'interno di una `Table`, un insieme di test vengono poi eseguiti su ogni riga della tabella.

### Testing idiomatico

Per poter scrivere test con una sintassi molto simile al linguaggio naturale vengono usate queste funzioni di **ScalaTest**:

- il `trait AnyFlatSpec`;
- i `should` `Matchers` al posto di `assert`.

## PlayerTest

I test relativi ai giocatori verificano le proprietà necessarie alla corretta gestione del giocatore attivo da parte della classe `Logic`.\
In particolare, poiché `Logic` utilizza il *pattern matching* sul tipo concreto del giocatore per determinare il comportamento da adottare durante il turno, viene verificata la corretta rappresentazione dei diversi tipi di giocatore.

Per la classe `User` viene controllato il colore assegnatogli.

Per la classe `Opponent` viene controllato l'assegnamento dei colori e che le strategie di gioco siano assegnate correttamente.

## PlacementStrategyTest

Questo file contiene i test per la classe `PlacementStrategy`, che definisce le strategie di posizionamento dei pezzi sulla scacchiera.

Contiene una `Table` con diverse impostazioni di partenza per la scacchiera, un avversario e un tempo limite per il calcolo della mossa ottimale.

Un test condiviso esegue i seguenti controlli per ciascuna riga della tabella:

- ### Test delle prestazioni

  Un test importante che ho implementato è quello sulle prestazioni del calcolo della mossa ottimale nella classe `SmartOpponentStrategy`.

  Questo test non ha lo scopo di produrre un benchmark assoluto, ma di verificare che il calcolo della mossa ottimale non superi un tempo limite prestabilito, così da garantire una buona esperienza di gioco per l'utente, esente da lunghe attese.

  Per ridurre l'influenza della variabilità della singola esecuzione, il calcolo viene eseguito più volte e viene considerato il tempo medio.

  ```scala
  val numberOfRuns = 3
  val totalTime = (1 to numberOfRuns).map( _ =>
    val startTime = System.currentTimeMillis()
    opponent.strategy.computePlacement
    val timeTaken = System.currentTimeMillis() - startTime
    timeTaken
  )
  val averageTime = totalTime.sum / numberOfRuns
  averageTime should be < expectedTime
  ```

- ### Test comportamentale delle strategie

  Per verificare che l'algoritmo di calcolo della mossa ottimale funzioni correttamente, ho creato dei test che eseguono intere partite tra due avversari virtuali, dove uno dei due avversari utilizza una strategia inferiore e l'altro una strategia superiore.

  In questo modo è possibile verificare che l'avversario con la strategia superiore vinca la partita, e di conseguenza che l'avversario più debole perda.

  L'esecuzione dell'intera partita è stata implementata in un metodo condiviso tra i diversi test in maniera funzionale, calcolando ogni turno fino a quando non ci sono più mosse disponibili, e restituendo lo stato finale della partita.\
  In questo modo la simulazione non richiede una variabile mutabile per mantenere lo stato corrente della partita.

  ```scala
  def playWholeMatch(player: Opponent)(using difficulty: OpponentType): Logic =
    val logic = Logic(b.state.shape, player.color, difficulty)
    Iterator.iterate(logic)((turn: Logic) =>
      turn.state.activePlayer match
        case ActivePlayer.Opponent => turn.placeOpponentDisk()
        case ActivePlayer.User =>
          given Board = Board(turn.state.board)
          val position = player.strategy.computePlacement
          turn.placeUserDisk(position)
    ).dropWhile(_.state.status == InProgress)
    .next()
  ```

## SaveManagerTest

Anche per la classe `SaveManager` ho implementato dei test che utilizzano una `Table` contenente diversi input su cui testare le operazioni di salvataggio/caricamento/cancellazione dei file.

Ogni riga contiene:

- Una istanza di un `SaveManager` con una directory temporanea;
- Un `Serializer` a cui il `SaveManager` si appoggia per salvare e caricare i file;
- Una istanza di un dato da salvare.

Nel test condiviso vengono eseguiti i seguenti controlli per ciascuna riga della tabella:

- **Salvataggio**:
  - il contenuto del file deve corrispondere alla serializzazione del dato fornito;
  - il salvataggio non deve lanciare eccezioni;
  - un salvataggio successivo dello stesso dato deve sovrascrivere il file.
- **Caricamento**:
  - il contenuto del file deve corrispondere alla deserializzazione del dato;
  - il caricamento non deve lanciare eccezioni;
  - i dati devono combaciare con quelli salvati;
  - due caricamenti successivi dello stesso file devono restituire lo stesso risultato.
- **Cancellazione**:
  - il file deve essere cancellato correttamente;
  - la cancellazione non deve lanciare eccezioni.
- **Elenco dei file di salvataggio**:
  - dopo un salvataggio, il file deve comparire nella lista dei file salvati;
  - dopo una cancellazione, il file non deve più comparire nella lista dei file salvati;
  - elencare i file salvati non deve lanciare eccezioni.

## SaveErrorTest

La gestione degli errori del `SaveManager` viene testata verificando che le diverse condizioni di errore vengano correttamente convertite nelle rispettive varianti dell'**ADT** `SaveError`.

Questi test usano operazioni su filesystem disponibili su tutte le piattaforme target (Windows, Linux e MacOS) e permettono di testare le funzionalità della classe `SaveError` senza dover dipendere da un sistema operativo specifico.

I test controllano i seguenti casi:

- **Salvataggio**:
  - deve fallire con un `WriteError` se la directory non può essere scritta;
  - non deve lasciare cambiamenti sul file system se il salvataggio fallisce.
- **Caricamento**:
  - deve fallire con un `ReadError` se il file non esiste o non può essere letto;
  - deve fallire con un `DecodeError` se il file non ha il contenuto corretto.
- **Cancellazione**:
  - deve fallire con un `DeleteError` se il file non esiste o non può essere cancellato;
  - non può cancellare un file se non è un file di salvataggio valido.
