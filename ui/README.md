# UI Module

Contains all Elm source files.

These get built, and copied to `web/src/main/resources/static` so that SpringBoot can simply host them.

The main idea is that we'll route pages via Spring MVC, and every page loads the corresponding Elm file/page.
