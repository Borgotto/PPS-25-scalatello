# Testing

## Modalità adottate

Il testing dell'applicazione è stato in larga parte automatizzato: nello specifico, sono state sottoposte a testing automatizzato tutte le entità di complessità non banale che compongono i moduli Model e Controller.

Il testing della View è invece stato svolto in maniera manuale, in ragione dell'alto livello di complessità del testing automizzato di un'interfaccia utente.

## Test Driven Development (TDD)

Per lo sviluppo delle parti sottoposte a testing automatizzato, è stato seguito l'approccio di **Test Driven Development (TDD)**, adottando il processo ciclico _Red-Green-Refactor_:

1. _Red_: viene scritto un test relativo a una funzionalità non ancora implementata, il quale inizialmente fallisce;
2. _Green_: viene implementata la quantità minima di codice necessaria a far passare il test;
3. _Refactor_: il codice viene riorganizzato e migliorato, assicurandosi che il comportamento verificato dai test rimanga invariato.

Si è ritenuto idoneo adottare tale approccio principalmente per due motivi:

- nel corso dell'evoluzione del codice, mantiene strettamente monitorata la correttezza del suo comportamento nei casi di test individuati;
- favorisce una valutazione incrementale e dettagliata dei casi di test possibili.

## Tecnologie utilizzate

Per la scrittura dei test automatizzati, è stato utilizzato il framework **ScalaTest**.

Inoltre, è stato utilizzato il framework **Mockito** per la definizione e l'utilizzo di test doubles. Nello specifico, è stato principalmente utilizzato per la creazione di _mock_ delle componenti non ancora integrate: al fine di poter testare componenti che richiedevano l'interazione con altri componenti ancora assenti o in fase di sviluppo, sono stati creati dei mock in sostituzione di tali dipendenze. I mock sono stati poi sostituiti con le componenti reali una volta terminato il loro sviluppo.

## Dettagli inerenti al testing

Ogni componente del gruppo riporta di seguito la parte di testing relativa alla propria parte di implementazione.

- [Emanuele Borghini](emanuele-borghini.md)
- [Elena Boschetti](elena-boschetti.md)
- [Cristina Zoccola](cristina-zoccola.md)
