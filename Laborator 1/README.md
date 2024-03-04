# Laborator 1
## Recapitulare utilizare fire de executie (thread-uri) in Java

O descriere informala a unui thread (fir de executie) ar fi o serie secventiala de instructiuni executate in cadrul unui proces. O aplicatie are cel putin un thread - threadul principal (main thread). In momentul in care o aplicatie Java este lansata in executie, aceasta implica si lansarea altor thread-uri pentru diverse operatii, precum managementul memoriei, notificari de evenimente, etc, dar acestea sunt de regula transparente din punctul de vedere al programatorului. Cand programam o aplicatie in Java, thread-ul principal este cel ce corespunde functiei *main* incheiat odata cu aceasta si implicit cu executia programului.

### Crearea si pornirea unui thread

Exista doua posibilitati principale pentru crearea unui thread in Java. Prima este prin extinderea clasei java.lang.Thread:
```
public class MyThread extends Thread {
   public void run (){
      System.out.println("This is my thread!");
   }
}
```
Codul ce va fi executat de noul thread (in paralel cu thread-ul ce il porneste pe acesta) este continut in metoda *run()*. Crearea thread-ului este realizata prin instantierea unui nou obiect din clasa ce extinde Thread. Aceasta nu porneste insa si executia thread-ului. Pentru a lansa executia este necesar apelul metodei *start()*. Se observa ca metoda *run()* nu se apeleaza in mod direct:
```
MyThread newThread = new MyThread();
newThread.start();
```
A doua posibilitate de a crea un thread este prin implementarea interfetei Runnable (care de altfel este implementata si de clasa Thread):
```
public class MyThread implements Runnable {
   public void run (){
      System.out.println("This is my thread!");
   }	
}
```
Singura metoda ce necesita implementare obligatorie este tot *run()* ca in cazul precedent. Pentru a porni executia dupa instantierea unui nou obiect, din nou metoda apelata va fi *start()* din clasa Thread. Pentru aceasta o instanta Thread trebuie creata pe baza instantei Runnable:
```
MyThread newThread = new MyThread();
Thread actualThread = new Thread(newThread);
actualThread.start();
```

### Interactiunea cu un thread
O instanta Thread sau a unei clase derivate din aceasta poate fi tratata ca orice alt obiect in ce priveste apelul metodelor din clasa respectiva. De exemplu se pot defini metode de tip getter/setter pentru diversi membri adaugati in clasa, se pot seta proprietati specifice deja existente in clasa Thread (ex., numele thread-ului), etc. De subliniat insa ca apelul unei metode dintr-o instanta Thread se va executa in thread-ul apelant, ca pentru orice alt obiect, si nu in cadrul executiei thread-ului pentru care e apelata metoda.
```
SomeThread someThread = new SomeThread();

someTread.setSomeStuff();  // <-- aceasta se executa in thread-ul curent;
                           //     obiectul someThread este modificat
                           //     dar modificarea in starea obiectului  
                           //     nu e executata de obiectul thread modificat in sine
```
Oprirea temporara a unui thread poate fi realizata prin apelul *sleep()* avand ca parametru durata dorita pentru oprire. Aceasta metoda trebuie apelata de thread-ul ce se doreste a fi oprit si nu poate fi apelata pentru acesta de catre un alt thread. O alta metoda pentru intreruperea temporara a executiei unui thread este *yield()*. Ca si *sleep()* si aceasta trebuie apelata de thread-ul ce se va opri, dar efectul consta doar intr-o cedare de prioritate, lasand in fata alte thread-uri ce asteapta sa fie preluate de catre procesor pentru executie.

Un thread poate astepta in mod explicit ca un alt thread sa finalizeze metoda sa *run()* prin apelul *join()* pe instanta thread-ului asteptat.
```
SomeThread someImportantThread = new SomeThread();
someImportantThread.start();
someImportantThread.join(); // <-- asteptam in thread-ul curent pentru finalizarea executiei someImportantThread
```

### Oprirea completa a unui thread
Cea mai simpla metoda de a opri complet un thread este prin setarea unui flag verificat periodic in cadrul metodei *run()*, pe baza acestuia thread-ul oprindu-si propria executie. (Aceasta abordare poate fi utilizata si pentru a altera comportamentul thread-ului intr-un alt mod dintr-un context extern, de exemplu pentru a-l notifica sa se intrerupa temporar printr-un apel sleep).

```
public class MyThread extends Thread {

   boolean alive = true;   // <-- aici poate sa apara o problema
                           //     care e, si cum s-ar corecta?

   public void run (){
      while (alive) {
         System.out.println("spending time doing nothing...");
      }
   }

   public void killThread() {	
      alive = false;
   }
}

...

MyThread lazyThread = new MyThread();
lazyThread.start();
lazyThread.killThread();   // <-- la urmatoarea iteratie in cadrul run
                           //     dupa ce alive este setat la false lazyThread se va opri
```
O alta modalitate prin care s-ar putea opri un thread este apelul *interrupt()* pentru instanta thread-ului. Acest apel seteaza un flag intern pentru thread ce poate fi verificat de catre thread in sine prin metoda *isInterrupted()*. In plus, apelul *interrupt()* face ca oricare metoda blocanta precum *sleep()* sau join() sa isi opreasca executia si sa arunce o exceptie.
```
public class MyThread extends Thread {

   public void run (){
      while (!isInterrupted()) {
         System.out.println("spending time doing nothing...");
         Thread.sleep(10);

      }
   }
}

...

MyThread lazyThread = new MyThread();
lazyThread.start();
lazyThread.interrupt(); // <-- daca acest apel are loc intr-un moment in care metoda sleep este in executie
                        //     va fi aruncata o exceptie; daca are loc la un alt moment atunci
                        //     flag-ul intern de intrerupere va fi setat
                        //     si la urmatoarea iteratie thread-ul isi va inceta executia
```

### Alte exemple

* PrinterThread - cod pentru un thread ce afiseaza litere dintr-un string
* SumThread - cod pentru un thread ce calculeaza o suma pe un interval
* Test - cod ce include cazuri de test pentru exemplele de mai sus

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
