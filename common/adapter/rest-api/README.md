# REST API module

This is the adapter to our domain so we can make our domain available through SpringBoot's RestControllers.

It should only depend on our Domain's API.

The main idea is to build a _Task Based_ REST API, so this thing can actually double as our "service layer" that tells us what the actions are we can do in our system.
If we need to not _comply_ to RESTful API standards, then that's ok. But we'll try to keep RESTful API standards as much as we can.
