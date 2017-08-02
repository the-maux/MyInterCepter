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
  * [**X**] Clean les activity des codes doublon
  * [**X**] Utiliser un maximum les process dans l'objet RootProcess
  * [**X**] fragmenter tout le code procédural en fonction
  * [**X**] Refactoriser la majorité des activité lourde en fragment simple
  * [**X**] Créer le AppTabLayout
  * [**X**] Changer les icones
  * [**X**] Changer les theme des couleurs par un vrai styles::*primary_*_colour
  * [**X**] 
  * [**X**] 
  * [**X**] 
  * [**X**] 
 

#### TOFIX


#### Example :
* Afficher le resultat d'une commande shell:

         Log.d(TAG, "whoami:" + new BufferedReader(new RootProcess("Whoami").exec("id").getInputStreamReader()).readLine());
