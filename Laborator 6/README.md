# Laborator 6
## Variabile de tip ThreadLocal in Java

Java ofera un tip prin care se pot instantia variabile locale unui anumit thread la runtime. Mai precis, un thread poate obtine propria copie initializata a unei anume variabile, asupra carei copii va avea exclusivitate in ce priveste modificarile. Tipul ce ofera aceasta facilitate este *ThreadLocal* si ofera urmatoarele metode:

```
public class ThreadLocal {

   protected T initialValue( );

   public T get( );

   public void set(T value);

   public void remove( );

}
```

Clasa este folosita in mod uzual pentru instantierea unui obiect ThreadLocal static ce va fi utilizat de mai multe thread-uri pentru a obtine instante locale pentru o variabila. Metoda *initialValue()* este cea care este tipic apelata in mod automat cand o variabila locala este initializata de un anume thread. Aceasta se intampla in momentul in care thread-ul apeleaza *get()* pentru prima data pentru a obtine valoarea. Singura exceptie apare cand thread-ul apeleaza *set()* pentru a seta in mod explicit valoarea initiala propria inainte ca *get()* sa fie apelat pentru prima data. Orice apel ulterior al *get()* dupa ce valoarea variabilei este setata local va returna valoarea curenta pentru un anume thread. Metoda *remove()* poate fi utilizata pentru a "reseta" variabila, in sensul ca dupa aceasta un apel intern catre *initialValue()* se va executa din nou pentru re-initializare.

Un exemplu de utilizare este oferit de documentatia Java, cu scopul de a obtine ID-uri unice pentru thread-uri:

```
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueThreadIdGenerator {

   private static final AtomicInteger uniqueId = new AtomicInteger(0);

   private static final ThreadLocal uniqueNum = 
      new ThreadLocal () {
         @Override protected Integer initialValue() {       //implementarea metodei initialValue()
            return uniqueId.getAndIncrement();              //pentru a obtine valoarea dorita intr-un anume thread         
      }
   };
 
   public static int getCurrentThreadId() {
      return uniqueNum.get();
   }
} 


class MyThread extends Thread {
		
   public void run() {
      int myId = UniqueThreadIdGenerator.getCurrentThreadId();            //va rezulta intern in 
                                                                          //apelarea metodei initialValue()
      System.out.println("My Id is "+myId+" .. definitely it is "+
                          UniqueThreadIdGenerator.getCurrentThreadId());  //acest apel va returna pur si simplu 
                                                                          //aceeasi valoare ca apelul precedent
                                                                          //- valoarea locala pentru acest thread                                            		
}	
}
```

## Exercitii
1. In cadrul cursului au fost prezentate doua variante de lock, TAS si TTAS ce folosesc o variabila AtomicBoolean, descrise in pseudocodul de mai jos. Creati o clasa numita CCASLock, in care pentru functionalitatea de locking utilizati un obiect de tip AtomicInteger similar cu modalitatea de implementare din lock-ul TTAS, dar folosind metoda [*compareAndSet*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html#compareAndSet-int-int-). Obiectul de tip AtomicInteger va fi considerat *locked* cand valoarea este 1 si *unlocked* cand valoarea este 0.\
   Comparati implementarea realizata cu un lock TAS precum cel descris la curs. Protejati un contor partajat de mai multe threaduri folosind implementarile lock-urilor, si mentineti si un contor local per thread pentru a observa numarul de accese in sectiunea critica a fiecarui fir de executie, similar cu cerintele relativ la testarea lock-ului Petersen dintr-un laborator trecut. Folositi ca valoare 300000 pentru limita contorului partajat. Executati experimente cu 4 si 8 thread-uri si obtineti o statistica cu urmatoarele informatii: tipul de lock folosit, numarul de thread-uri, dimensiunea contorului, numarul de incrementari realizate de fiecare thread, timpul total de executie.

   ```
   class TASlock {

      AtomicBoolean state = new AtomicBoolean(false);

      void lock() {
         while (state.getAndSet(true)) {}
      }
 
      void unlock() {
         state.set(false);
      }
   }
 
   class TTASlock {

      AtomicBoolean state = new AtomicBoolean(false);

      void lock() {
         while (true) {
            while (state.get()) {}
            if (!state.getAndSet(true))
               return;
         }
      }

      void unlock() {
         state.set(false);
      }
   } 
   ```

2. O echipa de programatori a dezvoltat algoritmul de lock prezentat in pseudocodul urmator. *ThreadId* se considera a fi o clasa ce furnizeaza un id unic pozitiv fiecarui thread. Intr-o executie concurenta a n > 1 thread-uri, este acest algoritm starvation-free? Argumentati. Hint: Considerati un caz cu n = 2 pentru o demonstratie, ce poate fi generalizat la n thread-uri.

   ```
   class ShadyLock {
      private volatile int turn;
      private volatile boolean used = false;

      public void lock() {
         int me = ThreadId.get();
         do {
            do {
               turn = me;
            } while (used);
            used = true;
         } while (turn != me); 
      }
   
      public void unlock () {
         used = false;
      }
   }
   ```

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
