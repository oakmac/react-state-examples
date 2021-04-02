(ns com.oakmac.react-state-examples.tabs
  (:require
    [re-frame.core :as rf]
    [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-db
  ::set-active-tab
  (fn [db [_ tab-id]]
    (assoc db :active-tab-id tab-id)))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :active-tab-id
  (fn [db _]
    (:active-tab-id db)))

;; -----------------------------------------------------------------------------
;; Views

(defn Tab [{:keys [active? id label]}]
  [:li {:class (when active? "is-active")
        :on-click #(rf/dispatch [::set-active-tab id])}
    [:a label]])

(defn Tabs []
  (let [active-tab-id @(rf/subscribe [:active-tab-id])]
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
              :id "TAB_FUN_PEOPLE"}]]]))
