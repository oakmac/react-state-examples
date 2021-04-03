(ns com.oakmac.react-state-examples.schema
  (:require
    [malli.core :as m]
    [taoensso.timbre :as timbre]))

; (def AppDb [:map])
(def AppDb int?)

(defn check-app-db-schema
  [app-db]
  (when ^boolean goog.DEBUG
    (when-not (m/validate AppDb app-db)
      (timbre/warn "app-db failed schema validation!")
      (timbre/warn app-db))))
