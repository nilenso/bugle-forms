# Bugle Forms

Simple forms to collect information or conduct surveys.

Like the famed bugle whose simple, no-valve harmonic notes relay instructions
for soldiers to assemble, Bugle Forms helps you easily assemble the information
you need to make your analytics, polls or events work.

Resemblance to the name of any megacorporation is purely incidental.

## Local setup

Ensure you have Leiningen installed.

Clone this repository, and then:

Setup database:

```
docker-compose up -d
```

Run migrations:

```
lein migrations migrate
```

Build and run the application:
```
lein uberjar
docker build -t bugle-forms .
docker run -dp 8080:8080 bugle-forms
```

You should now see the app running in `localhost:8080`.

## Migrations

To perform various migration tasks:

```
lein migrations migrate          # complete pending migrations
lein migrations rollback         # rollback to previous state
lein migrations up <ids ...>     # migrate specified ids
lein migrations down <ids ...>   # rollback specified ids
lein migrations create <name>    # generate migration files with specified name
```

## License

Copyright © 2022 Nilenso

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
