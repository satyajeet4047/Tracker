# Tracker
* eSignature – This will allow a user to sign on the mobile screen. Convert this to an image and save it in SD
card.
* Photo – This will allow a user to select existing photos from gallery and display it on screen (multi select)
or take new pictures and save in SD card.
* Location - The real-time accurate location of the user at a regular interval of 2
minutes. Location updated only when app is foreground.
* Permissions - Runtime permissions implemented.

#### The app has following packages:
1. **ui**: View classes along with their corresponding ViewModel.
2. **utils**: Utility classes.
3. **service** : background services.


#### Components Used :
* LiveData -Architecture Component
* ViewModel - Architecture Component
* Navigation- Architecture Component
* Location - Google maps api


#### Third-party Library Used :
* Picasso & Picasso-transformations - Image loading Libraries [link](https://github.com/square/picasso)
* Signature Pad - For drawing signatures [link](https://github.com/gcacace/android-signaturepad)


#### Todo
- [ ] Cache - Store selected photo information in database. 
- [ ] Animation for map marker and live tracking route.
- [ ] Refactor - Create base classes for activity, fragment.
- [ ] Unit Tests

## Demo

<img src="/art/tracker_app.gif" width="250" height="400"/>
