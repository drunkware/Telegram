## Telegram Ultra
This is my fork of Telegram with few changes from the main app that are not approved yet!:

* Disable trimming of spaces and newlines in messages to preserve the formatting.
* Simple markdown (Bold text, Color).
* Two options to forward messages (that includes text, video, photo, docs ....etc):
    - With original sender's name (like original app) "Quoted forwarding".
    - Without the original sender's name (will carry your name only) .
* Fix for many Whatsapp emoji which don't translate correctly to Telegram emoji.
* Increase of Emoji rendering size from 50 to 200.
* Increase message size from 2k to 4k.
* Show sender's name on videos, photos and documents.
* Share documents, photos and videos with other android apps.


Here is a list of changes that were approved and got integrated into the official application:
* Shared links with show the title along with the URL

Read more about the changes and updates from my blog [fadvisor.net] (http://www.fadvisor.net/blog/2014/03/telegram/)

## NOTE:
**This code is based on the development branch of Telegram source, some stuff are still under development and not tested thoroughly, use it at your own risk**.

Finally, I would like to thank [DrKLO] (https://github.com/DrKLO) for his work on the original application and sharing the code with us also for helping me with getting some stuff fixed.
<br>
-------
<br>
The original application README content:

## Telegram messenger for Android

[Telegram](http://telegram.org) is a messaging app with a focus on speed and security. It’s superfast, simple and free.
This repo contains the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

##Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

### API, Protocol documentation

Telegram API manuals: http://core.telegram.org/api

MTproto protocol manuals: http://core.telegram.org/mtproto

### Usage

**Beware of using the dev branch and uploading it to any markets, in many cases it not will work as expected**.

First of all, take a look at **src/main/java/org/telegram/messenger/BuildVars.java** and fill it with correct values.
Import the root folder into your IDE (tested on Android Studio), then run project.

### Localization

We moved all translations to https://www.transifex.com/projects/p/telegram/. Please use it.
