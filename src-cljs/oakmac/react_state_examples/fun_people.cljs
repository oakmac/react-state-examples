(ns oakmac.react-state-examples.fun-people
  (:require
    [clojure.string :as str]
    [oops.core :refer [ocall oget oset!]]
    [re-frame.core :as rf]
    [reagent.core :as reagent]
    [taoensso.timbre :as timbre]))

(defn- by-id [id]
  (js/document.getElementById id))

;; -----------------------------------------------------------------------------
;; Events

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(rf/reg-event-db
  :remove-fun-person
  (fn [db [_ idx]]
    (update-in db [:fun-people] vec-remove idx)))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  :fun-people
  (fn [db _]
    (:fun-people db)))

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

(defn PersonBox [idx {:keys [name reason]}]
  [:div.column.is-4 {:key idx}
    [:div.box.content.is-medium
      [:p (str name " is fun because " reason ".")]
      [:button.button.is-link {:on-click #(rf/dispatch [:remove-fun-person idx])}
        (str "Remove " name)]]])

(defn Column [idx names]
  [:div.columns {:key idx}
    (map-indexed PersonBox names)])

(defn FunPeople []
  (let [fun-people @(rf/subscribe [:fun-people])
        name-field @(rf/subscribe [:name-field])
        reason-field @(rf/subscribe [:reason-field])
        submittable? (and (not (str/blank? name-field))
                          (not (str/blank? reason-field)))]
    [:section.content
      [:h1.title "List of Fun People"]

      [:div.box
        [:form {:on-submit (fn [js-evt]
                             (ocall js-evt "preventDefault")
                             (rf/dispatch [:add-fun-person])
                             (ocall (by-id "nameInput") "focus"))}
          [:div.field
            [:label.label "Name"]
            [:div.control
              [:input.input.is-medium {:type "text"
                                       :value name-field
                                       :id "nameInput"
                                       :on-change #(rf/dispatch [:update-name-field (oget % "currentTarget.value")])}]]]
          [:div.field
            [:label.label "Reason they are fun"]
            [:div.control
              [:input.input.is-medium {:type "text"
                                       :value reason-field
                                       :on-change #(rf/dispatch [:update-reason-field (oget % "currentTarget.value")])}]]]
          [:div.field
            [:input.button.is-link.is-medium
              (merge {:type "submit"
                      :value "Add Fun Person"}
                     (when-not submittable?
                       {:class "is-disabled"
                        :disabled true}))]]]]

      (if (empty? fun-people)
        [:div.content.is-large
          [:p "Unfortunately, no one is fun :("]]
        (map-indexed Column (partition-all 3 fun-people)))]))
