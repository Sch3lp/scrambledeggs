# UI Module

Contains all Elm source files.

These get built, and copied to `web/src/main/resources/static` so that SpringBoot can simply host them.

The main idea is that we'll route pages via Spring MVC, and every page loads the corresponding Elm file/page.

We want to keep this as a separate directory, in the case when we eventually want to split the UI from the backend.
This is why the UI is a separate module/directory and not included in the Web module.