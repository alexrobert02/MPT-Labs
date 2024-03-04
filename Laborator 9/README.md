# Laborator 9   
## Exercitii

1. Se da implementarea urmatoare de referinta a listei optimiste. Adaptati implementarea listei pentru algoritmul lazy descris in curs. Realizati o evaluare comparativa a implementarilor. Executati experimente cu 4 si 8 thread-uri, jumatate din acestea executand operatii de adaugare, respectiv jumatate operatii de eliminare elemente din lista. Fiecare thread va incerca sa adauge, respectiv sa elimine 10000 de elemente generate random in intervalul 1-10000. Retineti rezultatele intr-o statistica ce va include ca informatii numarul de threaduri, tipul de lista si timpul de executie pentru fiecare experiment.


2. Adaptati algoritmul de lista optimista prin introducerea unei versionari, astfel incat operatiile ce implica modificarea listei sa incrementeze la fiecare schimbare un numar de versiune. Incercati sa va folositi de aceasta versiune pentru eficientizarea fazei de validare. Testati implementarea si comparati performanta acesteia cu varianta initiala de lista optimista - se cere evaluarea timpului mediu pentru o operatie de add/remove/contains la un experiment cu 4 thread-uri ce executa aceeasi operatie pe o lista cu 100000 de elemente.

---
Sursa: https://profs.info.uaic.ro/~eonica/mpt/index.html
