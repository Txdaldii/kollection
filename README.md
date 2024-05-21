# Kollection
##### Una lista di programmi 100% Kotlin
![](/logo.png)

Cosa contiene questa repo?
- [Kordle](#kordle) - Un gioco di parole
- [Kordle Solver](#kordle-solver) - Uno strumento di risoluzione di Kordle
- [Kotext](#kotext) - Una chatroom nel terminale
- [Kotris](#kotris)
- [Klasse](#klasse) - Classeviva, nel terminale
- [Come Eseguire](#come-eseguire)

## Kordle
Kordle è una ricostruzione di Wordle fatta in Kotlin, da giocare nel terminale.
Kordle è un gioco dove bisogna indovinare la parola corretta con le informazioni date.  
Supponiamo che la parola corretta sia **ABACO** (il giocatore non la sa)  
Il giocatore inserisce **BANDO**  
Verrebbe restituita questa stringa:  

![](/kordle-example.png)
#### Verde: La lettera è nella parola corretta, nella stessa posizione
#### Giallo: La lettera è nella parola corretta, ma in una posizione diversa
#### Grigio: La lettera NON è presente nella parola corretta  
Il giocatore ha 6 tentativi a disposizione.
  
## Kordle Solver
Kordle Solver risolve ogni partita di kordle.  
Basta inserire le lettere verdi già trovate nella partita e ogni possibile parola verrà trovata.  
##### ESEMPIO  
Il giocatore inserisce **ABBIA** nella partita.  
Il risultato è **AB**BI*A* (**Grassetto = verde**, *corsivo = giallo*)  
Inseriamo allora **AB---** in Kordle Solver  
Il risultato è:  
abaca, abaco, abate, abati, abato...  
  
## Kotext  
##### Per l'ITT Pascal, selezionare l'IP ``10.22.x.x``
Kotext è semplicemente una chatroom in LAN.  
Avvia il server, seleziona l'IP, inseriscilo in un client, scegli un nome utente, e sei nella chat!  
Ovviamente la chat funziona solo nella rete locale.  
Per ottenere un link ad un file, scrivi **file::/path/to/file.***  
Nota: Se, ad esempio, *mario* è in chat e prova ad entrare un altro utente con il nome *mario*, a quest'ultimo verrà assegnato il nome *mario1*.  
**NOTA: NON SI POSSONO AVVIARE PIÙ ISTANZE DEL SERVER NELLA STESSA RETE ALLO STESSO TEMPO**

## Kotris  
Il più semplice da comprendere, il più difficile da giocare.  
Kotris è tris contro un bot, programmato per essere impossibile.  
Così tanto impossibile che io stesso non sono sicuro che vincere sia possibile.  
Il bot è un algoritmo **minimax**, designato per vincere/pareggiare ogni partita.  
La griglia è strutturata così:  
``- - -``  
``- - -``  
``- - -``  
E si gioca inserendo un numero da 1 a 9, mappati così:  
``1 2 3``  
``4 5 6``  
``7 8 9``  

## Klasse
Claaseviva, nel terminale  
Il progetto più complicato, con uso di json, liste e mappe  
Per usarlo basta inserire nome utente e password  
Funzioni:  
- Vedere i voti, filtrati per quadrimestre o materia  
- Vedere i compiti  
- Calcolare la media dei voti  
- Vedere le assenze  
- Vedere le lezioni passate  

# Come Eseguire  
Per eseguire uno dei file jar precompilati, apri un terminale/prompt dei comandi nel percorso dove si trova il file, poi scrivi  
`**java -jar *nomefile.jar***`
