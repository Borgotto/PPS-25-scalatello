# Processo di sviluppo

Per la realizzazione del progetto, il gruppo ha adottato un processo di sviluppo Agile ispirato al framework Scrum. Di seguito, sono descritti i dettagli del processo adottato.

## Ruoli

Ciascun membro del gruppo, oltre al ruolo di sviluppatore, ha svolto uno specifico ruolo nell'ottica della simulazione di un'interazione semplificata ma realistica tra stakeholder e team di progetto. Nello specifico, sono stati stabiliti i seguenti ruoli.

### Committente

Uno dei membri del gruppo ha avuto il ruolo di committente del progetto, svolgendo i seguenti compiti:

- comunicazione dei requisiti di progetto;
- verifica del risultato prodotto al termine di ogni iterazione del processo di sviluppo, nell'ottica di garantirne la qualità e la conformità alle proprie aspettative, fornendo anche feedback utili a tale scopo per le iterazioni successive;
- valutazione e accettazione del risultato finale del progetto.

Tale ruolo è stato assegnato a Cristina Zoccola, poiché è stata la componente del gruppo che ha avanzato l'idea fondante del progetto e che ha più esperienza con il dominio in oggetto.

### Product Owner

Il Product Owner ha avuto il ruolo di coordinatore all'interno del team di sviluppo. Nello specifico, il Product Owner si è occupato dei seguenti compiti:

- redazione del *backlog*, ossia l'elenco di attività da svolgere nell'ambito del progetto, completo di livello di priorità, stima della durata e assegnatario per ciascuna attività;
- comunicazione con il committente, per la raccolta dei requisiti a inizio progetto e, al termine di ogni iterazione, dei feedback sul risultato presentato;
- coordinamento del lavoro tra i componenti del team di sviluppo.

Tale ruolo è stato assegnato a Elena Boschetti.

### Scrum Master

Lo Scrum Master ha avuto il ruolo di supervisore del processo di sviluppo, al fine di garantirne l'efficienza e la conformità ai principi base del framework Scrum e alle modalità definite all'inizio del progetto.

Tale ruolo è stato assegnato a Emanuele Borghini.

## Organizzazione del lavoro

Le attività di progetto sono state suddivise in iterazioni (*sprint*), prevalentemente della durata di una settimana. In totale, sono stati pianificati 7 sprint.

Il primo sprint è stato dedicato alle seguenti attività:

- definizione del processo di sviluppo, descritto nel presente capitolo;
- formalizzazione dei requisiti, il cui risultato è riportato nel capitolo ["Requisiti"](./3-requirements.md);
- scelta degli strumenti da utilizzare a supporto della pianificazione del lavoro e a supporto delle attività di sviluppo; gli strumenti adottati sono riportati nella sezione ["Strumenti"](#strumenti) del presente capitolo;
- redazione del backlog, consultabile alla [seguente pagina](https://github.com/Borgotto/PPS-25-scalatello/projects/1).

I 5 sprint successivi sono stati dedicati alle attività di progettazione, sviluppo e redazione della documentazione del codice. L'ultimo sprint è stato infine dedicato alla stesura della presente relazione.

Al termine di ogni sprint, è stato svolto un incontro avente la funzione di Sprint Review, finalizzata a valutare il lavoro svolto durante lo sprint corrente e a definire eventuali cambiamenti da adottare nello sprint successivo, e di Sprint Planning, finalizzato a stabilire ed assegnare le attività da portare a termine nello sprint successivo.

I componenti del gruppo si sono mantenuti quotidianamente aggiornati sullo stato del proprio lavoro, quantomeno via messaggio nella chat dedicata al progetto. Qualora emergessero poi questioni che necessitavano di un confronto più approfondito, i componenti del gruppo hanno svolto un incontro per discuterne a voce.

Gli incontri si sono svolti prevalentemente in via telematica.

## Strumenti

A supporto del processo di sviluppo, sono stati adottati i seguenti strumenti:

- **Git**: come sistema di controllo di versione;
- **GitHub**: per l'hosting remoto del repository;
- **GitHub Projects**: per la redazione del backlog e l'organizzazione degli sprint, in virtù della sua integrazione in GitHub;
- **SBT**: come build system, essendo lo standard per progetti Scala;
- il framework **ScalaTest**: per il testing automatizzato, essendo lo standard per progetti Scala;
- il framework **Mockito** per la creazione di *test doubles*, al fine di poter testare le diverse componenti in maniera indipendente prima di procedere alla loro integrazione;
- le **GitHub Actions**: per la CI (Continuous Integration) e la generazione della Scaladoc, anch'esse integrate in GitHub. La pipeline CI è stata configurata per eseguire i test automatici ad ogni push verso il repository (esclusi quelli verso i branch dedicati alla relazione), in modo tale da verificare l'integrità del sistema in maniera costante ed automatica.

[Indice](../index.md) | [Capitolo successivo](./2-requirements.md)
