(ns com.oakmac.react-state-examples.root
  (:require
    [com.oakmac.react-state-examples.fun-people :as fun-people :refer [FunPeople]]
    [com.oakmac.react-state-examples.hello-react :as hello-react :refer [HelloReact]]
    [com.oakmac.react-state-examples.login-form :as login-form :refer [LoginForm]]
    [com.oakmac.react-state-examples.lorem-ipsum :as lorem-ipsum :refer [LoremIpsum]]
    [com.oakmac.react-state-examples.tabs :as tabs :refer [Tabs]]
    [re-frame.core :as rf]
    [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; App Db

(def initial-login-form
  {:error-msg nil
   :info-msg nil
   :loading? false
   :password ""
   :username ""
   :logged-in? false})

(def initial-fun-people
   [{:name "Bill" :reason "he likes to party"}
    {:name "Billy" :reason "he parties harder than Bill"}
    {:name "William" :reason "he doesn't party at all"}])

(def initial-app-db
  {; :active-tab-id "TAB_HELLO_REACT"
   :login-form initial-login-form
   :fun-people initial-fun-people
   :reason-field ""
   :name-field ""})

;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-fx
  :global-init
  (fn [_ _]
    {:db initial-app-db
     :fx [[:dispatch [::tabs/init]]
          [:dispatch [::lorem-ipsum/init]]
          [:dispatch [::login-form/init]]
          [:dispatch [::fun-people/init]]]}))

;; TODO: clock component
; (defn Clock []
;   [:section.content
;     [:h1.title "Clock"]
;     [:div.box
;       "09:12:24"]])

;; -----------------------------------------------------------------------------
;; Views

(defn App
  "the Root component"
  []
  (let [active-tab-id @(rf/subscribe [::tabs/active-tab-id])]
    [:section.section
      [:div.container
        [Tabs]
        (case active-tab-id
          "TAB_HELLO_REACT" [HelloReact]
          "TAB_LOREM_IPSUM" [LoremIpsum]
          ; "TAB_CLOCK" [Clock]
          "TAB_LOGIN_FORM" [LoginForm]
          "TAB_FUN_PEOPLE" [FunPeople]
          (timbre/warn "Unknown tab-id:" active-tab-id))]]))
