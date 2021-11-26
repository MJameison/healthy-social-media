# healthy-social-media

Group repository for CS407

## PostboxNotification

N.B. Requires android 10 arbitrarily -- will likely relax this later on but when creating a vritual device ensure that SDK >= 29 is selected

This is an android app that creates a background service that monitors notifications and can intercept them.
It passes on the intercepted notifications to the app itself (TODO) for control and resending at a later date.
The aim is to allow users to control the notifications they recieve and determine which need to be immediately addressed and which can wait until a set later time.

Currently the service (InterceptionService) runs from when the app is first opened until uninstall -- it survives force quits and restarts for example. At the moment no mechanism to interact with the notifications exists although permissions for this are given.

## WebTemplate

This is a basic web template setup to help later on

To use the requirements.txt file to set up your Venv for the project (as of writing just a flask API)

Use : \
pip install -r requirements.txt

If theres like a Virtual environment that isn't there or anything make a Venv using
:\
python -m venv venv

Then for windows (This should work but I had to install a plugin for my IDE for bat files) :
venv\Scripts\activate

Or for Linux and Mac:
source myproject/bin/activate
