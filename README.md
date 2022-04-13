**Note**: The latest changes are found in the `fill-form-story` that have not been merged due to incomplete tests.

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

## Making a deployment on a VPS

We use docker compose to deploy to a remote server. Ensure you have the following in place:

- A user on the server named 'deploy'. In the `/home/deploy/.config/bugle-forms` folder, ensure you have:
    - A file named `db_password.txt` which contains the password for the database you that will be provisioned.
    - A file named `secrets.edn` which contains configuration for the deployment. (See config format section below)

To make the deployment, you need to run `docker-compose -f deploy.yml up -d` on the server side. We do this by setting the `DOCKER_HOST` variable to the ssh url of the remote server. Also ensure that `ENVIRONMENT` is set to either `prod` or `staging`.

## Secrets configuration

An example `secrets.edn` config file that uses all the available configuration options:

``` edn
{:db-spec  {:dbtype   "postgresql"
            :dbname   "bugle_prod"
            :user     "bugle_prod"
            :password "tops3cr3t"
            :host     "db"
            :port     5432}}
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
