SteerClear for Android repo!

Take a look through the source to find the most beautiful code you've seen in your life.

To test, pull down the repo. In strings.xml, there'll be an error (or at least should be) saying
"@string/googleApiKey could not be resolved" or something to that matter. This is on purpose, so we
don't commit api keys. To get GooglePlayServices working, you'll have to regenerate a key from
the Google Developers Console for this project, make a separate xml file called "keys.xml" in your
res/values folder, and paste this in the file:

<string name="googleApiKey">"APIKEY GOES HERE"</string>

Then it should work without a problem. Download to Genymotion or your Android phone to give it a
whirl. To use, you must have a valid W&M account.