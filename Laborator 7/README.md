# Laborator 7
## Lock-uri readers-writers

In multe situatii accesul la un obiect partajat de mai multe thread-uri implica operatii atat de citire cat si de scriere (modificare) a obiectului respectiv. In general thread-urile ce executa operatii de citire, nemodificand starea obiectului, nu necesita sincronizare intre ele. Accesul exclusiv este deci necesar doar intre thread-urile ce executa operatii de scriere, sau intre acestea si cele ce executa operatii de citire.\
In acest context, apare ca pattern des utilizat un tip de lock numit generic *read-write lock*, format practic din doua lock-uri utilizate concomitent *read lock* si *write lock*, ce asigura urmatoarele proprietati de safety:
* niciun thread nu poate obtine componenta write lock, cat timp un alt thread detine fie componenta write lock, fie componenta read lock
* niciun thread nu poate obtine componenta read lock, cat timp un alt thread detine componenta write lock

Un exemplu de lock simplu ce implementeaza acest pattern este urmatorul:

```
public class SimpleReadWriteLock {
  
   int readers;
   boolean writer;
   Lock helperLock;
   Lock readLock;
   Lock writeLock;
   Condition condition;
  
   public SimpleReadWriteLock() {
      writer = false;
      readers = 0;
      helperLock = new ReentrantLock();
      readLock = new ReadLock();
      writeLock = new WriteLock();
      condition = helperLock.newCondition();
   }
   public Lock readLock() {
      return readLock;
   }
   public Lock writeLock() {
      return writeLock;
   }

   protected class ReadLock implements Lock {
   
      public void lock() {      
      helperLock.lock();
      try {
         while (writer) {
            try { condition.await(); } 
            catch (InterruptedException e) {}
         }
         readers++;
      } finally {
         helperLock.unlock();
      }
   }

   public void unlock() {
      helperLock.lock();
      try {
         readers--;
         if (readers == 0)
            condition.signalAll();
      } finally {
         helperLock.unlock();
      }
   }
        
}

   protected class WriteLock implements Lock {
   
      public void lock() {
         helperLock.lock();
         try {
            while (readers > 0 || writer) {
               try { condition.await(); } 
               catch (InterruptedException e) {}
            }
            writer = true;
         } finally {
            helperLock.unlock();
         }
      }
        
      public void unlock() {
         helperLock.lock();
         try {
            writer = false;
            condition.signalAll();
         } finally {
         helperLock.unlock();
         }
      }
    
   }
  
}

//Mod de utilizare:

//instanta lock partajata de readers si writers
SimpleReadWriteLock rwLock = new SimpleReadWriteLock();

//In metoda run a unui thread reader:
public void run() {
   rwLock.readLock().lock() 
   try {
   // acces read la resursa partajata
   }
   finally {
      rwLock.readLock().unlock()
   }
} 

//In metoda run a unui thread writer:
public void run() {
   rwLock.writeLock().lock() 
   try {
   // acces write la resursa partajata
   }
   finally {
      rwLock.writeLock().unlock()
   }
} 
```

## Exercitii

1. Implementati exemplul *read-write* lock furnizat mai sus si testati lacatul respectiv in contextul protejarii unui contor partajat accesat de doua seturi distincte de thread-uri: readers care doar vor citi contorul si writers care vor incrementa contorul. Fiecare thread writer va realiza operatia de scriere intr-o bucla cu 100000 de iteratii. Thread-urile reader vor realiza operatia de citire similar intr-o bucla, dar isi vor incheia executia dupa ce contorul a atins valoarea asteptata in urma incrementarilor. De asemenea, fiecare thread reader isi va numara propriile citiri intr-un contor propriu separat. Contorul partajat va fi protejat de un read-write lock comun thread-urilor, folosind modul de utilizare descris mai sus. Executati teste cu minim 4 thread-uri din fiecare tip pornite simultan in ordine preferabil alternativa (primul writer dupa care primul reader, al doilea writer dupa care al doilea reader, samd). Masurati timpul total de executie si afisati si numarul de citiri efectuate. Inlocuiti protectia cu lacatul *read-write-lock* cu un simplu ReentrantLock folosit de toate threadurile si observati diferenta in timpul de executie.


2. Cum puteti rezolva problema accesului neechilibrat al thread-urilor reader la sectiunea critica in defavoarea thread-urilor writer? Implementati solutia gasita si verificati noul numar de citiri.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
