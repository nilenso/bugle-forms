# ADR 1: Server-side sessions for user identification

## Context

There are many ways to authenticate and identify a user in a web
application, each with their own tradeoffs. We are looking for a
solution suitable for a web app like Bugle Forms, which is assumed to
always be run from a browser.

For Bugle Forms, the solution needs to be:
- simple for the app user
- transparently understood and owned by the developer
- support learning of how authentication is performed
- flexible to infrastructure change

These options are currently popular:
- Session IDs sent through cookies and checked against a data store on
  the server side. The ongoing sessions' data can be stored in various
  ways:
  - In the server memory, using the default option of Ring's [sessions
    middleware](https://github.com/ring-clojure/ring/wiki/Sessions)
  - In a persistent store, like the server's filesystem
  - In the same database where the application state resides
  - In an external key-value caching service, like Redis
  - All of the above, but using Ring's cookie middleware, instead of the
    session middleware
- Session data can also be stored in the client side using encrypted
  cookies, and is supported by Ring's session middleware
- Using JWTs which is similar to the above in that it is client-side but
  is a standardized format and can be transported in many ways:
  - JWTs stored in and sent via cookies
  - JWTs stored in localStorage and sent via request headers using
    client-side JavaScript
  - JWTs stored in cookies and sent via request headers using
    client-side JavaScript
- Using a third-party authorization service, eg: Sign In with Google

## Decision

To begin with, we will use server-side session state using Ring's session
middleware for user authentication and identification. The sessions will be
stored in server memory for simplicity. Later down the line, we will store the
sessions in an external data store that is exposed through Ring's `SessionStore`
protocol.

## Status

Accepted

## Consequences

Storing the session data on server side is a mature and better
understood method of user identification for web applications. The
implementation is easier and more transparent than JWTs when using Ring
middlewares and the likelihood of implementation mistakes leaving open
security holes is lower.

The session store can be easily swapped out no change to the application
logic. All an application has to do is implement Ring's `SessionStore`
protocol for this to work (and several libraries provide an
implementation for the same such as `carmine` for Redis).

This solution assumes that the client is always a web browser, and the
app server is additionally responsible for authenticating the user.

Storing the sessions in server memory would mean that sessions are lost
for every app server restart. This will be mitigated in the future by
having an external session store.
