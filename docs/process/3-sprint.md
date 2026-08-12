# Sprint 3 (06/07/2026 - 12/07/2026)

## Obiettivi

Il primo obiettivo dello sprint è stato l'integrazione delle parti del Model realizzate nello sprint precedente.

L'obiettivo successivo è stato lo sviluppo delle seguenti parti:

- Controller (assegnato a Cristina Zoccola);
- Save Manager (assegnato a Emanuele Borghini);
- View, limitatamente al menu iniziale e al rendering dello stato della partita (assegnata a Elena Boschetti).

Similmente allo sprint precedente, si è desiderato pervenire all'implementazione disgiunta di queste parti; la loro integrazione è prevista per il prossimo sprint.

Il corretto comportamento di Controller e Save Manager sarà garantito da suite di test automatizzati, che faranno temporaneamente uso di Mockito per creare mock in sostituzione delle componenti non ancora integrate.

Per quanto riguarda la View, il risultato desiderato sarà invece verificato mediante test manuali e, in mancanza dell'integrazione con il Controller, i mock degli stati della partita saranno definiti manualmente.

## Svolgimento

L'integrazione delle parti del Model è stata completata il giorno 07/06/2026.

Successivamente, ciascun componente del gruppo si è dedicato allo sviluppo della propria parte sopra indicata.

## Esito

Gli obiettivi dello sprint sono stati raggiunti.

Nel prossimo sprint, si può quindi procedere all'integrazione di Model, Controller, View e Save Manager, che sarà svolta da Elena Boschetti.

Nello stesso sprint, sarà implementata la parte di View relativa alla gestione dei salvataggi.

Si è deciso inoltre di effettuare, dopo aver completato le due attività appena indicate, un refactoring intermedio per migliorare il codice finora prodotto.
