# UI Module

Contains all Elm source files.

These get built, and copied to `web/src/main/resources/static` so that SpringBoot can simply host them.

The main idea is that we'll route pages via Spring MVC, and every page loads the corresponding Elm file/page.

We want to keep this as a separate directory, in the case when we eventually want to split the UI from the backend.
This is why the UI is a separate module/directory and not included in the Web module.

We're using the [gradle-elm plugin](https://github.com/tmohme/gradle-elm-plugin), so if stuff goes haywire, go there to figure stuff out.

## Stuff about navigation
We want our links to be shareable with your friends (i.e. sworn enemies), so that they can just paste those links in their browser and will have the correct page loaded for them.

To do that, I was thinking _Let's hook that into the `init` function_. But!
When we link to a url like this: `http://localhost:8000/challenge/1234`, the `live-elm` plugin tries to render an index.html in challenge/index.html, where there is none.

To circumvent that, we tried to have the page load occur via a button from within the Elm loop, using `pushUrl`. But! Then we don't pass via the `init` function which actually tries to fetch the opponent's player info.

What happens if we try it out as if it were actually hosted somewhere, or rather, serve the Elm frontend via SpringBoot. Do we get the same behavior?

Then we get this output in the console:

```
Failed to load resource: the server responded with a status of 404 ()
b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e:1 Refused to apply style from 'http://localhost:8080/challenge/style.css' because its MIME type ('application/json') is not a supported stylesheet MIME type, and strict MIME checking is enabled.
b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e:17 Uncaught ReferenceError: Elm is not defined
    at b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e:17
b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e:1 Refused to apply style from 'http://localhost:8080/challenge/style.css' because its MIME type ('application/json') is not a supported stylesheet MIME type, and strict MIME checking is enabled.
```

or in the network tab:
```
b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e	200	document	Other	1.1 kB	11 ms
main.js	404	script	b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e	477 B	80 ms
style.css	(canceled)	stylesheet	b7c362e8-3ba0-450a-9e62-7f2c2ae89e3e	0 B	101 ms						
```

So, after a week of thinking about this problem and not finding a solution, during the livestream I finally figured out, that the main.js was not being loaded from the root, but instead from the relative path.
Which made complete sense in fact, because of this:
```html
<script type="text/javascript" src="main.js"></script>
```
So, all I needed to do was to specify that main.js should always be loaded from the root and I added a slash:
```html
<script type="text/javascript" src="/main.js"></script>
```

That fixed everything!