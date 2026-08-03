# Processo di sviluppo

Per la realizzazione del progetto, il gruppo ha adottato un processo di sviluppo Agile ispirato al framework Scrum. Di seguito, sono descritti i dettagli del processo adottato.

## Ruoli

Ciascun membro del gruppo, oltre al ruolo di sviluppatore, ha svolto uno specifico ruolo nell'ottica della simulazione di un'interazione semplificata ma realistica tra stakeholder e team di progetto. Nello specifico, sono stati stabiliti i seguenti ruoli.

### Committente

Uno dei membri del gruppo ha avuto il ruolo di committente del progetto, svolgendo i seguenti compiti.

- Comunicazione dei requisiti di progetto.
- Verifica del risultato prodotto al termine di ogni iterazione del processo di sviluppo, nell'ottica di garantirne la qualità e la conformità alle proprie aspettative, fornendo anche feedback utili a tale scopo per le iterazioni successive.
- Valutazione e accettazione del risultato finale del progetto.

Tale ruolo è stato assegnato a Cristina Zoccola, poiché è stata la componente del gruppo che ha avanzato l'idea fondante del progetto e che ha più esperienza con il dominio in oggetto.

### Product Owner

Il Product Owner ha avuto il ruolo di coordinatore all'interno del team di sviluppo. Nello specifico, il Product Owner si è occupato dei seguenti compiti.

- Redazione del _product backlog_, ossia l'elenco di attività da svolgere nell'ambito del progetto, completo di livello di priorità, stima della durata e assegnatario per ciascuna attività.
- Comunicazione con il committente, per la raccolta dei requisiti a inizio progetto e, al termine di ogni iterazione, dei feedback sul risultato presentato.
- Coordinamento del lavoro tra i componenti del team di sviluppo.

Tale ruolo è stato assegnato a Elena Boschetti.

### Scrum Master

Lo Scrum Master ha avuto il ruolo di supervisore del processo di sviluppo, al fine di garantirne l'efficienza e la conformità ai principi base del framework Scrum e alle modalità definite all'inizio del progetto.

Tale ruolo è stato assegnato a Emanuele Borghini.

## Organizzazione del lavoro

Le attività di progetto sono state suddivise in iterazioni (_dette sprint_), prevalentemente della durata di una settimana. In totale, sono stati pianificati 7 sprint.

Il primo sprint è stato dedicato alle seguenti attività.

- Definizione del processo di sviluppo, descritto nel presente capitolo.
- Formalizzazione dei requisiti, il cui risultato è riportato nel capitolo ["Requisiti"](./3-requirements.md).
- Scelta degli strumenti da utilizzare a supporto della pianificazione del lavoro e a supporto delle attività di sviluppo; gli strumenti adottati sono riportati nella sezione ["Strumenti"](#strumenti) del presente capitolo.
- Redazione del product backlog, consultabile al seguente [link](https://github.com/users/Borgotto/projects/1).

I 5 sprint successivi sono stati dedicati alle attività di progettazione, sviluppo e redazione della documentazione del codice. L'ultimo sprint è stato infine dedicato alla stesura della presente relazione.

Al termine di ogni sprint, è stato svolto un incontro avente la funzione di Sprint Review, finalizzata a valutare il lavoro svolto durante lo sprint corrente e a definire eventuali cambiamenti da adottare nello sprint successivo, e di Sprint Planning, finalizzato a stabilire ed assegnare le attività da portare a termine nello sprint successivo.

I componenti del gruppo si sono mantenuti quotidianamente aggiornati sullo stato del proprio lavoro, quantomeno via messaggio nella chat dedicata al progetto. Qualora emergessero poi questioni che necessitavano di un confronto più approfondito, i componenti del gruppo hanno svolto un incontro per discuterne a voce.

Gli incontri si sono svolti prevalentemente in via telematica.

## Strumenti

A supporto del processo di sviluppo, sono stati adottati i seguenti strumenti.

- Come sistema di controllo di versione, è stato utilizzato **Git**.
- Per l'hosting remoto del repository, è stata utilizzata la piattaforma **GitHub**.
- Per la redazione del product backlog e l'organizzazione degli sprint, è stato utilizzato **GitHub Projects**, in virtù della sua integrazione in GitHub.
- Come build system, è stato utilizzato **SBT**, essendo lo standard de-facto per progetti Scala.
- Per il testing automatizzato, è stato utilizzato il framework **ScalaTest**, essendo lo standard de-facto per progetti Scala. Inoltre, è stato utilizzato il framework **Mockito** per la creazione di _test doubles_, al fine di poter testare le diverse componenti in maniera indipendente prima di procedere alla loro integrazione.
- Per la CI, sono state utilizzate le **GitHub Actions**, anch'esse integrate in GitHub. La pipeline CI è stata configurata per eseguire i test automatici ad ogni push verso il repository, in modo tale da verificare l'integrità del sistema in maniera costante ed automatica.
