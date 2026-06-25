# Processo di sviluppo

Per la realizzazione del progetto, il gruppo ha adottato un processo di sviluppo Agile ispirato al framework [Scrum](https://it.wikipedia.org/wiki/Scrum_(informatica)). Di seguito, sono descritti i dettagli del processo adottato.

## Ruoli

Ciascun membro del gruppo, oltre al ruolo di sviluppatore, ha svolto uno specifico ruolo nell'ottica della simulazione di un'interazione semplificata ma realistica tra stakeholder e team di progetto. Nello specifico, sono stati definiti i seguenti ruoli.

### Committente

Uno dei membri del gruppo ha avuto il ruolo di committente del progetto, svolgendo i seguenti compiti.

- Comunicazione dei requisiti di progetto, poi formalizzati nella sezione ["Requisiti"](./3-requirements.md).
- Verifica del risultato prodotto al termine di ogni iterazione, nell'ottica di garantirne la qualità e la conformità alle proprie aspettative, fornendo anche feedback utili a tale scopo per gli sviluppi futuri. fornendo feedback utili a garantire la conformità alle specifiche e la qualità del risultato.
- Valutazione e accettazione del risultato finale del progetto.

Tale ruolo è stato assegnato a Cristina Zoccola, poiché è stata la componente del gruppo che ha avanzato l'idea fondante del progetto e che ha più esperienza con il dominio in oggetto.

### Product Owner

Il Product Owner ha avuto il ruolo di coordinatore all'interno del team di sviluppo. Nello specifico, il Product Owner si è occupatodei seguenti compiti.

- Redazione del Product Backlog, ossia dell'elenco di attività da svolgere nell'ambito del progetto, inclusa la stima di priorità e tempi per ciascun task individuato e l'assegnazione di ciascun task ai componenti del team di sviluppo.
- Comunicazione con il committente, per la raccolta dei requisiti e, al termine di ogni iterazione, dei feedback sul risultato presentato.
- Coordinamento del team di sviluppo in occasione dei meeting pianificati (riportati nella sezione ["Organizzazione del lavoro"](#organizzazione-del-lavoro)).

Tale ruolo è stato assegnato a Elena Boschetti.

### Scrum Master

Lo Scrum Master ha avuto il ruolo di supervisionare il processo di sviluppo, al fine di garantirne l'efficienza e la conformità ai principi base del framework Scrum e alle modalità definite all'inizio del progetto.

Tale ruolo è stato assegnato a Emanuele Borghini.

## Organizzazione del lavoro

Le attività di progetto sono state suddivise in iterazioni (_dette sprint_) della durata di una settimana. In totale, sono stati pianificati 8 sprint.

Il primo sprint è stato dedicato alle seguenti attività.

- Definizione in dettaglio del processo di sviluppo, inclusa l'assegnazione dei ruoli indicati nella [relativa sezione](#ruoli).
- Formalizzazione dei requisiti (il cui risultato è riportato nella sezione ["Requisiti"](./3-requirements.md)).
- Scelta degli strumenti da adottare per la pianificazione del lavoro e per le attività di sviluppo quali testing, build e CI (Continuous Integration).
- Redazione del Product Backlog.

Gli sprint successivi sono stati invece dedicati alle attività di progettazione, sviluppo e redazione della documentazione.

All'interno di ogni sprint (ad esclusione del primo sprint organizzativo), sono stati svolti i seguenti incontri.

- **Sprint Planning**: incontro svolto all'inizio di ciascuno sprint finalizzato a definire le attività da portare a termine nello sprint stesso.
- **Daily Scrum**: incontri giornalieri, per lo più di breve durata, finalizzati ad aggiornare cisascun membro del team sullo stato attuale del proprio lavoro e a risolvere eventuali dubbi o questioni.
- **Sprint Review**: incontro svolto al termine di ciascuno sprint, finalizzato a valutare il lavoro svolto durante lo sprint stesso e a definire eventuali cambiamenti e miglioramenti per lo sprint successivo.

## Strumenti

- **Github Projects** è stato utilizzato per la gestione delle task del progetto, lo _sprint planning_, e la gestione del _backlog_.
- **GitHub Actions** è stato utilizzato per l'integrazione continua, la creazione di _release_, l'esecuzione dei test automatici, e la generazione della documentazione. In particolare:
  - L'esecuzione automatica dei test è stata configurata per essere eseguita ad ogni push sul repository, o alternativamente come _pre-commit hook_ locale.
  - La creazione di _release_ è stata automatizzata in base ai nomi dei commit.

---

- Il framework **Scalatest** è stato utilizzato per la realizzazione e verifica degli _unit test_.

---

- **Mermaid** è stato utilizzato per la realizzazione dei diagrammi UML
