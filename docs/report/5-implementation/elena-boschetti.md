# Implementazione - Elena Boschetti

## Lavoro svolto

Nella fase implementativa del progetto, ho realizzato le seguenti parti:

- la [logica di gioco](https://github.com/Borgotto/PPS-25-scalatello/blob/main/src/main/scala/it/unibo/pps/model/Logic.scala);
- le strutture dati dedicate alla memorizzazione dello stato di una partita ([package `state`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/state)) e alla rappresentazione di concetti di dominio usati da tutta l'applicazione ([package `domain`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/domain), ad esclusione della classe Position);
- tutta la View ([package `view`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/view)), inclusa l'attuazione del pattern Observer per la notifica alla View degli aggiornamenti di stato di una partita, realizzando le interfacce `Publisher` e `Subscriber` ([package `observer`](https://github.com/Borgotto/PPS-25-scalatello/tree/main/src/main/scala/it/unibo/pps/observer)) e la loro implementazione da parte di View e [Controller](https://github.com/Borgotto/PPS-25-scalatello/blob/8dd948e07a178a86598a808c7a5241c43845be26/src/main/scala/it/unibo/pps/controller/Controller.scala#L67-L74).

## Aspetti implementativi rilevanti
