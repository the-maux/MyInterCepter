# MyInterCepter
Fork-Projet IntercepterNG, reversed App

#### WhatIsThis
  MyInterCepter is an network map to scan and install an MITM attack to the hostanmes he discover.
  The point is the refactor all the app and after put new features

#### ToInstall
    - Cloner le dépot
    - Ajouter son local.properties
    - Réouvrir ce projet avec ton IDE avec "app" gradle

#### TODO
  * [**X**] Clean les process lorsque l'application s'arete au travers d'un service
  * [**X**] Changer tout les path de globalVariable dans Single ou dispatch
  * [**X**] Intégration Ruby=> Metasploit
  * [**X**] Réaliser un vrai visuel design
  * [**X**] Créer un BottomBar groupant les actions MITM
  * [**X**] DORA diagnotique
  * [**X**] Supprimer les actions Cepter
  * [**X**] Intégrer Python=> WPScan.py [etc...]
  * [**X**] Automatiser l'attaque ARP
  * [**X**] Wrapper :: Tcpdump, Nmap, ArpSpoof, ping, etc
  * [**X**] 
 

#### TOFIX


#### Example :
* Afficher le resultat d'une commande shell:

         Log.d(TAG, "whoami:" + new BufferedReader(new RootProcess("Whoami").exec("id").getInputStreamReader()).readLine());
