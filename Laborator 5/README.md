# Laborator 5
## Operatii de coordonare folosind monitorul obiectelor din Java. Conditii.

Pe langa cuvantul cheie *synchronized* prin care un thread poate sa obtina acces exclusiv in executia unei metode a unei instante prin intermediul monitorului (lock) intern al respectivei instante, acest monitor intern mai permite si urmatoarele operatii de coordonare in acest context:
* *wait()* - acest apel suspenda thread-ul apelant si elibereaza monitorul intern (lock-ul obtinut prin synchronized) ce poate fi preluat de alt thread; thread-ul suspendat re-obtine monitorul cand este notificat (trezit) prin unul din apelurile urmatoare
* *notify()* - acest apel notifica (trezeste) un thread oarecare dintre cele suspendate in urma apelului de mai sus; thread-ul trezit va obtine din nou lock-ul intern corespunzator instantei pentru blocul synchronized, dar nu inainte ca thread-ul ce a apelat *notify* sa incheie executia acestui bloc sau sa o re-suspende (deci thread-ul trezit va mai astepta pana la respectivul moment)
* *notifyAll()* - similar cu *notify* cu diferenta ca toate thread-urile suspendate vor fi trezite aflandu-se in competitie pentru a obtine lock-ul intern

Apelul oricarei dintre metodele de mai sus are evident sens doar in contextul unui bloc synchronized (deci doar daca thread-ul care face apelul detine monitorul intern al instantei curente a obiectului). Un exemplu de utilizare pentru o coada simpla e cel de mai jos:

```
class SimpleQueue<T> {
   private T [] items;
   private int tail = 0, head = 0, count = 0;

   public SimpleQueue(int size)
   {
      items = (T[])new Object[size];
   }
 
   public synchronized void enq(T x) {
      while(count == items.length) 
      {
         try { wait(); }
         catch (InterruptedException e) { } 
         finally { } 
      }
      System.out.println("Enqueuing " + x);
      items[tail] = x;
      if (++tail == items.length) { tail = 0; } 
      count++; 
      notify(); 
   }
    
   public synchronized T deq() {
      while (count == 0) 
      {
         try { wait(); }
         catch (InterruptedException e) { } 
         finally { } 
      } 
      char x = items[head]; 
      if (++head == items.length) { head = 0; }
      count--;
      System.out.println("Dequeuing " + x); 
      notify(); 
      return x;
   }
}
```

In anumite situatii este preferabila utilizarea unui lock explicit in locul monitorului intern. In aceste situatii, exista posibilitatea de a coordona intr-un mod similar thread-urile ce acceseaza o anumita portiune de cod prin intermediul unor metode specifice unei instante *Condition* obtinuta pentru lock-ul respectiv prin apelul *newCondition()*. Aceste metode sunt respectiv *await()*, *signal()* si *signalAll()*. Un exemplu similar celui de mai sus, ce foloseste aceasta modalitate de coordonare ar fi urmatorul:

```
class SimpleQueue {
   private Lock lock = new ReentrantLock();
   private Condition full = lock.newCondition();
   private Condition empty = lock.newCondition();
   private T [] items;
   private int tail = 0, head = 0, count = 0;

   public SimpleQueue(int size)
   {
        items = (T[])new Object[size];
   }
 
   public void enq(T x) {
      lock.lock();
      try {
         while(count == items.length) 
         {
            try { full.await(); }                 // asteapta cat timp coada este plina
            catch (InterruptedException e) { } 		
            finally { } 
         } 
         System.out.println("Enqueuing " + x);
         items[tail] = x;
         if (++tail == items.length) { tail = 0; } 
         count++; 
         empty.signal();                          // semnaleaza starea cozii ca nemaifiind vida
      } finally {
         lock.unlock();
      }
   }

   public T deq() {
      lock.lock()
      try {
         while (count == 0) 
         {
            try { empty.await(); }                // asteapta cat timp coada este vida
            catch (InterruptedException e) { } 
            finally { } 
         } 
         char x = items[head]; 
         if (++head == items.length) { head = 0; }
         count--;
         System.out.println("Dequeuing " + x); 
         full.signal();                           // semnaleaza starea cozii ca nemaifiind plina
         return x;
      } finally {
         lock.unlock();
      }
   }
}
```

## TEMA

1. Se da urmatoarea secventa (istorie) de executie de mai jos. Este aceasta linearizabila? Dar consistent secventiala? Se considera valoarea initiala r = 0.\
Argumentati raspunsul oferind explicatiile (eventual secventa istoriei de executie) si/sau o diagrama cu punctele de linearizare dupa caz.
  
   ![linerex2.png](https://i.postimg.cc/K8X7L5gC/linerex2.png)


2. De ce in mod obisnuit in utilizarea unui lock se prefera ca apelul lock() sa fie executat inainte de blocul try, si nu in cadrul acestuia (prima varianta de mai jos si nu a doua)? Argumentati.

   ```
   lock inainte de try:
    
   someLock.lock();
   try {
      .....
   }
   finally {
   someLock.unlock();
   }
    
   lock in cadrul try:
    
   try {
      someLock.lock();
      .....
   }
   finally {
      someLock.unlock();
   }	
   ```
   
3. In exemplul de coada furnizat mai sus in cadrul laboratorului, *SimpleQueue*, ce foloseste lock, conditiile primesc semnal la fiecare operatie de adaugare, respectiv eliminare din coada. Consideram o varianta de optimizare in care apelul *signal* este realizat doar la momentul cand respectivele conditii sunt indeplinite. Deci, pentru cazul adaugarii, exclusiv in momentul in care coada devine nevida, iar in cazul eliminarii, exclusiv in momentul in care apare un loc in coada:

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

   public T deq() {
      lock.lock()
      try {
         while (count == 0) 
         {
            try { empty.await(); }		  
            catch (InterruptedException e) { } 
            finally { } 
         } 
         char x = items[head]; 
         if (++head == items.length) { head = 0; }
         count--;
         System.out.println("Dequeuing " + x); 
         if (count == items.length - 1) {
            full.signal(); 		
         }	  
         return x;
      } finally {
         lock.unlock();
      }
   }
   ```
   Exista o problema in aceasta abordare? Argumentati. Hint: Considerati o exemplificare in care coada este initial vida si exista cel putin 2 threaduri consumator si 2 threaduri producator. Puteti analiza doar operatia de adaugare, rationamentul fiind similar in situatia eliminarii.


4. Se considera mai jos exemplul lock-free pentru coada FIFO, cu dimensiune limitata, enuntata in cursul 3, ce poate fi folosita de doua thread-uri, unul producator (apeleaza doar enq) si unul consumator (apeleaza doar deq).

   ```
   public class LockFreeQueue {
      int head = 0, tail = 0;
      int items [] = new int[ QSIZE ];

      public void enq(int x) {
         while ( tail - head == QSIZE ) {};
         items [ tail % QSIZE ] = x; 
         tail ++;
      }

      public int deq () {
         while ( tail == head ) {};
         int item = items [ head % QSIZE ]; 
         head ++;
         return item;
      }
   }
   ```

   a) O prima varianta de generalizare a cozii pentru n thread-uri - mai multi producatori si mai multi consumatori - folosind un lock, este propusa mai jos. Observati o problema in generalizarea propusa? Argumentati raspunsul.

   ```
   public class LockBasedQueue {
      int head = 0, tail = 0;
      int items [] = new int[ QSIZE ];
      ReentrantLock lock = new ReentrantLock(); 

      public void enq(int x) {
         lock.lock();
         try {
            while ( tail - head == QSIZE ) {};
            items [ tail % QSIZE ] = x; 
            tail ++;
         } finally {
            lock.unlock();
         }
      }

      public int deq () {
         lock.lock();
         try {
            while ( tail == head ) {};
            int item = items [ head % QSIZE ]; 
            head ++;
            return item;
         } finally {
            lock.unlock();
         }
      }
   }
   ```

   b) O a doua varianta de generalizare a cozii pentru n thread-uri foloseste doua locks, conform pseudocodului de mai jos. Este aceasta corecta? Argumentati raspunsul.

   ```
   public class DoubleLockBasedQueue {
      int head = 0, tail = 0;
      int items [] = new int[ QSIZE ];
      ReentrantLock enqlock = new ReentrantLock(); 
      ReentrantLock deqlock = new ReentrantLock();
    
      public void enq(int x) {
         while ( tail - head == QSIZE ) {};
         enqlock.lock();
         try {
            items [ tail % QSIZE ] = x; 
            tail ++;
         } finally {
            enqlock.unlock();
         }
      }
    
      public int deq () {
         while ( tail == head ) {};
         deqlock.lock();
         try {
            int item = items [ head % QSIZE ]; 
            head ++;
            return item;
         } fnally {
            deqlock.unlock();
         }
      }
   }
   ```

   In argumentare se pot include si eventuale trace-uri demonstrative pentru executia unor thread-uri.


5. Consideram pseudocodul de mai jos pentru un algoritm de excludere mutuala, in care i reprezinta id-ul unic al thread-ului curent, iar n reprezinta numarul total de thread-uri:

   ```
   function init() {
      for (int k = 0; k < n; k++) {
         flag[k] = false;
         access[k] = false;
         label[k] = k + 1; 
      }
   }

   function lock(i) {
      flag[i] = true;
      do {
         access[i] = false;
         await ( every j != i has ( flag[j] == false || label[j] > label[i] )) {};
         access[i] = true;
      } while ( exists j != i with access[j] == true );
   }

   function unlock(i) {
      label[i] = max(label[0],...,label[n-1]) + 1; 
      access[i] = false;
      flag[i] = false;
   }
   ```

   Nota: Instructiunea *await* din pseudocod de la linia 13 are rolul de a astepta pana ce conditia este indeplinita (echivalent cu utilizarea *while* aplicat negatiei - se asteapta cat timp conditia din linia 13 nu este indeplinita: *while ( exists j != i with ( flag[j] == true && label[j] < label[i] ))* - etichetele nu pot fi egale).\
   a) Demonstrati ca algoritmul propus asigura excluderea mutuala.\
   b) Demonstrati ca algoritmul propus este atat deadlock-free cat si starvation-free.\
   c) Implementati algoritmul propus si modificati-l, pastrand garantiile demonstrate, astfel incat valorile din tabloul label sa nu mai creasca nelimitat. Observati daca apare un dezavantaj intre varianta modificata si cea initiala. Hint: Modificarea poate fi realizata exclusiv prin adaugiri si fara alte schimbari in liniile de cod initiale.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
