# ontheboat-api

Backend service for ontheboat.

This is a Spring Boot 3 application that uses JPA to talk to a Postgres database.

It uses OpenFeign to talk to the Auth0 authorization servers.

Users are Oauth authenticated using Auth0. A user that gets registered in Auth0 then has a profile created in the database. 
This profile is used for all ownership, onboard requests, etc.

This api provides
Boat Controller:
- Free text search that returns all boats that match the search string.
- Free text search using paging.
- Media for a boat using a given media id
- Details for an individual boat

Crew Controller:
- Returns the profile for the user based on the provided auth token.
- Submits an ownership change request
- Update a users profile.
