# Laborator 10
## Exercitii

1. Se considera exemplul de coada limitata lock-based discutat in cadrul cursului cu metodele *enq* si *deq* descrise ca in pseudocodul de mai jos. Considerand ca membrul size este de tip AtomicInteger, este necesara in metoda *deq* plasarea *size.getAndDecrement()* in cadrul sectiunii protejate de *deqLock*? Argumentati.

   ```
   public class BoundedQueue<T> {
      ReentrantLock enqLock, deqLock;
      AtomicInteger size;
      Node head, tail; 
      int capacity;
      Condition notFullCondition, notEmptyCondition;

      public BoundedQueue(int capacity) {
         this.capacity = capacity;
         this.head = new Node(null);
         this.tail = head;
         this.size = new AtomicInteger(0);
         this.enqLock = new ReentrantLock();
         this.notFullCondition = enqLock.newCondition();
         this.deqLock = new ReentrantLock();
         this.notEmptyCondition = deqLock.newCondition();
      }


      public void enq(T x) {
         boolean mustWakeDequeuers = false; 
  	
         enqLock.lock();
         try { 
            while (size.get() == capacity) {
               notFullCondition.await(); 
            }
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            if (size.getAndIncrement() == 0) {
               mustWakeDequeuers = true;
            }
         } finally {
            enqLock.unlock();
         }

         if (mustWakeDequeuers) {
            deqLock.lock();
      	    try {
               notEmptyCondition.signalAll();
      	    } finally {
               deqLock.unlock();
            }
         }
      }

      public T deq() {
         boolean mustWakeEnqueuers = false;
         T v;
  
         deqLock.lock();
         try {
            while (head.next == null) {
               notEmptyCondition.await();
            }
            v = head.next.value;
    	    head = head.next;
    	    if (size.getAndDecrement() == capacity) {
               mustWakeEnqueuers = true;
            }
         } finally { 
            deqLock.unlock(); 
         } 
  
         if (mustWakeEnqueuers) {
    	    enqLock.lock();
    	    try {
               notFullCondition.signalAll();
    	    } finally {
               enqLock.unlock();
    	    }
         }
  	
         return v;
    
      }
  
      protected class Node {

         public T value;
         public Node next;
    
         public Node(T x) {
            value = x;
            next = null;
         }
      }

   }
   ```

2. Pentru exemplul de coada din exercitiul anterior, presupunem ca in clasa interna *Node* ar exista un membru ReentrantLock *nodelock* (similar cu lacatul din structura unui nod individual din lista fine-grained), si ca ne folosim de acest lacat din nodul *head* in locul *enqLock* si respectiv de lacatul din nodul *tail* in locul *deqLock*, dupa cum e detaliat mai jos. Vor mai functiona corect in acest caz metodele *enq* si *deq* de exemplu: pastrand caracterul FIFO al cozii, din perspectiva ordinii thread-urilor ce le apeleaza? Argumentati.

   ```
   public void enq(T x) {
      boolean mustWakeDequeuers = false; 
  	
      head.nodelock.lock();
      try { 
         while (size.get() == capacity) {
            notFullCondition.await(); 
         }
         Node e = new Node(x);
         tail.next = e;
         tail = tail.next;
         if (size.getAndIncrement() == 0) {
            mustWakeDequeuers = true;
         }
      } finally {
         head.nodelock.unlock();
      }

      if (mustWakeDequeuers) {
         tail.nodelock.lock();
         try {
            notEmptyCondition.signalAll();
         } finally {
            tail.nodelock.unlock();
         }
      }
   }

   public T deq() {
      boolean mustWakeEnqueuers = false;
      T v;
    
      tail.nodelock.lock();
      try {
         while (head.next == null) {
            notEmptyCondition.await();
         }
         v = head.next.value;
         head = head.next;
         if (size.getAndDecrement() == capacity) {
            mustWakeEnqueuers = true;
         }
      } finally { 
         tail.nodelock.unlock(); 
      } 
        
      if (mustWakeEnqueuers) {
         head.nodelock.lock();
         try {
            notFullCondition.signalAll();
         } finally {
            head.nodelock.unlock();
         }
      }
        
      return v;

   }

   protected class Node {

      public T value;
      public Node next;
      public ReentrantLock nodelock;
    
      public Node(T x) {
         value = x;
         next = null;
         nodelock = new ReentrantLock();
      }
   }
   ```

3. Implementati algoritmul de coada limitata lock-based introdus in cadrul cursului si descris in exercitiul 1 de mai sus. Considerati dimensiunea cozii 1000. Masurati eficienta implementarii calculand o medie a timpului pe operatie pentru n = 100000 operatii *enq* (t_enq_size) si respectiv *deq* (t_deq_size) executate de 4 thread-uri rulate simultan (2 enq si 2 deq).\
   Executati si un test de corectitudine prin scaderea numarului de operatii executate de un thread deq la o valoare intre 99501 si 99999, si verificarea diferentei dintre adaugari si eliminari ca numar de elemente asteptat sa ramana in coada la finalul executiei.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
