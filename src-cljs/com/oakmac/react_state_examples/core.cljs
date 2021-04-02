(ns com.oakmac.react-state-examples.core
  (:require
    [clojure.string :as str]
    [com.oakmac.react-state-examples.fun-people :as fun-people]
    [com.oakmac.react-state-examples.tabs :refer [Tabs]]
    [com.oakmac.react-state-examples.lorem-ipsum :refer [LoremIpsum]]
    [goog.dom :as gdom]
    [goog.functions :as gfunctions]
    [oops.core :refer [ocall oget]]
    [re-frame.core :as rf]
    [reagent.dom :as rdom]
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

(def initial-db-state
  {:active-tab-id "TAB_HELLO_REACT"
   :login-form initial-login-form
   :fun-people initial-fun-people
   :lorem-ipsum 2
   :reason-field ""
   :name-field ""})

;; -----------------------------------------------------------------------------
;; Events

(rf/reg-event-db
  :init
  (fn [_ _]
    initial-db-state))


























(rf/reg-event-db
  :reset-login-form
  (fn [db _]
    (assoc db :login-form initial-login-form)))

(rf/reg-event-db
  :update-login-form
  (fn [db [_ new-form]]
    (update-in db [:login-form] merge new-form)))

(rf/reg-event-db
  :login-success
  (fn [db [_ new-form]]
    (assoc-in db [:login-form :logged-in?] true)))

(rf/reg-event-fx
  :submit-login-form
  (fn [{:keys [db]} _]
    (let [login-form (:login-form db)
          {:keys [password username]} login-form]
      (cond
        (str/blank? username)
        {:db (update-in db [:login-form] merge {:loading? false
                                                :error-msg "Please enter a username."})}

        (str/blank? password)
        {:db (update-in db [:login-form] merge {:loading? false
                                                :error-msg "Please enter a password."})}

        :else
        (merge
          {:db (update-in db [:login-form] merge {:error-msg nil
                                                  :info-msg nil
                                                  :loading? true})
           :wait-login nil})))))

(rf/reg-fx
  :wait-login
  (fn []
     (js/setTimeout
       (fn []
         (rf/dispatch [:login-success]))
       2500)))




;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :tab
  (fn [db _]
    (:tab db)))



(rf/reg-sub
  :login-form
  (fn [db _]
    (:login-form db)))


;; -----------------------------------------------------------------------------
;; Views

(defn HelloReact []
  [:section.content
    [:h1.title "Hello React!"]
    [:p "React makes it painless to create interactive UIs. Design simple views for each state in your application, and React will efficiently update and render just the right components when your data changes."]
    [:p "Declarative views make your code more predictable and easier to debug."]])










;; TODO: clock component
(defn Clock []
  [:section.content
    [:h1.title "Clock"]
    [:div.box
      "09:12:24"]])

















(defn PasswordInput
  [password loading?]
  [:div.field
    [:label.label "Password"]
    [:div.control
      [:input.input.is-medium
        {:disabled loading?
         :on-change #(rf/dispatch [:update-login-form {:password (oget % "currentTarget.value")}])
         :placeholder "Password"
         :type "password"
         :value password}]]])

(defn UsernameInput
  [username loading?]
  [:div.field
    [:label.label "Username"]
    [:div.control
      [:input#usernameInput.input.is-medium
        {:disabled loading?
         :on-change (fn [js-evt]
                      (rf/dispatch [:update-login-form {:username (oget js-evt "currentTarget.value")}]))
         :placeholder "Username"
         :type "text"
         :value username}]]])

(defn LoginForm2
  []
  (let [form @(rf/subscribe [:login-form])
        {:keys [error-msg info-msg loading? username password logged-in?]} form]
    [:form {:on-submit (fn [js-evt]
                         (ocall js-evt "preventDefault")
                         (rf/dispatch [:submit-login-form]))}
      (when-not (str/blank? info-msg)
        [:div.notification.is-info
          [:button.delete]
          info-msg])
      (when-not (str/blank? error-msg)
        [:div.notification.is-danger
          [:button.delete]
          error-msg])
      [UsernameInput username loading?]
      [PasswordInput password loading?]
      [:div.field
        [:div.control
          [:button.button.is-primary.is-medium
            {:class (when loading? "is-loading")
             :type "submit"}
            "Login"]]]]))

(defn LoggedInSuccess []
  [:div.columns.is-centered
    [:div.column.is-half
      [:div.box
        [:h1.title "Login Success!"]
        [:button.button.is-primary.is-medium
          {:on-click #(rf/dispatch [:reset-login-form])}
          "Reset Login Form"]]]])

(defn LoginFormPage []
  (let [{:keys [logged-in?]} @(rf/subscribe [:login-form])]
    [:section.content
      [:div {:style {:height "40px"}}]
      (if logged-in?
        [LoggedInSuccess]
        [:div.columns.is-centered
          [:div.column.is-half
            [:div.box
              [:h1.title "Login"]
              [LoginForm2]]]])]))





























(defn App
  "the Root component"
  []
  (let [active-tab-id @(rf/subscribe [:active-tab-id])]
    [:section.section
      [:div.container
        [Tabs]
        (case active-tab-id
          "TAB_HELLO_REACT" [HelloReact]
          "TAB_LOREM_IPSUM" [LoremIpsum]
          "TAB_CLOCK" [Clock]
          "TAB_LOGIN_FORM" [LoginFormPage]
          "TAB_FUN_PEOPLE" [fun-people/FunPeople]
          (timbre/warn "Unknown tab-id:" active-tab-id))]]))

;; -----------------------------------------------------------------------------
;; Global Init

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
      (rf/dispatch-sync [:init])
      (rdom/render [(var App)] app-container-el))))

(init!)
