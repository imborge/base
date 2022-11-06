(ns clj.new.base
  (:require
   [clj.new.templates :refer [->files name-to-path year
                              sanitize-ns project-name
                              renderer]]
   [selmer.parser :as selmer]))

(defn render-template
  [template options]
  (selmer/render
   (str "<% safe %>" template "<% endsafe %>")
   options
   {:tag-open \< :tag-close \> :filter-open \< :filter-close \>}))

(defn rand-str
  [n]
  (->> (repeatedly #(char (+ (rand 26) 65)))
       (take n)
       (apply str)))

(defn base
  [name & feature-params]
  (let [render  (renderer "base" render-template)
        options (merge
                 {:full-name             name
                  :name                  (project-name name)
                  :ns-name               (sanitize-ns name)
                  :sanitized             (name-to-path name)
                  :default-cookie-secret (rand-str 16)
                  :year                  (year)
                  :features              (set feature-params)})]
    (println "Generating fresh 'clj new' imborge/base project")
    (println "with these features:" (:features options))
    (->files options
             [".dir-locals.el" (render ".dir-locals.el" options)]
             [".gitignore" (render ".gitignore" options)]
             
             ["deps.edn" (render "deps.edn" options)]
             ["build.clj" (render "build.clj" options)]
             ["README.md" (render "README.md" options)]

             ["env/dev/clj/{{sanitized}}/dev_middleware.clj" (render "env/dev/clj/dev_middleware.clj" options)]
             ["env/dev/clj/{{sanitized}}/env.clj" (render "env/dev/clj/env.clj" options)]
             ["env/dev/clj/user.clj" (render "env/dev/clj/user.clj" options)]
             ["env/dev/resources/logback.xml" (render "env/dev/resources/logback.xml" options)]

             ["env/test/resources/logback.xml" (render "env/test/resources/logback.xml" options)]

             ["env/prod/clj/{{sanitized}}/env.clj" (render "env/prod/clj/env.clj" options)]
             ["env/prod/resources/logback.xml" (render "env/prod/resources/logback.xml" options)]

             ["resources/system.edn" (render "resources/system.edn" options)]
             ["resources/migrations/placeholder.txt" (render "resources/migrations/placeholder.txt" options)]
             ["resources/sql/queries.sql" (render "resources/sql/queries.sql")]

             ["src/clj/{{sanitized}}/config.clj" (render "src/clj/config.clj" options)]
             ["src/clj/{{sanitized}}/core.clj" (render "src/clj/core.clj" options)]
             ["src/clj/{{sanitized}}/ig_utils.clj" (render "src/clj/ig_utils.clj" options)]

             ["src/clj/{{sanitized}}/db/conman.clj" (render "src/clj/db/conman.clj" options)]
             ["src/clj/{{sanitized}}/db/postgres.clj" (render "src/clj/db/postgres.clj" options)]
             ["src/clj/{{sanitized}}/db/migratus.clj" (render "src/clj/db/migratus.clj" options)]
             
             ["src/clj/{{sanitized}}/web/handler.clj" (render "src/clj/web/handler.clj" options)]
             ["src/clj/{{sanitized}}/web/undertow.clj" (render "src/clj/web/undertow.clj" options)]
             ["src/clj/{{sanitized}}/web/controllers/health.clj" (render "src/clj/web/controllers/health.clj" options)]
             ["src/clj/{{sanitized}}/web/middleware/core.clj" (render "src/clj/web/middleware/core.clj" options)]
             ["src/clj/{{sanitized}}/web/middleware/exception.clj" (render "src/clj/web/middleware/exception.clj" options)]
             ["src/clj/{{sanitized}}/web/middleware/formats.clj" (render "src/clj/web/middleware/formats.clj" options)]
             ["src/clj/{{sanitized}}/web/routes/api.clj" (render "src/clj/web/routes/api.clj" options)]
             ["src/clj/{{sanitized}}/web/routes/utils.clj" (render "src/clj/web/routes/utils.clj" options)]

             ["test/clj/{{sanitized}}/test_utils.clj" (render "test/clj/test_utils.clj" options)]
             ["test/clj/{{sanitized}}/core_test.clj" (render "test/clj/core_test.clj" options)])))
