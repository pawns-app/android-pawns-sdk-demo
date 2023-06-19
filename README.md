![alt text](https://pawns.app/wp-content/uploads/2022/12/pawns-app-dark.svg)

# Pawns SDK  #
-------------
## An internet sharing library for Android.  ##

Contact our representative to get terms and conditions and collect all information needed.

### Content ###
- [Information needed](#information-needed)
- [Installation](#installation)
- [Setup](#setup)
- [How to use](#how-to-use)
- [Increase SDK service run time](#increase-sdk-service-run-time)
  - [Battery optimisation settings](#battery-optimisation-settings)
  - [Launch on boot complete](#launch-on-boot-complete)
- [License](#license)

### Summary ###

* Min Sdk 23

### Information needed ###

* API KEY

## Installation ##

Add jitpack repository to your project level build.gradle
````
allprojects {
    maven {
        url 'https://jitpack.io'
        credentials { username 'jp_e49n19d4qo4q6futud00uhqesp' }
    }
}
````

Within build.gradle (:app) dependencies section add library
````
implementation'com.github.IPRoyal:android-pawns-sdk:x.y.z'
````

## Setup ##

Setup internet sharing service notification channel name (displayed to users in application settings under notification section). Default value is "Sharing service".

````
   <application
   ...

        <meta-data
            android:name="com.iproyal.sdk.pawns_service_channel_name"
            android:value="My Internet Sharing" />

    </application>
````

Make a few updates in the onCreate method of your app's Application class

Use Pawns.Builder to build and setup SDK for later use in your application. It is a mandatory to provide your API key as it is used to identify your developer account when Internet Sharing service is running.
Optionally, you can provide service configuration to modify what exactly foreground service notification will display to user, when sharing service is launched.
**TIP** *It is recommended to setup service configuration, because it acts as an explanation to the user why your application is running foreground service.*

Setup code example:

    override fun onCreate() {
        super.onCreate()
        
        Pawns.Builder(this)
            .apiKey("Your api key here")
            .serviceConfig(
                ServiceConfig(
                    title = R.string.service_name,
                    body = R.string.service_body,
                    smallIcon = R.drawable.ic_demo_icon
                )
            )
            .build()
    }  



### How to use ####

Our SDK provides 3 main functionalities

* Starting service
* Stopping service
* Exposing state of service

#### Starting service ####
````
Pawns.instance.startSharing(context)
````
#### Stopping service ####
````
Pawns.instance.stopSharing(this)
````
#### Observing state of service ####

Depending on your technology stack this may vary, but we covered two main use cases, which hopefully will help you out.

#### Coroutines and Composable ####

Collecting service state for composable components is very similar to collecting state from viewModel. Pawns instance exposes coroutines StateFlow serviceState, which can be observed easily.
````
val state = Pawns.instance.serviceState.collectAsState(Dispatchers.Main.immediate)
````
#### Xml and Listeners ####

Within your activity or fragment implement our PawnsServiceListener, which will get triggered every time our service state changes.
````
public interface PawnsServiceListener {
    public fun onStateChange(state: ServiceState)
}
````
We provide with listener register/unregister methods
````
    override fun onCreate() {
        super.onCreate()
        Pawns.instance.registerListener(this)
    }

    override fun onDestroy() {
        super.onPonDestroyause()
        Pawns.instance.unregisterListener()
    }  
````

### Increase SDK service run time ###

#### Battery optimisation settings ####

The Android system stops a service only when memory is low and it must recover system resources for the activity that has user focus. If the service is bound to an activity that has user focus, it's less likely to be killed; if the service is declared to run in the foreground, it's rarely killed.

Our library is using foreground services. By default android system does not kill foreground services, but a fair number of manufacturers such as “Xiaomi”, “Honor” and others have their own battery optimisation layer and kills foreground services after awhile.

One of the solutions is to request users to go to your application settings and remove battery optimisation or in other words remove battery restrictions.
````
Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
    data = Uri.parse("package:" + context.packageName)
}
````
#### Launch on boot complete ####

Android applications can declare a BroadcastReceiver, which gets triggered when android device restarts and finishes booting. When receiving this event, simply Pawns.instance.startSharing(context).

If you have not done initialisation during onCreate() of your application class, then you will need to initialise Pawns when receiving boot complete action.

Some android devices have a special application setting option to allow AutoStart. If this option is not enabled, then action for boot complete will not be received.


# License #
~~~~
 Copyright 2022 Pawns.app.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
~~~~