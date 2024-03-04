# Laborator 8
## Problema lost-wakeups

Reluam mai jos, o portiune din exemplul de coada propus in exercitiul 3 din tema, ce modifica exemplul anterior, SimpleQueue, din laboratorul 5. In exemplul initial conditiile primesc semnal la fiecare adaugare sau eliminare din coada. Aparent, conform modificarii din exercitiul 3, ar fi fost mai eficient ca apelul *signal* sa fie executat doar la momentele cand cele doua conditii devin efectiv indeplinite. Mai precis, in cazul adaugarii, doar in momentul in care coada devine nevida

```
public void enq(T x) {
   lock.lock();
   try {
      while(count == items.length) 
      {
         try { full.await(); }
         catch (InterruptedException e) { } 
         finally { } 
      } 
      System.out.println("Enqueuing " + x);
      items[tail] = x;
      if (++tail == items.length) { tail = 0; } 
      count++;
      if (count == 1) { 
         empty.signal(); 
      }
   } finally {
      lock.unlock();
   }
}
```

Desi aparent o optimizare, aceasta abordare creeaza o potentiala problema numita generic *lost-wakeup*. Sa presupunem ca doua sau mai multe thread-uri consumator se afla in stare de asteptare a conditiei *empty* pentru a elimina un element din coada aflata in stare vida. De asemenea sa presupunem ca avem doua sau mai multe thread-uri producator care adauga elemente, si acestea reusesc sa faca acest lucru inainte ca un thread consumator trezit sa elimine un element din coada. In aceasta situatie, doar primul thread producator va semnala conditia *empty* trezind un singur consumator, ceilalti producatori nefacand acest lucru pentru ca avem deja un element in coada. Consecinta este ca restul consumatorilor vor ramane suspendati, pierzand trezirea (lost wakeup), cel putin pana ce noi thread-uri consumator vor consuma restul elementelor din coada.\
O alta varianta de a solutiona aceasta problema este utilizarea *signalAll()* in loc de *signal()* apelat la fiecare executie ca in varianta initiala. Notificarea va trezi in acest caz toate thread-urile aflate in stare de asteptare. Acestea insa vor se vor afla in competitie pentru acelasi lock ceea ce va creste nivelul de contention la momentul respectiv.

## Exercitii

1. Se da urmatoarea implementare de referinta pentru algoritmul de lista coarse-grained in care protejarea operatiilor asupra listei se realizeaza printr-un lock singular membru al listei utilizat de fiecare metoda: CoarseList.java. Identificati, cat mai precis, cate o linie de cod din metoda *add* care corespunde punctelor de linearizare pentru situatiile de adaugare element cu succes in lista, respectiv de esec la adaugare element in lista. Argumentati raspunsul.


2. Completati implementarea listei fine-grained prezentata la curs (implementati metoda add) si realizati o evaluare comparativa a implementarii cu lista coarse-grained din exercitiul anterior. Executati experimente cu 4 si 8 thread-uri, jumatate din acestea executand operatii de adaugare, respectiv jumatate operatii de eliminare elemente din lista. Fiecare thread va incerca sa adauge, respectiv sa elimine 10000 de elemente generate random in intervalul 1-1000. Retineti rezultatele intr-o statistica ce va include ca informatii numarul de threaduri, tipul de lista si timpul de executie pentru fiecare experiment.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
