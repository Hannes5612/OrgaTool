# Organizing tool
This is a simple tool which will help you get your live organized.
Data is stored on a seperate server and cann be accessed with only username and password.

Main features are the simple creation of tasks and the neat looking windows you can choose between to get different views of your tasks.
It will also feature the current time, date and weather.

Submission date is 10th of August 2020

# Run the Program

Simply download the orgatool.jar from the main directory and execute it. Java sdk 11+ is needed to run it.

## API Documentation

Can be found under https://hfrey.de/orgadoc/apidocs/index.html


## Note

Please mind, that JavaFX 11 suffers from proper HiDPI support on Ubuntu, since its refering to a outdated settings-variable.
If you use such a screen make the application scale with the following command:

``` gsettings set org.gnome.desktop.interface scaling-factor <factor>``` 

The factor for e.g. a 4k screen would be 2 :)

## Screenshots

![Login Page](/screenshots/LoginPage.png)
![Overview Screen](/screenshots/MainScreen.png)