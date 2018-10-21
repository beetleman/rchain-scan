# rchain-scan

generated using Luminus version "3.10.6"

FIXME

## Prerequisites

You will need [Docker] and [docker-compose]

or

[Leiningen] 2.0 or above installed.

First options is preferred and described in doc

[Leiningen]: https://github.com/technomancy/leiningen
[Docker]: https://docs.docker.com/install/
[docker-compose]: https://docs.docker.com/compose/install/

## Running

To start a web server for the application, run:

    docker-compose run --rm --service-ports repl lein run

## Running tests
Before starting tests, populate rchain with at least 10 blocks:

    docker-compose up rchain
    docker-compose exec rchain bash -c "/scripts/generate.sh 10"

To start tests in "watch" mode

    docker-compose run --rm tests

To run tests once

    docker-compose run --rm tests lein test


## Run repl

    docker-compose up repl

### run app

connect to repl using Emacs `cider-connect-clj`(C-c M-c) and:

``` clojure
(start)
```

### run ClojureScript compiler

after conecting to repl type

``` clojure
(start-fw)
```

or use `cider-connect-cljs`(C-c M-C) and you will get it for free


## License

Copyright © 2018 FIXME
