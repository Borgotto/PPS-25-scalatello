# Testing - Elena Boschetti

Per quanto concerne la mia parte di sviluppo, ho definito test automatizzati per la logica di gioco (`Logic`), raggruppati nella suite di test [`LogicTest`](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/test/scala/it/unibo/pps/model/LogicTest.scala).

I test sono stati scritti utilizzando lo stile `AnyFlatSpec`, scelto per la sua leggibilità, pulizia e semplicità di utilizzo.

Molti dei test hanno richiesto l'inizializzazione di `Logic` con una `Board` configurata diversamente rispetto alla sua normale configurazione iniziale, e alcuni di questi test hanno richiesto di verificare che lo stato della `Board` dopo una certa operazione sulla `Logic` fosse quello atteso; i test hanno quindi frequentemente richiesto di descrivere stati della `Board`. Fare ciò usando direttamente i factory method disponibili per la `Board` sarebbe stato però molto verboso e ripetitivo. Per questo motivo, ho definito un semplice DSL (Domain-Specific Language) per esprimere uno stato della `Board` mediante una rappresentazione testuale, la quale deve essere strutturata secondo le seguenti regole.

- Deve essere suddivisa in tante righe quante ne deve avere la `Board`.
- Ogni riga deve avere un numero di caratteri pari al numero di colonne che la `Board` deve avere (e deve quindi essere uniforme per tutte le righe).
- Ciascun carattere in posizione X su una riga Y descrive il contenuto della cella in posizione (X, Y) della `Board`. Sono utiilizzati i seguenti caratteri:
  - `B`: indica che la cella è occupata da un disco nero.
  - `W`: indica che la cella è occupata da un disco bianco.
  - `.`: indica che la cella è vuota.

Per fare un esempio, la configurazione iniziale di una `Board` 4x4 è così rappresentata:

```text
....
.WB.
.BW.
....
```

La rappresentazione testuale è trasformata in un'istanza di `Board` per mezzo dell'extension method `toBoard` definito nel codice di test ([link al metodo](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/test/scala/it/unibo/pps/testutils/TestExtensions.scala)).

[Indice](./index.md)
