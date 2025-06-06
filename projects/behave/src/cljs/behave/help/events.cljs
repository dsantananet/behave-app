(ns behave.help.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
 :help/highlight-section
 (fn [_ [_ help-key]]
   {:fx                    [[:dispatch [:state/set :help-current-highlighted-key help-key]]]
    :help/scroll-into-view help-key}))

(rf/reg-fx
 :help/scroll-into-view
 (fn [help-key]
   (when-let [content (first (.getElementsByClassName js/document "help-area__content"))]
     (when-let [section (.getElementById js/document help-key)]
       (let [buffer (* 0.05 (.-offsetHeight content))
             top    (- (.-offsetTop section) (.-offsetTop content) buffer)]
         (.scroll content #js {:top top :behavior "smooth"}))))))

(rf/reg-event-db
 :help/scroll-top
 (fn [db]
   (when-not (get-in db [:state :help-area :hidden?])
     (let [content (first (.getElementsByClassName js/document "help-area__content"))]
       (.scroll content #js {:top 0 :behavior "smooth"})))))

(rf/reg-event-db
 :help/select-tab
 (fn [db [_ new-tab]]
   (assoc-in db [:state :help-tab] (:tab new-tab))))

(rf/reg-event-db
 :help/open-image-viewer
 (fn [db [_ url alt]]
   (assoc-in db
             [:state :help-area :image-modal]
             {:title "Help Image" :src url :alt alt})))

(rf/reg-event-db
 :help/open-table-viewer
 (fn [db [_ table]]
   (assoc-in db
             [:state :help-area :table-modal]
             table)))
