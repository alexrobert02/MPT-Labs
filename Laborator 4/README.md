# Laborator 4
## Tipuri de date atomice in Java

Platforma Java ofera in pachetul *java.util.concurrent.atomic* cateva implementari pentru tipuri de date atomice. Printre acestea se regasesc urmatoarele:

* **AtomicBoolean** - Valoare booleana ce poate fi actualizata atomic
* **AtomicInteger** - Valoare intreaga ce poate fi actualizata atomic
* **AtomicReference\<V>** - Referinta de obiect ce poate fi actualizata atomic

Fiecare dintre aceste clase ofera o serie de metode comune ce pot fi utile in sincronizare, precum:
* **get()**
  - returneaza valoarea curenta a instantei, avand acelasi efect relativ la memorie precum citirea unei variabile volatile
* **set(newValue)**
  - seteaza valoarea curenta la cea specificata ca argument, avand acelasi efect relativ la memorie precum scrierea unei variabile volatile
* **getAndSet(newValue)**
  - returneaza vechea valoare si seteaza noua valoare data ca argument; are un efect atomic ce inglobeaza practic executia unui apel *get()* urmat de un apel *set()*
* **compareAndSet(expectedValue, newValue)**
  - daca valoarea curenta a obiectului este cea precizata ca *expectedValue*, aceasta va fi schimbata cu argumentul dat ca *newValue*, in mod atomic; apelul va returna true in cazul schimbarii valorii, sau false in caz contrar

## Exercitiu

1. Se da pseudocodul de mai jos ca varianta pentru algoritmul lui Peterson de excludere mutuala ce ofera o generalizare pentru n thread-uri. Ideea este de a trece fiecare thread printr-un filtru de n-1 nivele pana la accesul la sectiunea critica. i poate fi considerat ca identificator al unui thread iar L ca numar al nivelului. Tabloul level asociat nivelelor retine nivelul curent pentru fiecare thread, iar tabloul victim retine identificatorul fiecarui ultim thread ce a avansat la respectivul nivel.

   ```
   lock() {
      for (int L = 1; L < n; L++) {
         level[i] = L;
         victim[L] = i; 
         while (( exists k != i with level[k] >= L ) &&
            victim [L] == i ) {}; 
         }
      }

   unlock() {
      level[i] = 0;
   }
   ```

   Scrieti un program ce foloseste algoritmul lui Peterson generalizat de mai sus pentru a proteja un contor partajat. Fiecare thread va incrementa acest contor. In plus, fiecare thread va numara accesele proprii la contorul partajat intr-un tablou separat de dimensiune n (fiecare thread va avea asociat un element din tablou). Thread-urile se vor opri cand contorul va atinge valoarea limita 300000. Programul va afisa timpul total de executie, valoarea finala a contorului si a numarului de accese per thread din tabloul mentionat mai sus. Rulati programul cu cel putin 4 thread-uri.\
   Observand rezultatele, ce puteti spune despre proprietatea de *fairness* (bounded waiting) a algoritmului lui Peterson generalizat in comparatie cu algoritmul pentru 2 thread-uri?\
   Hints: In situatia in care observati durate foarte mari ale executiei, folositi o limita mai joasa pentru contor. Pentru tablourile level si victim tipul recomandat pentru utilizare este AtomicInteger (folosirea volatile va face doar referintele tablourilor volatile, nu si elementele acestora).

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
