# Velvet

A storyboard creation Application for simple and shareable representations of a project's identity. 
This would allow new age film makers, stylists, designers and others to store ideas and ongoing collaborations
while also having a way to share storyboards in an easily accessible manner. The Application currently supports
images and notes

# Project Status

The Current implementation of velvet uses a Firebase database & Firebase Realtime Database to store and upload media or labels and create projects. 
Further abstraction of how data is represented was necessary to further abstract the activities and reduce coupling. 
The chosen Architecture is the Android M(odel).V(iew).V(iew)M(odel). The recent update integrates a MVVM architecture on the mainActivity.
Views will be responsible for creating a ViewModel instance which acts as a mediator between the view and the Repository. The Repository will do all the 
operational work of retrieving and writing data to the database. This draws several advantages such as 
the abstraction of data operations, ease of updates and unit testing. 

Also in this architecture the View Model is responsible for notifying an Adapter that data has been altered and that a UI update is necessary. 
The View-model will then send the current data to the adapter. This Adapter will handle all UI changes. The next version of Velvet in development 
will have some of the ongoing methods listed at the bottom of the page.


# Files Structure 

- Views
    - MainActivity 
        - The MainActivity stores all the users current projects. The user is currently able to create a Project, navigate to a particular projects page or navigate to the settings page. In future updates users will be able to delete, rename, and sort projects.
    - ProjectsActivity
        - On this page users can View and upload media in project instances. This currently includes images and labels but we would like to also support audio and video soon. 
    - SettingsActivity
        - Gives a user the ability to sign out of the application and view the current email of the user. 
    - SignInAcitivity
        - Sign in to application using google sign in client
        
- Models
    - MainViewModel
        - Handles all interaction with the Repository. Projects adapter and user main activity both use the same instance of the mainViewModel to complete their respective tasks.
    - ProjectsViewModel
        - Not yet implemented but it will essentially work in the same function as the main view model. 
- Repositories
    - MainRepository
        - Performs all MainActivity database queries. The Only object that communicates with the repository is the mainViewModel. 
    - ProjectsRepository
        - Not yet implemented but it will essentially work in the same function as the MainRepository
        
- Adapter
    - ProjectsAdapter
        - This adapter is used for populating Project views onto the Main Activity. Data is received from the mainViewModel and is used to update the representational data of MainActivity. GridLayoutManager uses a recycler view to create the grid structure views will be placed in. 

- Fragments
    - AddContentDialog
        - Handles the user interaction of creating new media items. A dialog gives the options: Image, Audio, and Label. On image selection the user is directed too their photo library and on image selection helper functions assist with importing the image into the application and uploading it to the database.

    - ProjectNameDialog
        - Handles the creation of a new project. On selection of the floating action button the user is prompted to enter a new name for a project. The new Project is created and stored in the database.

- Helper
    - Time
        - This helper class holds methods for retrieving and formatting the current date and time. These are especially necessary when creating a new project.
    - UserSingleton 
        - Synchronizes singleton instance of user across application. Only one instance is needed to get user info.
 
- Project 
    - Project object used for storing new user objects. 

# Database noSQL Structure 

Info of the Application is stored in Firebase Realtime database and media is stored in Firebase Storage both of which are cloud hosted databases. The database contains 3 collections: Users, Projects, and MediaCluster which holds a reference to media items stored  in firebase storage. 

# Prerequisites for Installation

- Minimum SDK Version: 17
- Target SDK Version: 29

- Latest Version Tested on:
    - Android Studio 4.2.1
    - Android 8.1

# Needs for Further implementation 

- Import & export of user Audio 
- Rearrange Main page order(by name or date) 
- Replace button icon 
- Delete Projects Functionality
- Rearrange Layout of media 
- Delete media functionality
- Export of and conversion of projects (pdf/powerpoint/zip)
- Camera implementation
- ArView Visualize physical products (ex.Fashion/furniture/art/ jewelry collection)

@Author Victor Chuol
