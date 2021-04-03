(ns com.oakmac.react-state-examples.tabs
  (:require
    [clojure.string :as str]
    [re-frame.core :as rf]
    [re-frame.interceptor :as interceptor]
    [re-frame.std-interceptors :as std-interceptors]
    [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

(def initial-active-tab-id "TAB_HELLO_REACT")

(rf/reg-event-db
  ::init
  (fn [db _]
    (assoc db ::active-tab-id initial-active-tab-id)))

;; NOTE: could also check this via schema or regex
(defn- valid-tab-id? [id]
  (and (string? id)
       (str/starts-with? id "TAB_")))

(def check-tab-id
  (interceptor/->interceptor
    :id :check-tab-id
    :before (fn [context]
              (when ^boolean goog.DEBUG
                (let [event (get-in context [:coeffects :event])
                      new-tab-id (nth event 1)]
                  (when-not (valid-tab-id? new-tab-id)
                    (timbre/warn "Uh-oh! Bad tab-id:" new-tab-id))))
              context)))

(rf/reg-event-db
  ::set-active-tab
  [check-tab-id] ;; <-- small interceptor to check the tab-id value
  (fn [db [_ tab-id]]
    (assoc db ::active-tab-id tab-id)))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  ::active-tab-id
  (fn [db _]
    (::active-tab-id db)))

;; -----------------------------------------------------------------------------
;; Views

(defn Tab [{:keys [active? id label]}]
  [:li {:class (when active? "is-active")
        :on-click #(rf/dispatch [::set-active-tab id])}
    [:a label]])

(defn Tabs []
  (let [active-tab-id @(rf/subscribe [::active-tab-id])]
    [:div.tabs.is-boxed.is-medium
      [:ul
        [Tab {:active? (= active-tab-id "TAB_HELLO_REACT")
              :label "Hello React"
              :id "TAB_HELLO_REACT"}]
        [Tab {:active? (= active-tab-id "TAB_LOREM_IPSUM")
              :label "Lorem Ipsum"
              :id "TAB_LOREM_IPSUM"}]
        [Tab {:active? (= active-tab-id "TAB_LOGIN_FORM")
              :label "Login Form"
              :id "TAB_LOGIN_FORM"}]
        ;; TODO: write the Clock component
        ; [Tab {:active? (= active-tab-id "TAB_CLOCK")
        ;       :label "Clock"
        ;       :id "TAB_CLOCK"}]
        [Tab {:active? (= active-tab-id "TAB_FUN_PEOPLE")
              :label "Fun People"
              :id "TAB_FUN_PEOPLE"}]
        [Tab {:active? (= active-tab-id :invalid-tab-id)
              :label "Invalid Tab"
              :id :invalid-tab-id}]]]))
