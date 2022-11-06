# base

A Clojure(Script) starter template made for myself to support rapid app development. Heavily inspired / copied from [clj-kit](https://clj-kit.github.io/).

## Usage

FIXME: write usage documentation!

Creating a project from this template (the `:new` alias in this template project defaults `:template` to base):

```bash
    clojure -X:new :name myname/myproject
    cd myproject
```

Build a deployable jar of this template:

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the JAR in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

Install it locally (requires the `ci` task be run first):

    $ clojure -T:build install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment
variables (requires the `ci` task be run first):

    $ clojure -T:build deploy

Your template will be deployed to net.clojars.imborge/base on clojars.org by default.

## License

Copyright © 2022 Børge André Jensen
