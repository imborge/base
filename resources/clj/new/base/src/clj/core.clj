(ns <<ns-name>>.core
  (:require
   [clojure.tools.logging :as log]
   [integrant.core :as ig]
   [<<ns-name>>.config :as config]
   [<<ns-name>>.env :refer [defaults]]
   [<<ns-name>>.db.conman]
   [<<ns-name>>.db.migratus]
   [<<ns-name>>.db.postgres]
   [<<ns-name>>.web.undertow]
   [<<ns-name>>.web.handler]
   [<<ns-name>>.web.routes.api]))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what :uncaught-exception
                 :exception ex
                 :where (str "Uncaught exception on" (.getName thread))}))))

(defonce system (atom nil))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (ig/halt!))
  (shutdown-agents))

(defn start-app [& [params]]
  ((or (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (ig/prep)
       (ig/init)
       (reset! system))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& _args]
  (start-app))
