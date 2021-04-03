(ns com.oakmac.react-state-examples.schema
  (:require
    [malli.core :as m]
    [malli.error :as me]
    [taoensso.timbre :as timbre]))

;; different schema libraries:
;; - plumatic Schema
;; - clojure.spec
;; - malli

(def NumParagraphs
  [:com.oakmac.react-state-examples.lorem-ipsum/num-paragraphs [:and int? [:>= 0]]])
  ; [:num-paragraphs [:and int? [:> 0]]])

(def ActiveTab
  [:com.oakmac.react-state-examples.tabs/active-tab-id [:re #"^TAB_[A-Z0-9_]+$"]])
  ; [:active-tab-id [:re #"^TAB_[A-Z0-9_]+$"]])

(def AppDb
  [:map
   NumParagraphs
   ActiveTab])

(defn check-app-db-schema
  [app-db]
  (when ^boolean goog.DEBUG
    (when-not (m/validate AppDb app-db)
      (timbre/warn (-> AppDb
                       (m/explain app-db)
                       (me/humanize))))))
      ; (timbre/warn "app-db failed schema validation!")
      ; (timbre/warn app-db))))
