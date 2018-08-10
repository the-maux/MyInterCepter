# ![D.A.O](https://github.com/the-maux/MyInterCepter/blob/master/startmeBaby.gif?raw=true)
[TODO](https://github.com/the-maux/MyInterCepter/projects/1)

#### ToInstall
    - Cloner le dépot
    - Ajouter son local.properties
    - Réouvrir ce projet avec ton IDE avec "app" gradle

#### Example :
* Afficher le resultat d'une commande shell:
         Log.d(TAG, "whoami:" + new BufferedReader(new RootProcess("Whoami").exec("id").getInputStreamReader()).readLine());
