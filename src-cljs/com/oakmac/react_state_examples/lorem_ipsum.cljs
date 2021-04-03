(ns com.oakmac.react-state-examples.lorem-ipsum
  (:require
    [re-frame.core :as rf]
    [taoensso.timbre :as timbre]))

;; -----------------------------------------------------------------------------
;; Events

(def default-num-paragraphs 1)

(rf/reg-event-db
  ::init
  (fn [db _]
    (assoc db ::num-paragraphs default-num-paragraphs)))

(rf/reg-event-db
  ::add-paragraph
  (fn [db _]
    (update db ::num-paragraphs inc)))

(rf/reg-event-db
  ::remove-paragraph
  (fn [db _]
    ;; for testing our schema: allow negative number of paragraphs
    ; (update db ::num-paragraphs dec)

    ;; "normal" logic does not allow negative paragraphs
    (update db ::num-paragraphs (fn [n]
                                  (if (zero? n) 0 (dec n))))))

;; -----------------------------------------------------------------------------
;; Subscriptions

(rf/reg-sub
  ::num-paragraphs
  (fn [db _]
    (::num-paragraphs db)))

;; -----------------------------------------------------------------------------
;; Views

(def lorem-ipsum-txt "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus faucibus at magna sit amet tristique. Suspendisse ut varius dui, tincidunt semper sapien. Nullam bibendum eros lectus, eu posuere velit ultrices sed. Proin consectetur lacus nec metus ullamcorper, ac tempus felis eleifend. Donec eu euismod nisl. Morbi fringilla justo sit amet blandit dictum. Fusce sollicitudin ipsum ut mauris posuere pharetra. Praesent vitae elit nec ex placerat faucibus a in diam. Sed bibendum sit amet dui at facilisis. Vivamus vitae felis lacinia, gravida lectus id, placerat ipsum. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer feugiat, sem in interdum tempor, nisi enim mollis nibh, luctus laoreet massa ligula nec ex. ")

(defn LoremIpsum []
  (let [num-paragraphs @(rf/subscribe [::num-paragraphs])]
    [:section.content
      [:h1.title "Number of Lorem Ipsum paragraphs: " num-paragraphs]
      [:div.buttons
        [:button.button.is-info.is-medium
          {:on-click #(rf/dispatch [::add-paragraph])}
          "Add Paragraph"]
        [:button.button.is-info.is-medium
          {:on-click #(rf/dispatch [::remove-paragraph])}
          "Remove Paragraph"]]
      [:div.content
        (for [x (range num-paragraphs)]
          [:p {:key x} lorem-ipsum-txt])]]))
