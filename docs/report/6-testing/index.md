# Testing

Il **testing** del codice è stato una parte fondamentale dello sviluppo, in quanto è stato seguito il metodo **TDD**.

Esso è stato testato utilizzando **ScalaTest** con il supporto di **Mockito**.

## Mockito

**Mockito** è stato utilizzato per:

- **creare un *mock* delle parti non ancora implementate:**

  Durante la scrittura del codice, ogni componente ha creato un *mock* per sostituire delle classi con cui aveva bisogno di interagire ma che non erano ancora state implementate. \
  Una volta implementate le classi in questione i *mock* sono stati rimpiazzati con le classi effettive;

- ***partial mocking*:**

  In particolare, la funzionalità *Spy*: per poter testare alcuni metodi senza dover intaccare l'incapsulamento.

---

Ogni componente del gruppo riporta di seguito la parte di testing del proprio codice.

- [Emanuele Borghini](emanuele-borghini.md)
- [Elena Boschetti](elena-boschetti.md)
- [Cristina Zoccola](cristina-zoccola.md)
