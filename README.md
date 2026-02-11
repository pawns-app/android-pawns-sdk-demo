![GitHub Latest Release)](https://img.shields.io/github/v/release/pawns-app/android-pawns-sdk-demo?logo=github&style=flat&color=5324CE)

![Pawns App](https://pawns.app/app/uploads/2022/12/pawns-app-dark.svg)

# Pawns SDK  #
-------------

## An internet sharing library for Android  ##


<div style="border: 2px solid orange; padding: 10px; border-radius: 12px;">

⚠️ <strong>IMPORTANT</strong>

We have added a <strong>consent screen requirement</strong>. Updating to 1.8.0 or above will require you to inform users about our terms and conditions and get their consent. More information is available in <strong>Consent</strong> section.

</div></br>

Contact our representative to get terms and conditions and collect all information needed.

### Content ###
- [Information needed](#information-needed)
- [Installation](#installation)
- [Setup](#setup)
- [Consent](#consent)
- [How to use](#how-to-use)
- [Service types](#service-types)
- [Main Functionality](#main-functionality)
- [Foreground service](#foreground-service)
- [Increase SDK service run time](#increase-sdk-service-run-time)
  - [Battery optimisation settings](#battery-optimisation-settings)
  - [Launch on boot complete](#launch-on-boot-complete)
- [License](#license)

### Summary ###

* Min Sdk 21
* Target Sdk 35

### Information needed ###

* API KEY

## Installation ##

Add jitpack repository to your project level build.gradle
````
allprojects {
    maven { url 'https://jitpack.io' }
}
````

Within build.gradle (:app) dependencies section add library
````
implementation 'app.pawns:android-pawns-sdk:x.y.z'
````

## Setup ##

This is split into two different sections. This one is main including library setup. Next section is **consent**, as it might differs for different projects, it has its own section. Our SDK comes with **Background** and **Foreground** services. It is up to you to decide which one suits your case in the best way and add it to your project.

AndroidManifest.xml requires you to declare service you want to use.

**Background**
````
     <service
            android:name="com.pawns.sdk.internal.service.PeerServiceBackground"
            android:exported="false" />
````

**Foreground**

**Note:** Applications targeting Android 14 must declare foreground service type. We suggest using "special use", because it is a long term solution, but it could prompt google play store review, where they might ask for a video and explanation how this service is being used in your application.
For a temporary solution you can use "data sync" type. This type should not prompt google play store review, but it will get **deprecated** later in the future. SDK will check manifest for "data sync" permissions, if it is declared and "special use" is not declared, then service itself will launch by using "data sync" type, although it is your responsibility to declare service in manifest accordingly.

````
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    
    <service
        android:name="com.pawns.sdk.internal.service.PeerServiceForeground"
        android:exported="false"
        android:foregroundServiceType="specialUse">
        <property
            android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
            android:value="Allows to share internet traffic by modifying device's network settings to be used as a gateway for internet traffic. Device becomes a gateway which allows to send and receive internet traffic." />
    </service>
````

Make a few updates in the onCreate method of your app's Application class

Use Pawns.Builder to build and setup SDK for later use in your application. It is a **mandatory to provide your API key** as it is used to identify your developer account when Internet Sharing service is running.
By default SDK will start service as **FOREGROUND** service. Optionally, you can provide service configuration to modify what exactly foreground service notification will display to user, when sharing service is launched.
**TIP** *It is recommended to setup service configuration, because it acts as an explanation to the user why your application is running foreground service.*

Setup code example:

**Kotlin**

````
    override fun onCreate() {
        super.onCreate()

        Pawns.Builder(context)
            .apiKey("Your api key here")
            .serviceConfig(
                ServiceConfig(
                    title = R.string.service_name,
                    body = R.string.service_body,
                    smallIcon = R.drawable.ic_demo_icon
                )
            )
            .serviceType(ServiceType.FOREGROUND)
            .build()
    }  
````

**Java**

````
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Pawns.Builder(context)
                .apiKey("Your api key here")
                .serviceConfig(
                        new ServiceConfig(
                                R.string.service_name,
                                R.string.service_body,
                                R.drawable.ic_stat_name,
                                ServiceNotificationPriority.DEFAULT,
                                null
                        )
                )
                .serviceType(ServiceType.FOREGROUND)
                .build();
    }  
````

## Consent ##

Obtaining user consent is mandatory for any application that wishes to use this library.  
Without consent, internet sharing will not work.

Users must be informed about the applicable terms and conditions and must be able to either agree to or refuse participation in the internet sharing process.
Moreover, users must be able to withdraw their consent at any time. Even after user has already given their consent, they should be able to withdraw their consent later on.


To make this process as easy as possible, the SDK provides a ready-to-use consent screen that can be displayed to users.  
Using the provided screen is optional — you may implement your own UI if preferred. The SDK exposes all necessary methods and required text to support a custom implementation.

The full consent text, including hyperlinks to the relevant terms and policies, is provided as a separate resource file.

#### Recommended Consent flow ####

You may choose when to display the consent screen, but it should be shown either:
- at the start of the application (for example, as part of onboarding), **or**
- immediately before starting internet sharing.

Before showing the consent screen, always check whether the user has already made a choice.

**Kotlin**

````
Pawns.getInstance().isConsentGiven()
````

**Java**

````
Pawns.Companion.getInstance().isConsentGiven();
````

If consent has not yet been given, launch the SDK-provided consent screen by retrieving its activity intent.

**Kotlin**

````
private val consentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        Pawns.getInstance().startSharing(context)
    }
}

// somewhere (e.g. on button click)
val consentIntent = Pawns.getInstance().getConsentIntent()
consentLauncher.launch(consentIntent)
````

**Java**

````
private ActivityResultLauncher<Intent> consentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Pawns.getInstance().startSharing(context);
                    }
                }
        );

// somewhere (e.g. on button click)
Intent consentIntent = Pawns.getInstance().getConsentIntent();
consentLauncher.launch(consentIntent);
````

#### Alternative flow ####

If you do not wish to use the SDK-provided consent screen or require full control over styling and layout, you may provide your own consent UI.  
In this case, you must display the provided consent text and record the user’s decision using the SDK methods.

The flow remains the same — instead of launching the SDK activity, you show your own screen.

To check whether consent has already been given:

**Kotlin**

````
Pawns.getInstance().isConsentGiven()
````

**Java**

````
Pawns.Companion.getInstance().isConsentGiven();
````

When the user accepts or refuses participation, report the result to the SDK:

**Kotlin**

````
Pawns.getInstance().setConsentGiven(true/false)
````

**Java**

````
Pawns.Companion.getInstance().setConsentGiven(true/false);
````

#### Withdrawing consent ####

Withdrawing the consent should be allowed at any time. You should provide a way for users to withdraw their consent somewhere within your application.
It can be done either in **Settings** or in any other suitable location. We recommend using either a button or a switch to allow users to withdraw their consent.

This method can be used to record the user’s decision:

**Kotlin**

````
Pawns.getInstance().setConsentGiven(true/false)
````

**Java**

````
Pawns.Companion.getInstance().setConsentGiven(true/false);
````

### How to use ####

#### Service types ####

There are currently two types of service you can run:
* Background service does not require to display notification, so it is less intrusive to the user. Downside is that application must be
  active and running, otherwise it will get terminated. Putting phone to sleep will stop service as well.
* Foreground service must display a notification as this service can run independently from application. Meaning that it will continue to run even when
  application is not active and not running. If application is closed there are still some specific cases when service will get terminated after a while, but
  we will provide tips how to extend time of running later on.

#### Main Functionality ####

Our SDK provides 3 main functionalities

* Starting service
* Stopping service
* Exposing state of service

#### Starting service ####

**NOTE:** If using foreground service, default startSharing method creates a notification automatically by using ServiceConfig values
provided during initialization process. If your application already have another foreground service running you can optionally provide
startSharing with your current notification object and id, which you pass along when launching your foreground service with startForeground()
method. In this case our service will attach to existing notification instead of creating a new one.

It is recommended to use **onCreate()** method for **foreground** service and **onStart()** method for **background** service.

**Kotlin**

````
Pawns.getInstance().startSharing(context)
````
**Java**

````
Pawns.Companion.getInstance().startSharing(context);
````

#### Stopping service ####

**Kotlin**

````
Pawns.getInstance().stopSharing(context)
````

**Java**

````
Pawns.Companion.getInstance().stopSharing(context);
````

#### Observing state of service ####

Depending on your technology stack this may vary, but we covered two main use cases, which hopefully will help you out.

#### Coroutines and Composable ####

Collecting service state for composable components is very similar to collecting state from viewModel. Pawns instance exposes coroutines StateFlow serviceState, which can be observed easily.
````
val state = Pawns.getInstance().getServiceState().collectAsState(Dispatchers.Main.immediate)
````
#### Xml and Listeners ####

Within your activity or fragment implement our PawnsServiceListener, which will get triggered every time our service state changes. Updating UI within onStateChange method might require using **runOnUiThread**, because the state change happens while running on background thread.
````
public interface PawnsServiceListener {
    public fun onStateChange(state: ServiceState)
}
````
We provide with listener register/unregister methods

**Kotlin**

````
    override fun onCreate() {
        super.onCreate()
        Pawns.getInstance().registerListener(pawnsServiceListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        Pawns.getInstance().unregisterListener()
    }  
````

**Java**

````
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Pawns.Companion.getInstance().registerListener(pawnsServiceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Pawns.Companion.getInstance().unregisterListener();
    } 
````

The current service state can also be read through its value property

**Kotlin**

````
    val lastKnownState = Pawns.getInstance().getServiceStateSnapshot()
````

**Java**

````
    ServiceState lastKnownState = Pawns.Companion.getInstance().getServiceStateSnapshot();
````

## Foreground service ##

Setup internet sharing service notification channel name (displayed to users in application settings under notification section). Default value is "Sharing service".

````
   <application
   ...

        <meta-data
            android:name="com.pawns.sdk.pawns_service_channel_name"
            android:value="My Internet Sharing" />

    </application>
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