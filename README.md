## Alarm

A mock-up of the native Alarm App. <strike>This app is hooked up to a test environment.</strike>

## Prerequisites

The following should be locally installed:

* Git
* Maven 3.0.3+
* Android SDK

### Clone this project

```bash
git clone https://github.com/askcs/alarm.git
cd alarm
```

## IDE integration

### IntelliJ

* press <kbd>Open Project</kbd>
* select `alarm/pom.xml`

### Eclipse

* see: http://rgladwell.github.io/m2e-android

## Using Voice in this app

* Make sure you have a Dutch TTS voice installed (like Daan, from Acapela)
* Be aware that the app is completely Dutch, so configuring an English TTS voice will not make it work in English.

Once installed, hit the mic icon in the ActionBar and speak the command. The command is based on 
keywords given to any UI element. 
For example: you're in the MainActivity and would like to go to Settings, hit the mic icon and speak "Instellingen" (Dutch for "Settings"). The TTS utters "Instellingen" and the SettingsActivity is launched. Give it also a try in Settings -> New Alarm at an EditText. 
