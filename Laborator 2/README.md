# Laborator 2
## Recapitulare notiuni de baza pentru sincronizare in Java
### Specificatorul "synchronized"

In java fiecare instanta de obiect are asociat un lock intern (monitor). Acest lock poate fi utilizat de thread-uri pentru a se sincroniza la accesul obiectului prin utilizarea specificatorului *synchronized*. Exista doua moduri principale de utilizare a acestuia.\
Prima posibilitate este prin declararea metodelor din clasa obiectului ca synchronized. Cand un thread va executa o metoda synchronized va obtine un lock pentru obiectul respectiv. Orice alt thread ce va inceca sa execute orice metoda synchronized pe aceeasi instanta de obiect va astepta pana ce thread-ul curent va elibera lock-ul in urma finalizarii apelului metodei executate.
```
public class SharedObject {

   int x = 0;
   
   public synchronized int get() {
      return x; 
   }
   
   public synchronized void inc() {
      x++;
   }
}

public class MyThread extends Thread {

   SharedObject shared;
   
   public MyThread(SharedObject shared) {
      this.shared = shared;
   }
   
   public void run (){
      int i = 0;			
      while (i < 100) {
         shared.inc();          // <------------------------------------------------------------|
         i++;                                   //
         Thread.sleep(10);                      //                                              |
      }                                         //                                              |
   }                                            //                                              |
}                                               //                                              |
                                                //                                              |
...                                             //                                              |
                                                //                                              |
SharedObject myShared = new SharedObject();     //                                              |
MyThread thread1 = new MyThread(myShared);      //                                              |
MyThread thread2 = new MyThread(myShared);      //                                              |		
                                                //                                              |
thread1.start();                                // <---Cand aceste thread-uri vor executa acest apel
thread2.start();                                // <-/ unul din ele va obtine lock-ul intern pentru myShared
                                                //     daca acesta este liber, sau va astepta pana la eliberarea acestuia
```

A doua modalitate de utilizare a *synchronized* este prin specificarea explicita a obiectului pentru care lock-ul este cerut si sectiunea de cod pentru care este mentinut. Aceasta modalitate poate fi utila fie pentru sincronizarea accesului doar pentru o parte a codului dintr-o metoda, sau pentru folosirea de lock-uri diferite pentru diferite portiuni de cod. Utilizarea de lock-uri diferite pentru diferite portiuni de cod din aceeasi clasa prin intermediul *synchronized* e posibila prin folosirea de lock-uri ce corespund altor obiecte decat cel curent. Aceasta poate permite unor grupuri diferite de thread-uri sa execute simultan sectiuni de cod diferite, dar sa se sincronizeze pentru o anumita sectiune care necesita acces exclusiv.
```
public class SharedObject {

   int x = 0;
   int y = 0;
   SomeObject lockObject = new SomeObject();          //<-- nu conteaza implementarea clasei in acest exemplu
                                                      //    ne intereseaza doar lock-ul intern

   public synchronized void inc() {                   //<-- sincronizare prin intermediul lock-ului asociat 
      x++;                                            //    obiectului curent
   }

   public void dec() {                                //<-- nesincronizat ca metoda integrala, thread-urile pot incepe
                                                      //    executia dec in timp ce un alt thread executa inc
      int i = 0;

      synchronized(lockObject) {                      //<-- un thread ce va ajunge aici obtine lock-ul pentru lockObject
                                                      //    daca este disponibil; daca lock-ul nu este disponibil
         y--;                                         //    thread-ul va astepta pana la momentul respectiv;
                                                      //    pentru ca lock-ul este pentru un alt obiect, aceasta executie 
      }                                               //    nu blocheaza alte thread-uri sa execute inc() unde metoda e
                                                      //    accesata in functie de obtinerea lock-ului instantei curente
 
      while (i < 100000) {                            //<-- putem obtine un avantaj prin acest mod de sincronizare  
                                                      //    pentru a mentine aceasta portiune de cod nesincronizat; 
         System.out.println("spending long time");    //    daca ar fi sincronizat, un thread ar bloca pe restul ca sa  
         Thread.sleep(10);                            //    modifice variabila partajata y pentru un timp lung in 
         i++;                                         //    mod inutil in contextul in care y nu e accesat aici
      }	
   }
}

public class MyThreadInc extends Thread {

   SharedObject shared;

   public void MyThread(SharedObject shared) {
      this.shared = shared
   }

   public void run (){
      int i = 0; 				
      while (i < 100) {
         shared.inc();
         i++;				
         Thread.sleep(10);
      }			
   }
}

public class MyThreadDec extends Thread {

   SharedObject shared;

   public void MyThread(SharedObject shared) {
      this.shared = shared
   }

   public void run (){
      int i = 0; 				
      while (i < 100) {
         shared.dec();
         i++;
         Thread.sleep(10);
      }			
   }
}
 
...
 
SharedObject myShared = new SharedObject();
MyThreadInc thread1 = new MyThreadInc(myShared);
MyThreadInc thread2 = new MyThreadInc(myShared);
MyThreadDec thread3 = new MyThreadDec(myShared);
MyThreadDec thread4 = new MyThreadDec(myshared);
 
thread1.start();
thread2.start();
thread3.start();
thread4.start();
```

### Specificatorul "volatile"

Specificatorul *volatile* poate fi folosit la declararea unei variabile pentru a ignora potentiale optimizari ale compilatorului ce pot cauza inconsistente asupra starii observabile a memoriei. Valoarea unei variabile ce nu este declarata ca volatile poate fi mentinuta in memoria accesibila local thread-ului (cached) pentru un acces mai rapid, iar thread-ul poate sa piarda observarea unor schimbari efectuate asupra acestei variabile de catre un thread diferit. Prin declararea variabilei ca volatile orice operatii de citire sau scriere asupra acesteia sunt executate in memoria principala partajata, implicand evident un cost crescut de acces, in favoarea unei vederi consistente asupra starii variabilei.
```
// Stop thread example .. revisited
 
public class MyThread extends Thread {
 
   boolean alive = true;					
   public void run (){
      int count = 0;
      while (alive) {                                                     //<--------------|
         count++;                                                         //               |
      }                                                                   //               |
                                                                          //               |
      System.out.println("final count is " + count);                      //               |
   }                                                                      //               |
                                                                          //               |
      public void killThread() {                                          //               |
                                                                          //               |
         alive = false;                                                   //               |
      }                                                                   //               |
                                                                          //               |
}                                                                         //               |
                                                                          //               |
...                                                                       //               |
                                                                          //               |
MyThread lazyThread = new MyThread();                                     //               |
lazyThread.start();                                                       //               |
lazyThread.killThread();   // <-- la urmatoarea iteratie while in cadrul run               |
                           //     dupa ce alive este setat pe false lazyThread se va opri  |
                           //     ... sau nu                                               |
                           //     variabila este setata de thread-ul curent                |
                           //     si verificata de lazyThread ------------------------------
                           //     care ar putea sa nu observe schimbarea in proria
                           //     memorie (cache) ce retine valoarea
 
//Solutie: adaugarea volatile la declararea variabilei alive
//va asigura citirea si scrierea obligatorie in memoria partajata
//si vizibilitatea valorii reale a variabilei pentru toate thread-urile
 
boolean volatile alive = true;
```

### Locking explicit in Java

Pe langa specificatorul *synchronized*, Java ofera un mod explicit de locking prin intermediul interfetei Lock (disponibila in pachetul java.util.concurrent.locks) ce este implementata de o serie de clase care pot fi utilizate pentru instantierea de locks. Una dintre aceste clase este ReentrantLock.

Clasa ReentrantLock poate fi folosita pentru instantierea de locks ce pot fi folosite intr-un mod similar cu synchronized descris mai sus pentru accesarea unei portiuni de cod. Principalele metode utilizate in acest scop sunt *lock* si *unlock*.
```
import java.util.concurrent.locks.ReentrantLock;

class SharedWork {

   ReentrantLock lock;
   SomeObject sharedObject;

   SharedWork() {
      lock = new ReentrantLock();
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
            lock.lock();                    //thread-ul obtine lock-ul
            shareObject.someMethod();       //executa o metoda ce necesita sincronizare
            lock.unlock();                  //thread-ul elibereaza lock-ul
         }
      }
   }	
}
```
Ce se intampla daca in situatia de mai sus este aruncata o exceptie?. Spre deosebire de *synchronized* care va elibera lock-ul intern cand executia programului paraseste sectiunea sincronizata, lock-urile explicite raman blocate pana la apelul *unlock*. Din acest motiv este recomandata asigurarea ca apelul *unlock* va fi executat in orice situatie:
```
...
   while (true) {
      lock.lock();                        //thread-ul obtine lock-ul
      try {
         shareObject.someMethod();        //executa o metoda ce necesita sincronizare		
      }
      finally {
          lock.unlock();                  //thread-ul elibereaza lock-ul
      }
   }
...
```
Una din proprietatile ReentrantLock este ca metoda *lock* poate fi apelata cu succes de mai multe ori de thread-ul ce detine lock-ul respectiv. Efectul consta intr-o incrementare interna mentinuta pe instanta lock-ului. In cazul apelului *unlock* contorul intern este decrementat, iar lock-ul nu este eliberat pana ce acesta nu ajunge la valoarea 0. In situatia in care se doreste ca lock-ul sa nu fie folosit in acest mod re-entrant, practic prin limitarea la incrementarea pana la 1 a acestui contor intern, se poate utiliza metoda *isHeldByCurrentThread*:
```
// sample din documentatia Java
class X {
   ReentrantLock lock = new ReentrantLock();
   // ...

   public void m() {
      assert !lock.isHeldByCurrentThread();     //checking here if the thread does not already hold the lock
      lock.lock();
      try {
         // ... method body
      } finally {
         lock.unlock();
      }
  }
}
```
Valoare curenta a contorului intern se poate obtine prin apelul metodei *getHoldCount*.

## Exercitii

1. Scrieti un program care porneste n si respectiv m thread-uri de doua tipuri diferite conform descrierii ce urmeaza. Aceste thread-uri acceseaza un contor partajat (initializat cu 0) intr-o bucla (100000 de iteratii). In fiecare iteratie thread-urile din primul tip citesc contorul intr-o variabila locala clasei thread-ului, o incrementeaza, si stocheaza valoarea rezultata inapoi in contorul partajat. Thread-urile din al doilea tip functioneaza similar dar in loc de incrementarea contorului il decrementeaza. Cand toate thread-urile finalizeaza operatiile, programul va afisa valoarea contorului si durata executiei (incercati o rulare pentru n = m).


2. Pastrati prima varianta a programului. Modificati-l pentru a proteja accesul la contor folosind modificatorul synchronized. Contorul trebuie sa fie 0 la finalul executiei pentru cazul n = m.\
   Rulati toate ambele variante de implementare masurand timpul de rulare in milisecunde, folosind seturi egale m = n = 1, 2 si 4 thread-uri si centralizati rezultatele intr-un tabel comparativ.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
