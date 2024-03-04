# Laborator 12

Limbajul Java ofera in cadrul pachetului java.util.concurrent o serie de clase de tip colectie ce pot fi utilizate intr-un context multithreaded. Acestea urmeaza in parte in implementarea interna unele din tehnicile si algoritmii discutati in cadrul cursului si pot constitui alternative la acestea. Mentionam pe scurt in cele ce urmeaza o parte din acestea.

## Clasa ConcurrentSkipListSet din Java

Clasa ConcurrentSkipListSet<E> este una din clasele disponibile in pachetul java.util.concurrent package ce ofera acces concurent sigur. Clasa ofera o optimizare comparativ cu o lista obisnuita in ceea ce priveste accesul la elemente, acestea fiind ordonate fie pe baza ordinii naturale (in functie de tipul template), fie prin intermediul unei implementari de tip Comparator. Clasa ofera principalele metode definite in mod uzual pentru o lista:
* boolean add(E e);
* boolean remove(Object o);
* boolean contains(Object o);

Datorita ordinii mentinute intre elementele listei, pe langa metodele de mai sus mai sunt oferite si altele pentru acces la elemente in functie de ordinea acestora ( *first()* ; *last()* ; *ceiling (E e)* ; *floor (E e)* ; etc). Mai multe informatii despre aceasta clasa pot fi regasite in [documentatia Java](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListSet.html).

## Clasa ConcurrentLinkedQueue din Java

Clasa ConcurrentLinkedQueue<E> ofera o implementare de tip coada cu acces concurent sigur disponibila in acelasi pachet java.util.concurrent. Clasa ofera metodele specifice unei cozi:
* boolean add(E e);

  (adauga un element la capatul *tail* al cozii)
* E poll();

  (extrage un element din capatul head al cozii)
* E peek();

  (returneaza elementul din capaturl head al cozii fara a-l extrage)

Clasa ofera de asemenea acces in interiorul cozii, nu doar la capetele *head* si *tail*, prin doua metode: *contains(Object o)* si *(remove(Object o)*. Mai multe informatii despre clasa pot fi regasite in [documentatia Java](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html).

## Alte tipuri de cozi blocante disponibile in pachetul java.util.concurrent

Acelasi pachet java.util.concurrent package contine de asemenea si alte clase ce ofera o functionalitate FIFO pentru o coada intr-o maniera blocanta. In continuare enumeram cateva caracteristici de baza ale unor astfel de tipuri:

### ArrayBlockingQueue

Este o clasa ce ofera functionalitatea FIFO a unei cozi ce functioneaza intr-un context concurent cu o capacitate fixa a numarului de elemente ce pot fi retinute prin intermediul unui vector. Pe langa metodele care returneaza o simpla valoare booleana in cazul esecului adaugarii sau extragerii unui element din coada, clasa (ca si restul celor blocante) ofera si o serie de metode care asteapta (blocheaza) threadul curent pana ce operatia este posibila cu succes. Principalele astfel de metode sunt *put(E e)* ce asteapta pana ce apare spatiu pentru un element in coada, si respectiv *take()* ce asteapta pana ce coada contine cel putin un element pentru a fi extras. Mai multe informatii despre aceasta clasa pot fi regasite [aici](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ArrayBlockingQueue.html).

### DelayQueue

Aceasta clasa reprezinta o coada nelimitata cu o caracteristica anume a elementelor continute. Acestea trebuie sa implementeze interfata *Delayed*. Prin aceasta interfata o intarziere de timp este asociata fiecarui element, clasa fiind capabila sa ofere restrictionarea extragerii elementelor respective pana ce aceasta intarziere (delay) expira. Metoda *take()* blocheaza asigurand aceasta verificare. Mai multe informatii despre aceasta clasa pot fi regasite [aici](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/DelayQueue.html).

### LinkedBlockingQueue

Are o functionalitate similara cu ArrayBlockingQueue cu principalela diferenta ca elementele sunt stocate intr-o lista inlantuita in locul unui vector, lista ce poate fi limitata optional. In mod implicit capacitatea este egala cu Integer.MAX_VALUE, dar poate fi setata mai jos prin constructorul clasei. Mai multe informatii despre aceasta clasa pot fi regasite [aici](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingQueue.html).

### PriorityBlockingQueue

Este o clasa ce ofera o ordine de prioritate elementelor (care ar trebui sa fie comparabile) bazata in mod implicit pe ordinea naturala sau pe baza unui obiect Comparator specificat in constructorul clasei. In acest mod, capul cozii (head) este considerat cel mai mic element pe baza criteriului de comparatie. Coada este implicit nelimitata si blocheaza doar cand este vida la apelul metodei *take()*. Mai multe informatii despre aceasta clasa pot fi regasite [aici](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/PriorityBlockingQueue.html).

### SynchronousQueue

Nu este o clasa efectiv de tip coada in sensul unei colectii, reprezentand mai precis un canal de tip rendezvous fara capacitate. O incercare de a insera un element din partea unui thread va avea succes doar daca exista un alt thread ce incearca sa extraga un element. Atat timp nu exista un apel simultan pentu o metoda put(E e) cu un apel take(), apelul take() va bloca (si viceversa). Mai multe informatii despre aceasta clasa pot fi regasite [aici](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/SynchronousQueue.html).

## Exercitiu:

1. Realizati o evaluare comparativa a unui algoritm pentru coada prezentat in curs sau in laboratoarele trecute si una dintre clasele de tip coada din Java. Cele doua cozi trebuie sa se comporte similar din punct de vedere al caracteristicii blocante la adaugari in cazul unei cozi pline sau eliminari in cazul unei cozi vide. Executati experimente in care sa masurati timpul de executie cu 4 si 8 thread-uri, jumatate din acestea executand operatii de adaugare, respectiv jumatate operatii de eliminare elemente din coada. Fiecare thread va incerca sa adauge, respectiv sa elimine 1000000 de elemente generate random in intervalul 1-100. Retineti rezultatele intr-o statistica ce va include ca informatii numarul de threaduri, tipul de coada si timpul de executie pentru fiecare experiment.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
