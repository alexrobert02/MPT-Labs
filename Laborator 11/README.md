# Laborator 11
## Exercitii

1. In algoritmul de coada limitata lock-based introdus in cadrul cursului (si descris in exercitiul 1 din laboratorul anterior), presupunem ca in loc de utilizarea conditiilor *notFullCondition* si *notEmptyCondition*, si a flagurilor *mustWakeDequers*, respectiv *mustWakeEnqueuers*, pentru notificari intre threaduri, metodele *enq* si *deq* se vor folosi pur si simplu de o operatie de spinning, ca mai jos. Ce problema credeti ca apare in aceasta situatie in utilizarea algoritmului pentru coada?

   ```
   public void enq(T x) {
      boolean mustWakeDequeuers = false; 
  	
      enqLock.lock();
      try { 
         while (size.get() == capacity) {}; //spinning
         Node e = new Node(x);
         tail.next = e;
         tail = tail.next;
         size.getAndIncrement();
      } finally {
         enqLock.unlock();
      }
   }

   public T deq() {
      boolean mustWakeEnqueuers = false;
      T v;
  
      deqLock.lock();
      try {
         while (head.next == null) {}; //spinning
         v = head.next.value;
         head = head.next;
         size.getAndDecrement();
      } finally { 
         deqLock.unlock(); 
         return v;
      } 
   }
   ```

2. Eficientizati algoritmul de coada limitata lock-based din laboratorul anterior descris si in cadrul cursului, prin impartirea contorului de dimensiune in doua contoare separate pentru operatiile de *enq()* si respectiv *deq()*, cu scopul de a reduce interferenta intre acestea. Testati implementarea din laboratorul anterior modificata in acest mod in aceleasi conditii folosind 4 threaduri rulate simultan (2 enq si 2 deq) si 100000 de operatii per thread, calculand mediile timpului pe operatie (t_enq_split pentru *enq*, t_deq_split pentru *deq*). Comparati rezultatele obtinute in cele doua variante calculand ratiile t_enq_split/t_enq_size, respectiv t_deq_split/t_deq_size (t_enq_size si t_deq_size sunt masuratorile de timp obtinute in laboratorul anterior pentru varianta initiala).\
   Executati si un test de corectitudine similar celui din laboratorul anterior.\
\
   Hint: O idee de impartire a contorului *size* din algoritmul original in doua contoare separate poate pleca de la folosirea unui contor cu incrementare *enqSize* pentru operatia *enq*, si respectiv a unui contor cu decrementare *deqSize* pentru operatia *deq*. Initial ambele contoare vor fi zero. Dimensiunea cozii va fi la orice moment egala cu suma acestor doua contoare ce indica numarul de elemente adaugate, respectiv numarul de elemente eliminate din coada (*deqSize* este in permanenta egal cu zero sau negativ).\
Un thread ce adauga elemente va testa in metoda *enq()* dimensiunea contorului *enqSize* si va realiza adaugarea atat timp cat respectivul contor este mai mic decat capacitatea cozii. La atingerea capacitatii, threadul respectiv poate incerca obtinerea *deqLock*, pentru a realiza urmatoarele operatii ce folosesc ambele contoare:\
a) *enqSize* = *enqSize* + *deqSize* - actualizarea efectiva a dimensiunii listei\
b) *deqSize* = *0* - resetarea contorului elementelor eliminate\
Adaptarea algoritmului initial (inclusiv partea deq) si integrarea mecanismului de mai sus poate imbunatati performanta prin sincronizarea contoarelor doar periodic cand se estimeaza posibila depasire a capacitatii, spre deosebire de varianta de baza care implica utilizarea aceluiasi contor atomic simultan in orice apel *enq* sau *deq*.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
