# Processo di sviluppo

Per la realizzazione del progetto, il gruppo ha adottato un processo di sviluppo Agile ispirato al framework [Scrum](https://it.wikipedia.org/wiki/Scrum_(informatica)). Di seguito, sono descritti i dettagli del processo adottato.

## Ruoli

Ciascun membro del gruppo, oltre al ruolo di sviluppatore, ha svolto uno specifico ruolo nell'ottica della simulazione di un'interazione semplificata ma realistica tra stakeholder e team di progetto. Nello specifico, sono stati definiti i seguenti ruoli.

### Committente

Uno dei membri del gruppo ha avuto il ruolo di committente del progetto, svolgendo i seguenti compiti.

- Comunicazione dei requisiti di progetto, poi formalizzati nella sezione ["Requisiti"](./3-requirements.md).
- Verifica del risultato prodotto al termine di ogni fase (detta _iterazione_) del processo di sviluppo, nell'ottica di garantirne la qualità e la conformità alle proprie aspettative, fornendo anche feedback utili a tale scopo per le iterazioni successive.
- Valutazione e accettazione del risultato finale del progetto.

Tale ruolo è stato assegnato a Cristina Zoccola, poiché è stata la componente del gruppo che ha avanzato l'idea fondante del progetto e che ha più esperienza con il dominio in oggetto.

### Product Owner

Il Product Owner ha avuto il ruolo di coordinatore all'interno del team di sviluppo. Nello specifico, il Product Owner si è occupato dei seguenti compiti.

- Redazione del Product Backlog, ossia dell'elenco di attività da svolgere nell'ambito del progetto, inclusa la stima di tempi e priorità per ciascuna attività e l'assegnazione di ciascuna attività ai componenti del team di sviluppo.
- Comunicazione con il committente, per la raccolta dei requisiti a inizio progetto e, al termine di ogni iterazione, dei feedback sul risultato presentato.
- Coordinamento del lavoro tra i componenti del team di sviluppo.

Tale ruolo è stato assegnato a Elena Boschetti.

### Scrum Master

Lo Scrum Master ha avuto il ruolo di supervisore del processo di sviluppo, al fine di garantirne l'efficienza e la conformità ai principi base del framework Scrum e alle modalità definite all'inizio del progetto.

Tale ruolo è stato assegnato a Emanuele Borghini.

## Organizzazione del lavoro

Le attività di progetto sono state suddivise in iterazioni (_dette sprint_), prevalentemente della durata di una settimana. In totale, sono stati pianificati 7 sprint.

Il primo sprint è stato dedicato alle seguenti attività.

- Definizione del processo di sviluppo, inclusa l'assegnazione dei ruoli indicati nella sezione ["Ruoli"](#ruoli).
- Formalizzazione dei requisiti (il cui risultato è riportato nel capitolo ["Requisiti"](./3-requirements.md)).
- Scelta degli strumenti da utilizzare per la pianificazione del lavoro e per le attività di sviluppo, quali testing, build e CI (Continuous Integration).
- Redazione del Product Backlog.

I 5 sprint successivi sono stati dedicati alle attività di progettazione, sviluppo e redazione della documentazione del codice. L'ultimo sprint è stato infine dedicato alla stesura della relazione del progetto.

All'interno di ogni sprint (ad esclusione del primo sprint organizzativo), sono stati svolti i seguenti incontri.

- **Sprint Planning**: incontro svolto all'inizio di ciascuno sprint finalizzato a definire le attività da portare a termine nello sprint stesso.
- **Daily Scrum**: incontri giornalieri, per lo più di breve durata, finalizzati ad aggiornare cisascun membro del team sullo stato attuale del proprio lavoro e a risolvere eventuali dubbi o questioni.
- **Sprint Review**: incontro svolto al termine di ciascuno sprint, finalizzato a valutare il lavoro svolto durante lo sprint stesso e a definire eventuali cambiamenti e miglioramenti per lo sprint successivo.

## Strumenti

A supporto del processo di sviluppo, sono stati adottati i seguenti strumenti.

- Come sistema di controllo di versione, è stato utilizzato **Git**.
- Per l'hosting remoto del repository, è stata utilizzata la piattaforma **GitHub**.
- Per la redazione del Product Backlog e l'organizzazione degli sprint, è stato utilizzato **GitHub Projects**, strumento integrato in GitHub.
- Come build system, è stato utilizzato **SBT**.
- Per il testing automatizzato, è stato utilizzato il framework **ScalaTest**. Inoltre, è stato utilizzato il framework **Mockito** per la creazione di [test doubles](https://en.wikipedia.org/wiki/Test_double) da utilizzare all'interno dei test nelle fasi iniziali dello sviluppo.
- Per la CI, sono state utilizzate le **GitHub Actions**, anch'esse integrate in GitHub.
