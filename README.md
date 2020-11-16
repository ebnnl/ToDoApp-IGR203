# ToDoApp-IGR203
Repository for code of a ToDo List app. Final Project of the Interaction Homme-Machine (IGR203) course @ Telecom Paris.
Android app to organise tasks with different priorities in a group.

**Link to demo video :** https://drive.google.com/file/d/134D5VS6fxOZxJKMCOHiIAYIHypip5_ZV/view?usp=sharing

## Fonctionnalités implémentées
Pour ce prototype d’application de to-do list, nous avons implémenté les fonctionnalités suivantes :
- Créer un groupe
- Créer des membres
- Créer une tâche :
  - Choix du groupe, du responsable et de la deadline
  - Choix de la priorité en fonction des priorités des autres tâches du groupe
- Affichage des tâches :
  - Affichage sous forme de “post-it” dont la taille est proportionnelle à la priorité de la tâche
  - Choix du groupe dont on veut afficher les tâches
  - Filtrer les tâches du groupe par responsable
  - Déplacer les post-it sur l’écran
- Redimensionner le post-it pour modifier la priorité de la tâche
- Affichage des propriétés des tâches 
- Marquer une tâche comme terminée

## Interface
L’interface de notre prototype se compose des activités suivantes :
- Activité principale (**MainActivity**) :
  - En haut, en titre, le nom du groupe dont on veut afficher les tâches
  - En haut à droite, un bouton pour ouvrir un menu déroulant et choisir le groupe à afficher, ou créer un nouveau groupe
  - Sous le titre, le nom de la personne dont on veut afficher les tâches
  - Au centre : les tâches sous forme de post-it. Ce sont des boutons déplaçables et redimensionnables. La couleur du post-it correspond à la couleur du responsable. Toucher un bouton ouvre l’activité de visualisation de la tâche. Toucher longuement un bouton sans faire de mouvement ouvre un bouton pour marquer la tâche comme terminée.
  - A gauche : un bouton pour chaque membre du groupe. Toucher un bouton affiche les tâches du responsable.
  - En bas à droite : bouton pour ouvrir l’activité pour créer une nouvelle tâche

- Activité pour créer un groupe (**CreateGroupActivity**):
  - Champ pour entrer le nom du groupe
  - Une checkbox pour chaque utilisateur déjà créé. Les membres du groupe seront ceux checkés.
  - Bouton pour ouvrir le dialogue pour créer un membre
  - Bouton pour enregistrer le groupe (ce bouton ne s’affiche que lorsqu’un nom de groupe a été entré).

- Dialogue pour créer un membre :
  - Champ pour entrer le nom du membre
  - Choix déroulant pour choisir la couleur du membre

- Activité pour créer une tâche (**CreateTaskActivity**) :
  - Champs pour entrer le nom de la tâche
  - Choix déroulant pour choisir, parmis les groupes existants, le groupe pour lequel on veut créer un tâche. Par défaut, le groupe est le groupe qui était choisi dans l’activité principale.
  - Ensemble de boutons radios pour choisir le responsable de la tâche. Un bouton radio pour chaque membre du groupe sélectionné. 
  - Bouton pour ouvrir un dialogue avec un calendrier pour définir la deadline.
  - Curseur pour définir la priorité de la tâche. Les priorités des autres tâches du groupe sélectionné sont repérées par des points sur la barre du curseur. En déplaçant le curseur, un texte apparaît pour indiquer la tâche du groupe dont la priorité est inférieure à celle du curseur, et celle dont la priorité est supérieure.
  - Bouton pour enregistrer la tâche (ce bouton ne s’affiche que lorsqu’un nom de tâche a été entré.

- Activité pour visualiser et éditer une tâche (**SeeTaskActivity**) :
  - Bouton avec le nom de la tâche, dont la couleur dépend du responsable de la tâche, et dont la taille est proportionnelle à la priorité. 
  - Champ de texte contenant le nom de la tâche. Champ modifiable pour éditer le nom de la tâche.
  - Curseur pour visualiser et/ou éditer la priorité de la tâche
  - Bouton pour enregistrer les modifications


## Organisation du code

Les données des tâches et des groupes sont stockées dans une base de données. Pour interagir avec cette base de données (Ajouter/enlever des tâches, etc..), on utilisera les méthodes de la classe **DAOBase**. Tout objet DAOBase possède une référence vers la même base de données accessible par la classe **DataBaseHandler**, que l’on n’utilisera jamais directement. Toute activité ayant besoin d’avoir accès aux données des tâches aura sa propre instance de DAOBase dans ses attributs.

La classe DAOBase récupère les données depuis la base de données, et les structure dans une instance de la class **GroupsList**. Cette classe est une liste d’instances de la classe Group. Elle représente l’ensemble des groupe créés par l’utilisateur. Un objet de la classe **Group** possède 3 attributs : le nom du groupe, la liste des personnes du groupe et les tâches du groupe. Les personnes sont des objets de la classe **Person**, qui est constitué de 2 attributs : le nom de la personne et la couleur représentant cette personne dans l’interface. Les tâches sont des objets de la classe **Task**, qui possède 7 attributs : le nom de la tâche, sa priorité, sa deadline, sa position sous forme de coordonnées x et y, des références vers le groupe auquel elle appartient et son responsable.

Lors de certaines opérations graphiques, la base de données est mise à jour. C’est le cas lorsque par exemple, on valide une tâche comme effectuée, on repositionne le “post-it” représentant la tâche dans l’interface, ou lorsqu’on le redimensionne, car la taille de la tâche est directement liée à sa priorité. Afin de bien tenir compte de ces changements dans l’interface, on recharge les données de MainActivity à l’aide de la fonction loadContent(). 

La détection de repositionnement ou resizing d’une tâche se fait à l’aide des classes **MultiTouchListener et OnPinchListener** qui implémentent les listeners correspondants. Etant donné que le repositionnement ou le resizing modifient la base de données, il est nécessaire de passer une référence de la tâche au constructeur de ces listeners.

La classe **DottedSeekBar** est une customisation de la classe SeekBar d’Android, nous permettant d’ajouter des points de contrôle sur un curseur. En effet, nous voulions que l’utilisateur puisse définir la priorité de la tâche à l’aide d’un curseur, tout en pouvant visualiser la priorité des autres tâches. La classe DottedSeekBar nous permet donc de placer des points sur le curseur, correspondants aux priorités des autres tâches.

