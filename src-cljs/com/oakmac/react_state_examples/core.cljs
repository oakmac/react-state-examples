(ns com.oakmac.react-state-examples.core
  (:require
    [com.oakmac.react-state-examples.root :refer [App]]
    [goog.dom :as gdom]
    [goog.functions :as gfunctions]
    [oops.core :refer [ocall]]
    [re-frame.core :as rf]
    [reagent.dom :as rdom]
    [taoensso.timbre :as timbre]))

(def app-container-el (gdom/getElement "root"))

(defn on-refresh
  "Forces a Reagent re-render of all components.
   NOTE: this function is called after every shadow-cljs hot module reload"
  []
  (rf/clear-subscription-cache!)
  (rdom/force-update-all))

(def init!
  (gfunctions/once
    (fn []
      (timbre/info "Initializing React UI Examples")
      (rf/dispatch-sync [:global-init])
      (rdom/render [(var App)] app-container-el))))

(ocall js/window "addEventListener" "load" init!)
