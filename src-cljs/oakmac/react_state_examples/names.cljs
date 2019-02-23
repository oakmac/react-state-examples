(ns oakmac.react-state-examples.names
  (:require
    [clojure.string :as str]
    [oops.core :refer [ocall oget oset!]]
    [re-frame.core :as rf]
    [reagent.core :as reagent]
    [taoensso.timbre :as timbre]))

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

(defn NameRow [idx {:keys [name reason]}]
  [:div.box.field {:key idx}
    [:div.content
      [:p (str name " is fun because " reason ".")]
      [:button.card-footer-item.button.is-text {:on-click #(rf/dispatch [:remove-fun-person idx])}
        "Remove"]]])

(defn Names []
  (let [names @(rf/subscribe [:names])
        name-field @(rf/subscribe [:name-field])
        reason-field @(rf/subscribe [:reason-field])
        submittable? (and (not (str/blank? name-field))
                          (not (str/blank? reason-field)))]
    [:section.content
      [:h1.title "List of Fun People"]

      [:div.box
        [:div.field
          [:label.label "Name"]
          [:div.control
            [:input.input.is-medium {:type "text"
                                     :value name-field
                                     :on-change #(rf/dispatch [:update-name-field (oget % "currentTarget.value")])}]]]
        [:div.field
          [:label.label "Reason they are fun"]
          [:div.control
            [:input.input.is-medium {:type "text"
                                     :value reason-field
                                     :on-change #(rf/dispatch [:update-reason-field (oget % "currentTarget.value")])}]]]
        [:div.field
          [:button.button.is-link.is-medium
            (if submittable?
              {:on-click #(rf/dispatch [:add-fun-person])}
              {:class "is-disabled"
               :disabled true})
            "Add Fun Person"]]]

      (if (empty? names)
        [:p "No one is fun!"]
        (map-indexed NameRow (partition-all 3 names)))]))
