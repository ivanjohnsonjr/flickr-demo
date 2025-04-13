# Flickr-demo


Simple Android application to display information from Flickr api. The Home view contains a search box and a list of images, the user is allow to enter a string in the search box and when hit enter the search will retrieve the new result. 

When the application initial loads the user will see a list of image with any search text. This is the result of the recent Flickr photos.

__100% Kotlin__   
__100% Compose UI__

### API methods:
- flickr.photos.getRecent
- flickr.photos.search
- flickr.photos.getInfo


### Requirements:
- Android Studio Meerkat

### Libraries:
- Coil
- Retrofit
- GSON
- Serialization
- Timber
- Hilt
- Mockk - Unit testing
