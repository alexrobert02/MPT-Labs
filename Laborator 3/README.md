# Laborator 3
## Recapitulare semafoare in Java

La modul general, informal, un semafor este o primitiva de sincronizare bazata pe un contor intern ce indica un numar de permisiuni si doua operatii de baza pentru a oferi functionalitatea de sincronizare. Considerand contorul c, aceste doua operatii pot fi definite in forma lor cea mai simpla, in mod atomic, in urmatorul mod:

```
signal () {
   c++ ; 		
}
wait () {
   while (true) {
       if (c > 0) {
       c--;
       break;
       }
   }
}
```

Contorul are restrictia de a nu deveni negativ. Aceasta are ca rezultat un efect blocant de sincronizare in momentul in care atinge valoarea 0. Un thread ce va apela *wait()* va fi blocat si nu va fi capabil sa avanseze pana ce un alt thread va apela *signal()* incrementand semaforul. In mod uzual, un contor de semafor are de asemenea o limita superioara pentru a defini numarul de thread-uri ce pot executa wait() in mod concurent pe semaforul respectiv fara a se bloca - numarul total de permisiuni de acces. Prin setarea contorului de limita superioara la 1, va rezulta un semafor binar, ce poate fi utilizat in mod similar cu un lock pentru a sincroniza accesul la o sectiune critica.

In Java mecanismul de semaforizare este disponibil prin intermediul clasei java.util.concurrent.Semaphore. Constructorii clasei permit setarea contorului superior. Cele doua operatii de baza ale unui semafor au drept corespondent metodele *acquire()* pentru wait si respectiv *release()* pentru signal.

```
// exemplul pentru lock revizitat

import java.util.concurrent.Semaphore;
    
class SharedWork {
    
   Semaphore sem;			
   SomeObject sharedObject;
   
   SharedWork() {
      sem = new Semaphore(1);                 //numarul maxim de permisiuni de acces; 1 va crea un semafor
                                              //binar cu aproximativ aceeasi functionalitate ca 
                                              //un mutex implementat folosind interfata Lock
   sharedObject = new SomeObject();
   }
    
   public void accessObject() {
      MyThread thread1 = new MyThread();
      MyThread thread2 = new MyThread();
      thread1.start();
      thread2.start();
   }
    
   class MyThread extends Thread {

      public void run() {
         while (true) {
            sem.acquire();                  //thread-ul "obtine" semaforul prin decrementarea
                                            //numarului de permisiuni si interzice celoralte thread-uri
                                            //sa intre in sectiunea critica
            sharedObject.someMethod();      //executa o metoda care necesita acces exclusiv
            sem.release();                  //thread-ul "elibereaza" semaforul
         }
      }
   }	
}
```

Una din principalele diferente intre un lock si un semafor este ca in cazul unui lock, odata ce acesta a fost blocat poate fi deblocat doar de thread-ul care l-a blocat. In cazul unui semafor, orice thread poate apela *release*.

## Exercitii

1. Sincronizati programul implementat in exercitiul din laboratorul anterior pentru incrementarea si decrementarea unui contor folosind un semafor binar in locul metodei utilizate anterior. Ar avea sens in contextul respectiv utilizarea unui semafor cu un numar mai mare de permisiuni in locul unui semafor binar? Argumentati.


2. Algoritmul de tip lock al lui Peterson descris in curs, utilizabil pentru acces exclusiv de doua threaduri este atat deadlock-free cat si starvation-free.

    a) Putem spune ca exista o relatie de implicatie intre deadlock-freedom si starvation-freedom? Care este aceasta si cum argumentati?\
    b) Consideram modificarea metodei unlock conform pseudocodului urmator (i si j corespund identificatorilor celor doua threaduri sincronizate - i threadul din contextul de executie curent, j threadul celalalt):

   ```
   public void lock() {
      flag[i] = true;
      victim = i;
      while (flag[j] == true && victim == i) {};
   }
   
   public void unlock() {
      flag[i] = false;
      while (flag[j] == true) {};   
   }
   ```

   Mai este algoritmul starvation-free in aceasta forma? Argumentati raspunsul printr-o demonstratie sau printr-o descriere a unui trace de executie.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
