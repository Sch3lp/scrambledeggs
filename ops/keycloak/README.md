# Rebuilding the Scrambled Realm

I need to do this, every time the docker database is wiped for whatever reason.

Create a new realm, call it `Scrambled`.

In said realm, add a new client, call it `scrambled-ui`.

Enable `implicit-flow`.

Switch access type to `confidential` in stead of `public`.

Also turn `Service Accounts Enabled` to `On`.

Then fill in `Valid Redirect Uri's` as `http://localhost:8000/`.

And add `+` in `Web Origins` to not get CORS errors in your browser.

Once you save, a new `Credentials` tab will become visible.

There you'll find the secret, which we don't need at all.

Then, finally, to make sure that the elm ui can interpret the userinfo as it expects, we'll need to add a custom `mapper` for the `scrambled-ui` client.

So go to the `Mappers` tab, click `Create`.

`Mapper Type` : `User Property`  
`Property` : `username`  
`Token Claim Name` : `name`  
`Claim Json Type` : `String`  

Make sure `Add to Userinfo` is `Enabled`.