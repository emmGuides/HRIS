# A prototype for a mobile HRIS using Firebase as the database, Android Studio (with Java) as the IDE


# Development Log:
      June 21, 2022: 
		- Initialized Android Studio project, HRIS
		- Started making standard Register and Login views 
		- Added validation for correct user inputs on both the Register and Log In Views
		- Linked Application to a Firebase Realtime Database
		- Added Employee Write Operations for Name, Age, and Email (details written not final) to database
		- Added User Log in and Registration functionalities
		- Added a placeholder for the home screen after user logs in
		- Changed application logo/icon
		- Current Application Demonstration: https://youtu.be/YMS57gfBR6A  
      
      June 22, 2022:
		- Changed Homescreen's empty view to Android Navigation Drawer
		- made a Figma Design file
		- made high fidelity wireframes for the pre-existing Login and Register pages
		- made high fidelity wireframe for the rest of the homepage basic layout
		- made high fidelity wireframe for navigation drawer display
		- made high fidelity wireframe for calendar display
		- TODO: Try and Finish wireframes of different fragments from navigation drawer
		- Figma Design link: https://www.figma.com/file/X7z3bAinWjwX29zwR19xhI/HRIS?node-id=0%3A1
		- current application demonstration link: https://youtu.be/rGHMzYtdf3g

      June 23, 2022:
		- made high fidelity wireframes for Teams page
		- made high fidelity wireframes for Vacation Leave Application page
		- made high fidelity wireframes for Sick leave Application page
		- made high fidelity wireframes for Profile Page
		- wireframes for main functionalities finished
		- Added Github repository branches for Navigation UI/UX and Menu editing/experimentation
		- Successfully added functional Home, Calendar, Teams, Vacation Leave Request, Sick Leave Request Fragment and Views (background to be updated) in the 		  navigation bar
		- Changed icons for Home, Calendar, Teams, Vacation leave request, Sick leave request in the navigation bar
		- current application demonstration link: https://youtu.be/1QPmaLZollQ 
	
      June 24, 2022:
	     	- Successfully Added Firebase Signout functionality
		- Successfully Added user Email verification (turned off for now during development)
		- Added new branch in GitHub (navbar-display-testing) to test recently found bugs
		- Navigation Bar Header now correctly displays user's name and email
		- Bug fixing took up more time than initially anticipated.
		- Finished the Nav Bar UI
		- app demo link: https://drive.google.com/file/d/1H-UKZI8wRfbvOy8D2NwriGxK6T6xoP0K/view?usp=drivesdk
		
      June 27, 2022: 
		- Added Vacation Leave Form
		- Added Sick Leave Form
		- Added calendar toggle for both forms' start and end dates for their respective leave dates
		- Added empty/null user input checks for both forms, on all fields
		- Created new github branch to debug found bugs
		- YouTube Demo link: https://youtu.be/8opwW905qFg
		
      June 28, 2022:
		- Fixed threading bug found yesterday
		- Vacation leave form and Sick leave form now functional
		- Successfully established Firebase DB connection for dates and details in Vacation Leave request Form.
		- Successfully established Firebase DB connection for dates and details in Sick Leave request Form.
		- YouTube Demo link: https://youtu.be/taxS_z-l2Ro
		
      June 29, 2022:
		- Added homescreen basic layout
		- added dummy placeholders for announcements and attendance
		- added button to display time ins (current time on click) 
		- added button to add time out (current time on click), when timed in 
		- Added greeting that displays authenticated user's first name.
		- Added GitHub branch time-ins-outs to facilitate changes around time ins and outs
		- Demo Link: https://youtu.be/_WtjdjNkip4
		
      June 30, 2022:
		- modified homepage to have ScrollView
		- locked all views to portrait
		- overhauled home screen UI
		- Added today's date display
		- now displays current timed in duration (time out minus time in)
		- Time ins, out, duration of time in, added in DB with key(child) as current date
		- organized keys for vacation and sick leaves in DB
		- organized registered users' details under "User Details" child in DB
		
      July 1, 2022:
		- Added read operations for time ins and outs
		- Refactored some parts of the code
		- Fixed a bug found yesterday
		- reformatted pushed DB values to contain Date datatypes
      
      July 4, 2022:
 		- added confirmation dialog when user logs out
		- added confirmation dialog when user times in
		- added confirmation dialog when user times out
		- changed appbar text color, icon colors
		- changed appbar background
		- changed home screen's draft format
		- added on back pressed methods on some views to cover an app vulnerability
      
      July 5, 2022:
		- Created User Profile Fragment
		- added user profile fragment to the navigation drawer
		- added icon to the user profile item
		- added placeholder images for Teams and Calendar fragments
		- created DB Read methods for the user profile fragment details (name, age, email)
		- slightly modified homescreen UI
		
      July 6, 2022:
		- fixed layout on User Profile Fragment
		- Added User Details edit buttons
		- Added Dialog boxes that ask for new details to be updated
		- Added checks for EditText fields on Name and Age Dialog
		- Users can now Change name and age since it is also updated in the DB
		- Email and Password updates pushed behind schedule since they require authentication.
		
      July 7, 2022:
		- Removed edit info Icons
        	- replaced icons with onLongClick event listener
        	- Added info icon and message to let users know they can Long Tap on the fields
        	- Added calendar placeholder
        	- Successfully read time ins and outs all child and children
        	- Start finding ways on how to display all time ins and outs as a log
      
      July 8, 2022:
		- Added times in and out log as list
        	- Added Vacation Leave Request log as list
        	- Added Sick Leave Request log as list
        	- made all logs display necessary details
        	- Added Log Out description to the log out button
        	- Presented current state of the Application to Sach
		
      July 11, 2022:
		- Created Overtime Fragment and added it in the navigation
		- Created Offset Fragment and added it in the navigation
		- Added Fields for users to fill up in Overtime Fragment
		- Added necessary icons for these newer fragments in the navigation drawer
		- Added field checks for all fields in the Overtime Fragment
		- Form Button now pushes Hash map in the database from all the user fields
		
      July 12, 2022:
		- Added Offset form fields input checks
        	- Added User inputs into a Hashmap
        	- Successfully push hashmaps into DB
        	- Reformatted previous Sick and Vacation forms' data structure to hashmaps
        	- Added Buttons for Dialog implementations of Logs in calendar fragment
        	- Transferred Lists inside Dialogs
        	- Removed lists on Calendar Fragment
		
      July 13, 2022:
		- Added list view and empty view inside time in and out log dialog
		- Added list view and empty view inside sick leave log dialog
		- Added list view and empty view inside vacation leave log dialog
		- Added List view (from hashmap) and empty view inside overtimes log dialog
		- Added List view (from hashmap) and empty view inside offsets log dialog
		- Overtimes and offsets now reads, writes, and displays data from realtime database
		- All buttons in the calendar fragment have their UIs fixed and updated.
		- Entire Calendar Fragment and all Logs Display currently done
      
      July 14, 2022:
		- Added and adjusted fields in the vacation leave request form based on the sent layout
		- Added and adjusted fields in the sick leave request form based on the sent layout
		- Modified all data from forms to push Hashmaps (key-value pairs) of each pair of data required
		- Added edit user profile button
		- added checks for all fields in the user profile button
		- added dialog for edit user profile
		- new profile names and ages on this dialog now gets pushed to DB
		
      July 15, 2022:
		- Adjusted Offset form's fields in order to look better
		- Adjusted Overtime form's fields in order to look more appealing
		- Added labels to all input text fields
		- Added forgot password View
		- Added reset password functionality through email
		- Met with Sach for a weekly progress review of the application
		
      Sach's recommendations (July 15):
		- divider navigation drawer
		- no records available or found
		- Create upload function (clarify file format if yes)
		- Change 'Confirm' to Close icon (X button)
		- Remove Password Field display in Profile page
		- What Ms. Ariessa really wants to see in the HomePage, and the Teams page
