# Kollection
##### Una lista di programmi 100% Kotlin
![](/logo.png)

Cosa contiene questa repo?
- [Kordle](#kordle) - Un gioco di parole
- [Kordle Solver](#kordle-solver) - Uno strumento di risoluzione di Kordle
- Kotext (Client) - Una chatroom nel terminale
- Kotext (Server) - Il backend della chatroom
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

# Come Eseguire  
Per eseguire uno dei file jar precompilati, apri un terminale/prompt dei comandi nel percorso dove si trova il file, poi scrivi  
**java -jar *nomefile.jar***
