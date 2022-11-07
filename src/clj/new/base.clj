(ns clj.new.base
  (:require
   [clj.new.templates :refer [->files name-to-path year
                              sanitize-ns project-name
                              renderer]]
   [selmer.parser :as selmer]
   [clojure.pprint]))

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

(def available-features #{"+cljs" "+frontend"})

(def feature-dependencies
  {"+cljs" #{"+frontend"}})

(def core-files
  {".dir-locals.el" ".dir-locals.el"
   ".gitignore"     ".gitignore"

   "Makefile"  "Makefile"
   "deps.edn"  "deps.edn"
   "build.clj" "build.clj"
   "README.md" "README.md"

   "env/dev/clj/{{sanitized}}/dev_middleware.clj" "env/dev/clj/dev_middleware.clj"
   "env/dev/clj/{{sanitized}}/env.clj"            "env/dev/clj/env.clj"
   "env/dev/clj/user.clj"                         "env/dev/clj/user.clj"
   "env/dev/resources/logback.xml"                "env/dev/resources/logback.xml"

   "env/test/resources/logback.xml" "env/test/resources/logback.xml"

   "env/prod/clj/{{sanitized}}/env.clj" "env/prod/clj/env.clj"
   "env/prod/resources/logback.xml"     "env/prod/resources/logback.xml"

   "resources/system.edn"                 "resources/system.edn"
   "resources/migrations/placeholder.txt" "resources/migrations/placeholder.txt"
   "resources/sql/queries.sql"            "resources/sql/queries.sql"

   "src/clj/{{sanitized}}/config.clj"   "src/clj/config.clj"
   "src/clj/{{sanitized}}/core.clj"     "src/clj/core.clj"
   "src/clj/{{sanitized}}/ig_utils.clj" "src/clj/ig_utils.clj"

   "src/clj/{{sanitized}}/db/conman.clj"   "src/clj/db/conman.clj"
   "src/clj/{{sanitized}}/db/postgres.clj" "src/clj/db/postgres.clj"
   "src/clj/{{sanitized}}/db/migratus.clj" "src/clj/db/migratus.clj"

   "src/clj/{{sanitized}}/web/handler.clj"              "src/clj/web/handler.clj"
   "src/clj/{{sanitized}}/web/undertow.clj"             "src/clj/web/undertow.clj"
   "src/clj/{{sanitized}}/web/controllers/health.clj"   "src/clj/web/controllers/health.clj"
   "src/clj/{{sanitized}}/web/middleware/core.clj"      "src/clj/web/middleware/core.clj"
   "src/clj/{{sanitized}}/web/middleware/exception.clj" "src/clj/web/middleware/exception.clj"
   "src/clj/{{sanitized}}/web/middleware/formats.clj"   "src/clj/web/middleware/formats.clj"
   "src/clj/{{sanitized}}/web/routes/api.clj"           "src/clj/web/routes/api.clj"
   "src/clj/{{sanitized}}/web/routes/utils.clj"         "src/clj/web/routes/utils.clj"

   "test/clj/{{sanitized}}/test_utils.clj" "test/clj/test_utils.clj"
   "test/clj/{{sanitized}}/core_test.clj"  "test/clj/core_test.clj"})

(def frontend-files
  {"tailwind.config.js"                         "tailwind.config.js"
   "src/clj/{{sanitized}}/web/routes/pages.clj" "src/clj/web/routes/pages.clj"
   "src/clj/{{sanitized}}/web/pages/layout.clj" "src/clj/web/pages/layout.clj"
   "resources/public/css/screen.css"            "resources/public/css/screen.css"})

(def cljs-files
  {"package.json"    "package.json"
   "shadow-cljs.edn" "shadow-cljs.edn"

   "src/cljs/{{sanitized}}/core.cljs" "src/cljs/core.cljs"})

(defn render-files [files-map render-fn options]
  (apply ->files options (map
                          (fn [[dest-filename src-filename]]
                            [dest-filename (render-fn src-filename options)])
                          files-map)))

(defn resolve-feature-dependencies [features dependencies]
  (set (mapcat (fn [feature]
                 (if-let [feature-dependencies (get dependencies feature)]
                   (into [feature] (mapcat #(resolve-feature-dependencies % dependencies)
                                           [feature-dependencies]))
                   [feature]))
               features)))

(defn base
  [name & feature-params]
  (let [render  (renderer "base" render-template)
        options {:full-name             name
                 :name                  (project-name name)
                 :ns-name               (sanitize-ns name)
                 :sanitized             (name-to-path name)
                 :default-cookie-secret (rand-str 16)
                 :year                  (year)
                 :features              (resolve-feature-dependencies feature-params feature-dependencies)}
        options (merge
                 options
                 {:cljs?     (some #{"+cljs"} (:features options))
                  :frontend? (some #{"+frontend"} (:features options))})

        files (merge
               core-files
               (when (:frontend? options)
                 frontend-files)
               (when (:cljs? options)
                 cljs-files))]
    (println "Generating 'imborge/base' project using these features:"
             (:features options))
    (println "Options:")
    (clojure.pprint/pprint options)
    (render-files files render options)))
