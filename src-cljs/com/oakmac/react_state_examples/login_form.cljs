(ns com.oakmac.react-state-examples.login-form
  (:require
    [clojure.string :as str]
    [re-frame.core :as rf]
    [oops.core :refer [ocall oget]]
    [taoensso.timbre :as timbre]))

(def initial-login-form
  {:error-msg nil
   :info-msg nil
   :loading? false
   :password ""
   :username ""
   :logged-in? false})

;; -----------------------------------------------------------------------------
;; Effects

(rf/reg-fx
  :wait-login
  (fn []
    (js/setTimeout
      (fn []
        (rf/dispatch [:login-success]))
      (+ 100 (rand-int 500)))))

;; -----------------------------------------------------------------------------
;; Events

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

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :login-form
  (fn [db _]
    (:login-form db)))

;; -----------------------------------------------------------------------------
;; Views


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

(defn LoginForm []
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
