# Implementazione - Elena Boschetti

## Lavoro svolto

Nella fase implementativa del progetto, ho realizzato le seguenti parti:

- la [logica di gioco](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/model/Logic.scala);
- le strutture dati dedicate alla memorizzazione dello stato di una partita ([package `state`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/state)) e alla rappresentazione di concetti di dominio usati da tutta l'applicazione ([package `domain`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/domain), ad esclusione della classe `Position`);
- tutta la View ([package `view`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/view)), inclusa l'attuazione del pattern Observer per la notifica alla View degli aggiornamenti di stato di una partita, realizzando le interfacce `Publisher` e `Subscriber` ([package `observer`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/observer)) e la loro implementazione da parte di View e [Controller](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/controller/Controller.scala#L67-L74).

## Aspetti implementativi rilevanti

### Factory methods per la creazione di istanze di `Logic`

Allo stato attuale dell'applicazione, si hanno tre scenari possibili per la creazione di un'istanza di `Logic`.

- Se ne crea un'istanza per iniziare una nuova partita a partire dal normale stato iniziale previsto dalle regole del gioco.
- Se ne crea un'istanza per iniziare una partita a partire da uno stato salvato su file.
- Al fine di facilitare il testing, è molto utile poterne creare un'istanza che abbia una specifica configurazione di `Board` diversa da quella iniziale.

Per ciascuno di questi scenari, è stato definito un factory method, per mezzo dell'overloading del metodo `apply` del companion object di `Logic`.

Si evidenza inoltre che il factory method per il terzo scenario sopra elencato, essendo solo per scopi di testing, è stato dichiarato package-private, in modo da prevenirne l'uso improprio nel codice di produzione e, al tempo stesso, consentirne l'uso nel package corrispondente nel codice di test.

[Link alla definizione dei factory method](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/model/Logic.scala#L125-L167)

### Parametri contestuali nella View

All'interno della View, ci sono due entità che sono estensivamente utilizzate:

- il provider per la localizzazione delle stringhe (`I18n`);
- l'helper per le operazioni di I/O (`InputComponent`), che peraltro utilizza esso stesso il provider per la localizzazione.

Poiché tali entità sono necessarie a tutti i `CLIScreen` che compongono la View, passarle come parametro sarebbe una ridondanza evidente.

Si è quindi ritenuto opportuno dichiarare queste due entità come parametri contestuali. I parametri sono comunque dichiarati nella signature del costruttore di ogni classe in cui sono utilizzati, specificando però la loro natura contestuale per mezzo della keyword `using` (come fatto ad esempio [qui](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/cli/screens/MainMenuScreen.scala#L17)), mentre la loro definizione è effettuata una sola volta per mezzo delle rispettive clausole `given` ([una per `I18n`](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/Scalatello.scala#L13) e [una per `InputComponent`](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/cli/CLIView.scala#L27)).

### Extension methods

Per il rendering della Board su terminale a partire dal `BoardState` ricevuto dalla View, è stato definito un extension method su `BoardState`. Si è ritenuto opportuno definirlo come extension method all'interno del package `view` e non direttamente come metodo di `BoardState` in quanto è una responsabilità strettamente legata alla View.

Inoltre, è stato definito un altro extension method per ottenere una lista di stringhe localizzate a partire dalla lista delle loro chiavi, in modo da localizzare più stringhe con un'unica chiamata ove utile (ad esempio, per la localizzazione delle opzioni di un menu).

Link al codice:

- [Extension method per il rendering della Board](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/cli/io/BoardRendering.scala#L10-L42)
- [Extension method per la localizzazione di una sequenza di stringhe](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/i18n/I18n.scala#L24-L30)

### Funzioni monadiche e for-comprehension

Come accennato nel design di dettaglio della View, le funzioni di I/O su terminale adottano uno stile monadico: non eseguono direttamente l'operazione per poi ritornarne il risultato, bensì incapsulano l'operazione da eseguire all'interno di una monade (`IO`).

In concomitanza con questo approccio, è stato utilizzato il costrutto di for-comprehension, che fornisce una sintassi più pulita per la concatenazione di funzioni monadiche.

I costrutti appena citati sono stati utilizzati in maniera estesa nell'implementazione della View; [qui](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/cli/io/InputComponent.scala#L57-L74) è possibile visualizzarne un esempio di utilizzo.

**Nota:** si segnala che il codice che implementa la type class [Monad](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/utils/Monad.scala) e la monade [IO](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/view/cli/io/IO.scala) è stato preso dal materiale del corso realizzato dal prof. Mirko Viroli.
