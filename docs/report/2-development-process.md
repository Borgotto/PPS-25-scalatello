# Processo di sviluppo

Il gruppo ha adottato un processo di sviluppo agile, basato sul framework Scrum

## Ruoli e responsabilità

Tutti i membri del gruppo hanno ricoperto il ruolo di sviluppatori, mentre i ruoli di Product Owner, Committente e Scrum Master sono stati ricoperti da membri del gruppo specifici.

### Ruolo di Committente

Cristina Zoccola, si occuperà di fornire le specifiche del progetto, di definire i requisiti e di approvare le soluzioni proposte dal gruppo.

### Ruolo di Product Owner

Elena Boschetti, si occuperà di gestire il backlog del progetto, di definire le priorità delle attività e di garantire che il team di sviluppo stia lavorando sulle funzionalità più importanti per il progetto.

### Ruolo di Scrum Master

Emanuele Borghini, si occuperà garantire che il team stia seguendo le pratiche Scrum.

## Organizzazione del lavoro

Il lavoro è stato suddiviso in sprint della durata di una settimana, sette sprint totali, durante i quali sono state svolte le seguenti attività:

### Meeting

Il gruppo ha deciso di stabilire due incontri settimanali, gli incontri di inizio e fine sprint sono stati organizzati in presenza per facilitare la comunicazione e le scelte decisionali.

#### Sprint Planning

Nel primo incontro della settimana, il gruppo programma le attività da svolgere durante lo sprint corrente.

#### Sprint Review

Nel secondo incontro della settimana, il gruppo presenta i risultati dello sprint corrente e discute eventuali task da riprendere, modificare, o rinviare allo sprint successivo.

#### Daily Scrum

Gli incontri giornalieri invece sono tenuti in modalità remota, risultando più comodi.

In questi incontri, ogni membro del gruppo condivide lo stato di avanzamento del proprio lavoro, eventuali problemi riscontrati e le attività da svolgere per il giorno successivo.

## Strumenti per lo sviluppo

- **Github Projects** è stato utilizzato per la gestione delle task del progetto, lo *sprint planning*, e la gestione del *backlog*.
- **GitHub Actions** è stato utilizzato per l'integrazione continua, la creazione di *release*, l'esecuzione dei test automatici, e la generazione della documentazione.\In particolare:
  - L'esecuzione automatica dei test è stata configurata per essere eseguita ad ogni push sul repository, o alternativamente come *pre-commit hook* locale.
  - La creazione di *release* è stata automatizzata in base ai nomi dei commit.

---

- Il framework **Scalatest** è stato utilizzato per la realizzazione e verifica degli *unit test*.

---

- **Mermaid** è stato utilizzato per la realizzazione dei diagrammi UML
