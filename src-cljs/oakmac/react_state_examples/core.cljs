(ns oakmac.react-state-examples.core
  (:require
    [clojure.string :as str]
    [goog.functions :as gfunctions]
    [oakmac.react-state-examples.fun-people :as fun-people]
    [oops.core :refer [ocall oget oset!]]
    [re-frame.core :as rf]
    [reagent.core :as reagent]
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
  {:tab :hello-react
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
  :update-tab
  (fn [db [_ new-tab]]
    (assoc db :tab new-tab)))






(rf/reg-event-db
  :add-paragraph
  (fn [db [_ new-tab]]
    (update-in db [:lorem-ipsum] inc)))

(defn dec-not-zero [n]
  (if (zero? n)
    n
    (dec n)))

(rf/reg-event-db
  :remove-paragraph
  (fn [db [_ new-tab]]
    (update-in db [:lorem-ipsum] dec-not-zero)))











(rf/reg-event-db
  :update-name-field
  (fn [db [_ name]]
    (assoc db :name-field name)))

(rf/reg-event-db
  :update-reason-field
  (fn [db [_ reason]]
    (assoc db :reason-field reason)))

(rf/reg-event-db
  :add-fun-person
  (fn [db [_ reason]]
    (let [new-fun-person {:name (:name-field db)
                          :reason (:reason-field db)}]
      (-> db
          (update-in [:names] conj new-fun-person)
          (assoc :name-field ""
                 :reason-field "")))))














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
  :lorem-ipsum
  (fn [db _]
    (:lorem-ipsum db)))

(rf/reg-sub
  :login-form
  (fn [db _]
    (:login-form db)))

(rf/reg-sub
  :names
  (fn [db _]
    (:names db)))

(rf/reg-sub
  :name-field
  (fn [db _]
    (:name-field db)))

(rf/reg-sub
  :reason-field
  (fn [db _]
    (:reason-field db)))

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











(def lorem-ipsum "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus faucibus at magna sit amet tristique. Suspendisse ut varius dui, tincidunt semper sapien. Nullam bibendum eros lectus, eu posuere velit ultrices sed. Proin consectetur lacus nec metus ullamcorper, ac tempus felis eleifend. Donec eu euismod nisl. Morbi fringilla justo sit amet blandit dictum. Fusce sollicitudin ipsum ut mauris posuere pharetra. Praesent vitae elit nec ex placerat faucibus a in diam. Sed bibendum sit amet dui at facilisis. Vivamus vitae felis lacinia, gravida lectus id, placerat ipsum. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer feugiat, sem in interdum tempor, nisi enim mollis nibh, luctus laoreet massa ligula nec ex. ")

(defn LoremIpsum []
  (let [num-paragraphs @(rf/subscribe [:lorem-ipsum])]
    [:section.content

      [:h1.title "Number of Lorem Ipsum paragraphs: " num-paragraphs]
      [:div.buttons
        [:button.button.is-info.is-medium
          {:on-click #(rf/dispatch [:add-paragraph])}
          "Add Paragraph"]
        [:button.button.is-info.is-medium
          {:on-click #(rf/dispatch [:remove-paragraph])}
          "Remove Paragraph"]]
      [:div.content
        (for [x (range num-paragraphs)]
          [:p {:key x} lorem-ipsum])]]))












(defn Tab [{:keys [active? label kwd]}]
  [:li {:class (when active? "is-active")
        :on-click #(rf/dispatch [:update-tab kwd])}
    [:a label]])

(defn Tabs []
  (let [current-tab @(rf/subscribe [:tab])]
    [:div.tabs.is-boxed.is-medium
      [:ul
        [Tab {:active? (= current-tab :hello-react)
              :label "Hello React"
              :kwd :hello-react}]
        [Tab {:active? (= current-tab :lorem-ipsum)
              :label "Lorem Ipsum"
              :kwd :lorem-ipsum}]
        [Tab {:active? (= current-tab :login-form)
              :label "Login Form"
              :kwd :login-form}]
        ; [Tab {:active? (= current-tab :clock)
        ;       :label "Clock"
        ;       :kwd :clock}]]]))
        [Tab {:active? (= current-tab :names)
              :label "Fun People"
              :kwd :names}]]]))

(defn App
  []
  (let [current-tab @(rf/subscribe [:tab])]
    [:section.section
      [:div.container
        [Tabs]
        (case current-tab
          :clock [Clock]
          :lorem-ipsum [LoremIpsum]
          :hello-react [HelloReact]
          :login-form [LoginFormPage]
          :names [fun-people/FunPeople]
          nil)]]))

;; -----------------------------------------------------------------------------
;; Init

(def app-container-el (js/document.getElementById "root"))

(defn re-render
  "Forces a Reagent re-render of all components.
   NOTE: this function is called after every shadow-cljs hot module reload"
  []
  (reagent/force-update-all))

(def init!
  (gfunctions/once
    (fn []
      (rf/dispatch-sync [:init])
      (when-not (str/blank? (:master-password initial-db-state))
        (rf/dispatch [:submit-master-password]))
      (reagent/render [App] app-container-el))))

(init!)
