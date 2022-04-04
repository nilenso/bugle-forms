(ns bugle-forms.fixtures
  (:require
   [aero.core :as aero]
   [bugle-forms.app :as app]
   [bugle-forms.config :as config]
   [bugle-forms.db.connection :as db]
   [bugle-forms.migrations :as migrations]
   [clojure.java.io :as io]
   [mount.core :as mount]
   [next.jdbc :as jdbc]))

(defn setup-config [tests]
  (mount/stop #'config/config)
  (-> (mount/except #{#'app/server})
      (mount/swap {#'config/config (aero/read-config
                                    (io/resource "config.edn")
                                    {:profile :test})})
      mount/start)
  (tests)
  (mount/stop #'config/config))

(defn setup-db [tests]
  (mount/stop #'db/datasource)
  (mount/start #'db/datasource)
  (db/drop-db)
  (migrations/migrate)
  (try (tests)
       (finally (db/drop-db)))
  (mount/stop #'db/datasource))

(defn clear-db [tests]
  (jdbc/with-transaction [tx db/datasource]
    (let [opts (assoc (:options db/datasource) :rollback-only true)
          tx-opts (jdbc/with-options tx opts)]
      (with-redefs [db/datasource tx-opts]
        (tests)))))
